package yokwe.security.japan.jpx;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;

import yokwe.util.FileUtil;
import yokwe.util.ScrapeUtil;

public class StockPage {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(StockPage.class);
	
	public static final String PATH_DIR_DATA = "tmp/download/page";
	
	public static File getPageFile(String stockCode) {
		return new File(String.format("%s/%s", PATH_DIR_DATA, stockCode));
	}
	public static String getPageURL(String stockCode) {
		String stockCode4 = Stock.toStockCode4(stockCode);
		return String.format("https://quote.jpx.co.jp/jpx/template/quote.cgi?F=tmp/stock_detail&MKTN=T&QCODE=%s", stockCode4);
	}

	public static final String NO_INFORMATION = "指定された銘柄が見つかりません";
	
	// 会社基本情報  コード ISINコード 業種 所属部
	public static class CompanyInfo {
		public static final Pattern PAT = Pattern.compile(
				"<tr>\\s+" +
				"<td .+?><font .+?>コード</font></td>\\s+" +
				"<td .+?><font .+?>ISINコード</font></td>\\s+" +
				"<td .+?><font .+?>業種</font></td>\\s+" +
				"<td .+?><font .+?>所属部</font></td>\\s+" +
				"</tr>\\s+" +
				"<tr>\\s+" +
				"<td .+?><p><font .+?>(?<code>.+?)<br></font></p></td>\\s+" +
				"<td .+?><font .+?>(?<isin>[A-Z0-9]+)<br></font></td>\\s+" +
				"<td .+?><font .+?>(?<industry>.+?)<br></font></td>\\s+" +
				"<td .+?><font .+?>(?<category>.+?)<br></font></td>\\s+" +
				"</tr>"
		);
		public static CompanyInfo getInstance(String page) {
			return ScrapeUtil.get(CompanyInfo.class, PAT, page);
		}

		public final String code;
		public final String isin;
		public final String industry;
		public final Optional<String> category;
		
		public CompanyInfo(String code, String isin, String industry, Optional<String> category) {
			this.code     = code;
			this.isin     = isin;
			this.industry = industry;
			this.category = category;
		}
		
		@Override
		public String toString() {
			return String.format("{%s %s %s %s}", code, isin, industry, category);
		}
	}

	// 現在値
	public static class CurrentPriceTime {
		public static final Pattern PAT = Pattern.compile(
			"<td .+?><b>現在値 \\(時刻\\)</b></td>\\s*" +
			"<td .+?><b>(?<price>[0-9,.]*) \\((?<time>.+?)\\)</b></td>\\s*"
		);
		public static CurrentPriceTime getInstance(String page) {
			return ScrapeUtil.get(CurrentPriceTime.class, PAT, page);
		}

		@ScrapeUtil.AsNumber
		public final Optional<String> price;
		public final Optional<String> time;
		
		public CurrentPriceTime(Optional<String> price, Optional<String> time) {
			this.price = price;
			this.time  = time;
		}
		
		@Override
		public String toString() {
			return String.format("{%s,%s}", price, time);
		}
	}

	// 売り気配値
	public static class SellPriceTime {
		public static final Pattern PAT = Pattern.compile(
			"<td .+?><font .+?>売り気配値 \\(時刻\\)</font></td>\\s*" +
			"<td .+?><font .+?>(?<price>.+?) \\((?<time>.+?)\\)</font></td>\\s*"
		);
		public static SellPriceTime getInstance(String page) {
			return ScrapeUtil.get(SellPriceTime.class, PAT, page);
		}

		@ScrapeUtil.AsNumber
		public final Optional<String> price;
		public final Optional<String> time;
		
		public SellPriceTime(Optional<String> price, Optional<String> time) {
			this.price = price;
			this.time  = time;
		}
		
		@Override
		public String toString() {
			return String.format("{%s,%s}", price, time);
		}
	}

	// 買い気配値
	public static class BuyPriceTime {
		public static final Pattern PAT = Pattern.compile(
			"<td .+?><font .+?>買い気配値 \\(時刻\\)</font></td>\\s*" +
			"<td .+?><font .+?>(?<price>.+?) \\((?<time>.+?)\\)</font></td>\\s*"
		);
		public static BuyPriceTime getInstance(String page) {
			return ScrapeUtil.get(BuyPriceTime.class, PAT, page);
		}

		@ScrapeUtil.AsNumber
		public final Optional<String> price;
		public final Optional<String> time;
		
		public BuyPriceTime(Optional<String> price, Optional<String> time) {
			this.price = price;
			this.time  = time;
		}
		
		@Override
		public String toString() {
			return String.format("{%s,%s}", price, time);
		}
	}
	
	// 始値
	public static class OpenPrice {
		public static final Pattern PAT = Pattern.compile(
			"<td .+?><font .+?>始値</font></td>\\s*" +
			"<td .+?><font .+?>(?<value>.*?)<br></font></td>\\s*"
		);
		public static OpenPrice getInstance(String page) {
			return ScrapeUtil.get(OpenPrice.class, PAT, page);
		}

		@ScrapeUtil.AsNumber
		public final Optional<String> value;
		
		public OpenPrice(Optional<String> value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.format("{%s}", value);
		}
	}

	// 前日終値
	public static class LastClosePrice {
		public static final Pattern PAT = Pattern.compile(
			"<td .+?><font .+?>前日終値</font></td>\\s*" +
			"<td .+?><font .+?>(?<value>.*?)<br></font></td>\\s*"
		);
		public static LastClosePrice getInstance(String page) {
			return ScrapeUtil.get(LastClosePrice.class, PAT, page);
		}

		@ScrapeUtil.AsNumber
		public final Optional<String> value;
		
		public LastClosePrice(Optional<String> value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.format("{%s}", value);
		}
	}

	// 高値
	public static class HighPrice {
		public static final Pattern PAT = Pattern.compile(
			"<td .+?><font .+?>高値</font></td>\\s*" +
			"<td .+?><font .+?>(?<value>.*?)<br></font></td>\\s*"
		);
		public static HighPrice getInstance(String page) {
			return ScrapeUtil.get(HighPrice.class, PAT, page);
		}

		@ScrapeUtil.AsNumber
		public final Optional<String> value;
		
		public HighPrice(Optional<String> value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.format("{%s}", value);
		}
	}
	
	// 安値
	public static class LowPrice {
		public static final Pattern PAT = Pattern.compile(
			"<td .+?><font .+?>安値</font></td>\\s*" +
			"<td .+?><font .+?>(?<value>.*?)<br></font></td>\\s*"
		);
		public static LowPrice getInstance(String page) {
			return ScrapeUtil.get(LowPrice.class, PAT, page);
		}

		@ScrapeUtil.AsNumber
		public final Optional<String> value;
		
		public LowPrice(Optional<String> value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.format("{%s}", value);
		}
	}

	// 売買高
	public static class TradeVolume {
		public static final Pattern PAT = Pattern.compile(
			"<td .+?><font .+?>売買高</font></td>\\s*" +
			"<td .+?><font .+?>(?<value>.*?)株<br></font></td>\\s*"
		);
		public static TradeVolume getInstance(String page) {
			return ScrapeUtil.get(TradeVolume.class, PAT, page);
		}
		
		@ScrapeUtil.AsNumber
		public final Optional<String> value;
		
		public TradeVolume(Optional<String> value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.format("{%s}", value);
		}
	}

	// 売買代金
	public static class TradeValue {
		public static final Pattern PAT = Pattern.compile(
			"<td .+?><font .+?>売買代金</font></td>\\s*" +
			"<td .+?><font .+?>(?<value>.*?)<br></font></td>\\s*"
		);
		public static TradeValue getInstance(String page) {
			return ScrapeUtil.get(TradeValue.class, PAT, page);
		}
		
		@ScrapeUtil.AsNumber
		public final Optional<String> value;
		
		public TradeValue(Optional<String> value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.format("{%s}", value);
		}
	}

	// 発行済株式数
	public static class Issued {
		public static final Pattern PAT = Pattern.compile(
			"<td .+?><font .+?>発行済株式数</font></td>\\s*" +
			"<td .+?><font .+?>(?<value>.*?)<br></font></td>\\s*"
		);
		public static Issued getInstance(String page) {
			return ScrapeUtil.get(Issued.class, PAT, page);
		}

		public final long value;
		
		public Issued(long value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.format("%d", value);
		}
	}

	// 売買単位
	public static class TradeUnit {
		public static final Pattern PAT = Pattern.compile(
			"<td .+?><font .+?>売買単位</font></td>\\s+" +
			"<td .+?><font .+?>(?<value>[0-9,]+)株<br></font></td>\\s+"
		);
		public static TradeUnit getInstance(String page) {
			return ScrapeUtil.get(TradeUnit.class, PAT, page);
		}
		
		public final int value;
		
		public TradeUnit(int value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return String.format("%d", value);
		}
	}

	// 日足ヒストリカル
	public static class PriceVolume {
		public static final Pattern PAT = Pattern.compile(
			"(?<yyyy>20[0-9][0-9])/(?<mm>[01][0-9])/(?<dd>[0123][0-9])," +
			"(?<open>([0-9]+\\.[0-9])?)," +
			"(?<high>([0-9]+\\.[0-9])?)," +
			"(?<low>([0-9]+\\.[0-9])?)," +
			"(?<close>([0-9]+\\.[0-9])?)," +
			"(?<volume>[0-9]+)," +
			"([0-9]+)?" +
			"[\\r\\n]+"
		);
		public static List<PriceVolume> getInstance(String page) {
			return ScrapeUtil.getList(PriceVolume.class, PAT, page);
		}
		
		public final String yyyy;
		public final String mm;
		public final String dd;
		
		@ScrapeUtil.AsNumber
		public final Optional<String> open;
		@ScrapeUtil.AsNumber
		public final Optional<String> high;
		@ScrapeUtil.AsNumber
		public final Optional<String> low;
		@ScrapeUtil.AsNumber
		public final Optional<String> close;
		public final long             volume;
		
		public PriceVolume(String yyyy, String mm, String dd, Optional<String> open, Optional<String> high, Optional<String> low, Optional<String> close, long volume) {
			this.yyyy   = yyyy;
			this.mm     = mm;
			this.dd     = dd;
			this.open   = open;
			this.high   = high;
			this.low    = low;
			this.close  = close;
			this.volume = volume;
		}
		
		public String getDate() {
			return String.format("%s-%s-%s", yyyy, mm, dd);
		}
		
		@Override
		public String toString() {
			return String.format("{%s %s %s %s %s %s %s %s}", yyyy, mm, dd, open, high, low, close, volume);
		}
	}

	public static void main(String[] args) {
		logger.info("START");
		
		{
			for(File file: FileUtil.listFile(PATH_DIR_DATA)) {
				String string = FileUtil.read().file(file);
				if (string.contains(NO_INFORMATION)) continue;

				logger.info("file  {}", file.getName());
				logger.info("  companyInfo      {}", CompanyInfo.getInstance(string));
				logger.info("  currentPriceTime {}", CurrentPriceTime.getInstance(string));
				logger.info("  sellPrcieTime    {}", SellPriceTime.getInstance(string));
				logger.info("  buyPriceTime     {}", BuyPriceTime.getInstance(string));
				logger.info("  openPrice        {}", OpenPrice.getInstance(string));
				logger.info("  highPrice        {}", HighPrice.getInstance(string));
				logger.info("  lastClosePrice   {}", LastClosePrice.getInstance(string));
				logger.info("  lowPrice         {}", LowPrice.getInstance(string));
				logger.info("  tradeVolume      {}", TradeVolume.getInstance(string));
				logger.info("  tradeValue       {}", TradeValue.getInstance(string));
				logger.info("  tradeUnit        {}", TradeUnit.getInstance(string));
				logger.info("  issued           {}", Issued.getInstance(string));
				logger.info("  priceVolume      {}", PriceVolume.getInstance(string).size());

//				{
//					List<PriceVolume> list = PriceVolume.getInstance(string);
//					for(PriceVolume e: list) {
//						logger.info("    {}", e);
//					}
//				}
			}

		}
		logger.info("STOP");
	}
}
