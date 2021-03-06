package yokwe.security.japan.data;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.security.japan.jpx.Stock;
import yokwe.security.japan.jpx.StockPage;
import yokwe.security.japan.jpx.StockPage.BuyPriceTime;
import yokwe.security.japan.jpx.StockPage.CompanyInfo;
import yokwe.security.japan.jpx.StockPage.CurrentPriceTime;
import yokwe.security.japan.jpx.StockPage.HighPrice;
import yokwe.security.japan.jpx.StockPage.Issued;
import yokwe.security.japan.jpx.StockPage.LastClosePrice;
import yokwe.security.japan.jpx.StockPage.LowPrice;
import yokwe.security.japan.jpx.StockPage.OpenPrice;
import yokwe.security.japan.jpx.StockPage.PriceVolume;
import yokwe.security.japan.jpx.StockPage.SellPriceTime;
import yokwe.security.japan.jpx.StockPage.TradeUnit;
import yokwe.security.japan.jpx.StockPage.TradeValue;
import yokwe.security.japan.jpx.StockPage.TradeVolume;
import yokwe.util.JapanHoliday;
import yokwe.util.http.Download;
import yokwe.util.http.DownloadSync;
import yokwe.util.http.RequesterBuilder;
import yokwe.util.http.StringTask;
import yokwe.util.http.Task;

public class UpdateStockPrice {
	static final org.slf4j.Logger logger = LoggerFactory.getLogger(UpdateStockPrice.class);
	
	private static class Context {
		private List<StockPrice>               stockPriceList;
		private List<StockInfo>                stockInfoList;
		private Map<String, List<PriceVolume>> priceVolumeMap;
		private Integer                        buildCount;
				
		public void add(StockPrice newValue) {
			synchronized(stockPriceList) {
				stockPriceList.add(newValue);
			}
		}
		public void add(StockInfo newValue) {
			synchronized(stockInfoList) {
				stockInfoList.add(newValue);
			}
		}
		public void put(String key, List<PriceVolume> value) {
			synchronized(priceVolumeMap) {
				priceVolumeMap.put(key, value);
			}
		}

		public void incrementBuildCount() {
			synchronized(buildCount) {
				buildCount = buildCount + 1;
			}
		}
		public int getBuildCount() {
			int ret;
			synchronized(buildCount) {
				ret = buildCount;
			}
			return ret;
		}

		public Context() {
			this.stockPriceList = new ArrayList<>();
			this.stockInfoList  = new ArrayList<>();
			this.priceVolumeMap = new TreeMap<>();
			this.buildCount     = 0;
		}
	}
	
	private static final DateTimeFormatter FORMAT_HHMM = DateTimeFormatter.ofPattern("HH:mm");
	private static void buildContextFromPage(Context context, LocalDateTime dateTime, String stockCode, String page) {
		if (page.contains("指定された銘柄が見つかりません")) {
			//
		} else {
			CompanyInfo      companyInfo      = CompanyInfo.getInstance(page);
			TradeUnit        tradeUnit        = TradeUnit.getInstance(page);
			Issued           issued           = Issued.getInstance(page);
			CurrentPriceTime currentPriceTime = CurrentPriceTime.getInstance(page);
			BuyPriceTime     buyPriceTime     = BuyPriceTime.getInstance(page);
			SellPriceTime    sellPriceTime    = SellPriceTime.getInstance(page);
			OpenPrice        openPrice        = OpenPrice.getInstance(page);
			HighPrice        highPrice        = HighPrice.getInstance(page);
			LowPrice         lowPrice         = LowPrice.getInstance(page);
			TradeVolume      tradeVolume      = TradeVolume.getInstance(page);
			TradeValue       tradeValue       = TradeValue.getInstance(page);
			LastClosePrice   lastClosePrice   = LastClosePrice.getInstance(page);
			List<PriceVolume> priceVolumeList = PriceVolume.getInstance(page);
			
			// save for later use
			context.put(stockCode, priceVolumeList);

			// build stockInfoList
			{
				// String stockCode, String isin, int tradeUnit, long issued, String industry
				StockInfo stockInfo = new StockInfo(stockCode, companyInfo.isin, tradeUnit.value, issued.value);
				context.add(stockInfo);
			}

			String date = dateTime.toLocalDate().toString();
			String time = dateTime.toLocalTime().format(FORMAT_HHMM);
			
			String price     = currentPriceTime.price.orElse("");
			String priceTime = currentPriceTime.time.orElse("");
			
			String sell      = sellPriceTime.price.orElse("");
			String sellTime  = sellPriceTime.time.orElse("");
			
			String buy       = buyPriceTime.price.orElse("");
			String buyTime   = buyPriceTime.time.orElse("");
			
			String open      = openPrice.value.orElse("");
			String high      = highPrice.value.orElse("");
			String low       = lowPrice.value.orElse("");
			
			String volume    = tradeVolume.value.orElse("");
			String trade     = tradeValue.value.orElse("");
			
			String lastClose = lastClosePrice.value.orElse("");

			StockPrice stockPrice = new StockPrice(
					date,
					time,
					stockCode,
					
					price,
					priceTime,
					
					sell,
					sellTime,
					
					buy,
					buyTime,
					
					open,
					high,
					low,
					
					volume,
					trade,
					
					lastClose
				);
			context.add(stockPrice);
		}

	}

	private static class MyConsumer implements Consumer<String> {
		private final Context       context;
		private final String        stockCode;
		private final LocalDateTime dateTime;
		
		MyConsumer(Context context, String stockCode, LocalDateTime dateTime) {
			this.context   = context;
			this.stockCode = stockCode;
			this.dateTime  = dateTime;
		}
		@Override
		public void accept(String page) {
			context.incrementBuildCount();
			buildContextFromPage(context, dateTime, stockCode, page);			
		}
	}

	private static void buildContext(Context context) {
		int threadCount = 50;
		int maxPerRoute = 50;
		int maxTotal    = 100;
		int soTimeout   = 30;
		logger.info("threadCount {}", threadCount);
		logger.info("maxPerRoute {}", maxPerRoute);
		logger.info("maxTotal    {}", maxTotal);
		logger.info("soTimeout   {}", soTimeout);
		
		RequesterBuilder requesterBuilder = RequesterBuilder.custom()
				.setVersionPolicy(HttpVersionPolicy.NEGOTIATE)
				.setSoTimeout(soTimeout)
				.setMaxTotal(maxTotal)
				.setDefaultMaxPerRoute(maxPerRoute);

//		Download download = new DownloadAsync();
		Download download = new DownloadSync();
		
		download.setRequesterBuilder(requesterBuilder);
		
		// Configure custom header
		download.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit");
		download.setReferer("https://www.jpx.co.jp/");
		
		// Configure thread count
		download.setThreadCount(threadCount);

		List<Stock> stockList = Stock.getList();
		Collections.shuffle(stockList);
		final int stockListSize = stockList.size();
		
		LocalDateTime dateTime = LocalDateTime.now();
		
		for(Stock stock: stockList) {
			String stockCode = stock.stockCode;
			String uriString = StockPage.getPageURL(stockCode);
			
			Task task = StringTask.text(uriString, new MyConsumer(context, stockCode, dateTime));
			download.addTask(task);
		}

		logger.info("BEFORE RUN");
		download.startAndWait();
		logger.info("AFTER  RUN");
//		download.showRunCount();

		try {
			for(int i = 0; i < 10; i++) {
				int buildCount = context.getBuildCount();
				if (buildCount == stockListSize) break;
				logger.info("buildCount {} / {}", buildCount, stockListSize);
				Thread.sleep(1000);
			}
			{
				int buildCount = context.getBuildCount();
				if (buildCount != stockListSize) {
					logger.error("Unexpected");
					logger.error("  buildCount    {}", buildCount);
					logger.error("  stockListSize {}", stockListSize);
					throw new UnexpectedException("Unexpected");
				}
			}
			logger.info("AFTER  WAIT");
		} catch (InterruptedException e) {
			String exceptionName = e.getClass().getSimpleName();
			logger.error("{} {}", exceptionName, e);
			throw new UnexpectedException(exceptionName, e);
		}
	}
	private static void updatePrice(Context context) {
		// update price using list (StockPrice)
		int count       = 0;
		int countUpdate = 0;
		int countSkip   = 0;
		int countZero   = 0;
		int countTotal  = context.stockPriceList.size();
		for(StockPrice stockPrice: context.stockPriceList) {
			String date      = stockPrice.date;
			String stockCode = stockPrice.stockCode;
			
			if ((count % 1000) == 0) {
				logger.info("{}", String.format("%4d / %4d  %s", count, countTotal, stockCode));
			}
			count++;
			
			// Map of price of stock
			TreeMap<String, Price> priceMap = new TreeMap<>();
			//      date
			for(Price price: Price.getList(stockCode)) {
				if (JapanHoliday.isClosed(price.date)) continue;
				
				priceMap.put(price.date, price);
			}
			
			// Update priceMap with priceVolumeList
			//   Because if stock split, historical price will be adjusted.
			{
				double lastClose = -1;
				for(PriceVolume priceVolume: context.priceVolumeMap.get(stockCode)) {
					String           priceDate = priceVolume.getDate();
					Optional<String> open      = priceVolume.open;
					Optional<String> high      = priceVolume.high;
					Optional<String> low       = priceVolume.low;
					Optional<String> close     = priceVolume.close;
					long             volume    = priceVolume.volume;
					
					if (priceMap.containsKey(priceDate)) {
						Price price = priceMap.get(priceDate);
						
						if (volume == 0) {
							// no price
							if (lastClose != -1) {
								price.open   = lastClose;
								price.high   = lastClose;
								price.low    = lastClose;
								price.close  = lastClose;
								price.volume = 0;
							}
						} else {
							if (open.isPresent() && high.isPresent() && low.isPresent() && close.isPresent()) {
								price.open   = Double.parseDouble(open.get());
								price.high   = Double.parseDouble(high.get());
								price.low    = Double.parseDouble(low.get());
								price.close  = Double.parseDouble(close.get());
								price.volume = volume;
								
								priceMap.put(priceDate, price);
								lastClose = price.close;
							} else {
								logger.error("Unexpected");
								logger.error("  priceVolume {}", priceVolume);
								throw new UnexpectedException("Unexpected");
							}
						}
					}
				}
			}
			
			// update priceMap with data from StockPrice
			{
				double open;
				double high;
				double low;
				double close;
				long   volume;
				if (stockPrice.open.isEmpty() || stockPrice.high.isEmpty() || stockPrice.low.isEmpty() || stockPrice.price.isEmpty() || stockPrice.volume.isEmpty()) {
					// Cannot get lastPrice, skip to this entry
					if (priceMap.isEmpty()) {
						countSkip++;
						continue;
					}
					
					Price lastPrice;
					if (priceMap.containsKey(date)) {
						lastPrice = priceMap.get(date);
					} else {
						lastPrice = priceMap.lastEntry().getValue();
					}
					open   = lastPrice.close;
					high   = lastPrice.close;
					low    = lastPrice.close;
					close  = lastPrice.close;
					volume = stockPrice.volume.isEmpty() ? 0 : Long.parseLong(stockPrice.volume);
					countZero++;
				} else {
					open   = Double.parseDouble(stockPrice.open);
					high   = Double.parseDouble(stockPrice.high);
					low    = Double.parseDouble(stockPrice.low);
					close  = Double.parseDouble(stockPrice.price);
					volume = Long.parseLong(stockPrice.volume);
					countUpdate++;
				}

				if (!JapanHoliday.isClosed(date)) {
					Price price = new Price(date, stockCode, open, high, low, close, volume);
					// Over write old entry or add new entry
					priceMap.put(date, price);
				}
			}
			
			Price.save(priceMap.values());
		}
		
		logger.info("countTotal  {}", String.format("%4d", countTotal));
		logger.info("countSkip   {}", String.format("%4d", countSkip));
		logger.info("countZero   {}", String.format("%4d", countZero));
		logger.info("countUpdate {}", String.format("%4d", countUpdate));
	}

	private static void updateFiles() {
		Context context = new Context();
		
		buildContext(context);

		// Save data
		{
			List<StockPrice> list = context.stockPriceList;
			logger.info("save {} {}", StockPrice.PATH_FILE, list.size());
			StockPrice.save(list);
		}
		
		{
			List<StockInfo> list = context.stockInfoList;
			logger.info("save {} {}",StockInfo.PATH_FILE,  list.size());
			StockInfo.save(list);
		}

		// update each price file
		updatePrice(context);
	}
	
	public static void main(String[] args) throws IOException {
		logger.info("START");
		
		LocalDate today = LocalDate.now();
		if (JapanHoliday.isClosed(today)) {
			logger.warn("market is closed today");
		} else {
			updateFiles();
		}

		logger.info("STOP");
	}
}
