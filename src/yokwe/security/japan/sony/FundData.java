package yokwe.security.japan.sony;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.JsonObject;

import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.util.CSVUtil;
import yokwe.util.DoubleUtil;
import yokwe.util.HttpUtil;
import yokwe.util.json.JSONBase;

public class FundData extends JSONBase implements Comparable<FundData> {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FundData.class);

	public static final String PATH_FILE = "tmp/data/sony/sony-fund-data.csv";

	private static List<FundData> list = null;
	public static List<FundData> getList() {
		if (list == null) {
			list = CSVUtil.read(FundData.class).file(PATH_FILE);
			if (list == null) {
				list = new ArrayList<>();
			}
		}
		return list;
	}

	public static void save(Collection<FundData> collection) {
		save(new ArrayList<>(collection));
	}
	public static void save(List<FundData> list) {
		// Sort before save
		Collections.sort(list);
		CSVUtil.write(FundData.class).file(PATH_FILE, list);
	}

	public static enum Area {
		WORLD        ("01"),
		JAPAN        ("10"),
		NORTH_AMERICA("20"),
		EUROPE       ("30"),
		ASIA         ("40"),
		OCEANIA      ("50"),
		OTHER        ("60");
		
		public final String value;
		Area(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return name();
		}
		
		public static Area get(String value) {
			for(Area e: Area.values()) {
				if (e.value.equals(value)) return e;
			}
			logger.error("Unexpected value {}!", value);
			throw new UnexpectedException("Unexpected value");
		}
	}
	
	public static enum Target {
		STOCK   ("01"),
		BOND    ("10"),
		REIT    ("20"),
		BALANCE ("30"),
		COMODITY("40");
		
		public final String value;
		Target(String value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return name();
		}
		
		public static Target get(String value) {
			for(Target e: Target.values()) {
				if (e.value.equals(value)) return e;
			}
			logger.error("Unexpected value {}!", value);
			throw new UnexpectedException("Unexpected value");
		}
	}
	
	public static enum Company {
		ASAHILIFE("002", "朝日ライフ"),
		ASEMANE  ("004", "アセマネOne"),
		AMUNDI   ("006", "アムンディ"),
		EAST     ("009", "イーストS"),
		INVESCO  ("011", "インベスコ"),
		HSBC     ("012", "HSBC"),
		NN       ("014", "NN"),
		COMMONS  ("024", "コモンズ"),
		SCHRODER ("027", "シュローダー"),
		JPM      ("033", "JPモルガン"),
		JANAS    ("034", "ジャナス・キャピタル"),
		SOMPO    ("039", "SOMPO"),
		DAIWA    ("041", "大和"),
		TD       ("046", "T&D"),
		DEUTSCH  ("049", "ドイチェ"),
		NIKKOU   ("050", "日興"),
		NIKKOUAM ("051", "日興AMヨーロッパ"),
		NISSEI   ("052", "ニッセイ"),
		NOMURA   ("055", "野村"),
		BNP      ("061", "BNPパリバ"),
		PICTE    ("063", "ピクテ"),
		FIVESTAR ("065", "ファイブスター"),
		FEDILITY ("067", "フィデリティ"),
		BLACKROCK("070", "ブラックロック"),
		MANULIFE ("077", "マニュライフ"),
		SMDS     ("079", "三井住友DS"),
		SMTAM    ("080", "三井住友TAM"),
		UFJ      ("081", "三菱UFJ国際"),
		UBS      ("087", "UBS"),
		RAKUTEN  ("089", "楽天"),
		RHEOS    ("093", "レオス"),
		LM       ("094", "L・メイソン");
		
		public final String code;
		public final String name;
		Company(String code, String name) {
			this.code = code;
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
		
		public static Company get(String code) {
			for(Company e: Company.values()) {
				if (e.code.equals(code)) return e;
			}
			logger.error("Unexpected code {}!", code);
			throw new UnexpectedException("Unexpected value");
		}
	}
	
	public LocalDateTime dateTime;    // 2020-04-14 23:00:33
	public String  isinCode;           // IE0030804631
	//
    public double  bunpaiKinriFM;      // 14.97%
//  public String  bunpaiKinriRank;    // 6
    public String  categoryMeisyo;     // 国際REIT型
//  public String  flgFromSSeimei;     // 0
//  public String  flgHanbaiteisi;     // 0
//  public String  flgHRiskFund;       // 0
//  public String  flgSWOnly;          // 0
//  public String  fundAisyo;          // −
    public String  fundMei;            // ワールド・リート・オープン（毎月決算型）
//  public String  fundRyaku;          // ワールド・リート・オープン（毎月決算型）
//  public double  hanbaigakuFM;       // 4,359,962円
//  public String  hanbaigakuRanking;  // 127
    public double  hanbaiTesuryo;      // -
//  public String  hanbaiTesuryoFM;    // なし
    public String  hyokaKijyunbi;      // 2020年03月
    public long    jyunsisanEn;        // 142588
//  public String  jyunsisanEnFM;      // 142,588
//  public String  jyunsisanRank;      // 15
    public Company kanaCode;           // 081
    public int     kessanHindo;        // 12
//  public String  kessanHindoFM;      // 毎月
    public double  kijyunKagaku;       // 1403
//  public String  kijyunKagakuFM;     // 1,403円
//  public String  msCategoryCodeDai;  // 13
//  public String  nisaHanbaigakuFM;   // 696,961円
//  public String  nisaHanbaigakuRank; // 97
//  public String  returnRank;         // 221
//  public String  sbFundCode;         // 09111041
    public double  sintakHosyu;        // 1.705
//  public String  sintakHosyuFM;      // 1.705%
    public int     sougouRating;       // 2
//  public String  sougouRatingFM;     // ★★
    public Area    toshiArea;          // 01
    public Target  toshiTarget;        // 20
    public double  totalReturn;        // -33.36
//  public String  totalReturnFM;      // -33.36%
    public String  tukaCode;           // JPY
//  public String  tumitateKensu;      // 96
//  public String  tumitateKensuRank;  // 77
//  public String  tumitatePlan;       // 1
//  public String  tumitatePlanFM;     // ○
    public double  zenjituhiFM;        // -47円

	
	public FundData(LocalDateTime dateTime, String isinCode, RawFundData raw) {
        this.dateTime           = dateTime;
        this.isinCode           = isinCode;
        //
        this.bunpaiKinriFM      = raw.bunpaiKinriFM.equals("-") ? -1 : DoubleUtil.round(Double.parseDouble(raw.bunpaiKinriFM.replace("%", "")) * 0.01, 4);
//      this.bunpaiKinriRank    = raw.bunpaiKinriRank;
        this.categoryMeisyo     = raw.categoryMeisyo;
//      this.flgFromSSeimei     = raw.flgFromSSeimei;
//      this.flgHanbaiteisi     = raw.flgHanbaiteisi;
//      this.flgHRiskFund       = raw.flgHRiskFund;
//      this.flgSWOnly          = raw.flgSWOnly;
//      this.fundAisyo          = raw.fundAisyo;
        this.fundMei            = raw.fundMei;
//      this.fundRyaku          = raw.fundRyaku;
//      this.hanbaigakuFM       = raw.hanbaigakuFM;
//      this.hanbaigakuRanking  = raw.hanbaigakuRanking;
        this.hanbaiTesuryo      = raw.hanbaiTesuryo.equals("-") ? -1 : DoubleUtil.round(Double.parseDouble(raw.hanbaiTesuryo), 4);
//      this.hanbaiTesuryoFM    = raw.hanbaiTesuryoFM;
        this.hyokaKijyunbi      = raw.hyokaKijyunbi.replace("年", "-").replace("月", "");
        this.jyunsisanEn        = raw.jyunsisanEn.equals("-") ? -1 : Math.round(Double.parseDouble(raw.jyunsisanEn) * 1000000);
//      this.jyunsisanEnFM      = raw.jyunsisanEnFM;
//      this.jyunsisanRank      = raw.jyunsisanRank;
        this.kanaCode           = Company.get(raw.kanaCode);
        this.kessanHindo        = raw.kessanHindo.equals("-") ? -1 : Integer.parseInt(raw.kessanHindo);
//      this.kessanHindoFM      = raw.kessanHindoFM;
        this.kijyunKagaku       = raw.kijyunKagaku.equals("-") ? -1 : Double.parseDouble(raw.kijyunKagaku);
//      this.kijyunKagakuFM     = raw.kijyunKagakuFM;
//      this.msCategoryCodeDai  = raw.msCategoryCodeDai;
//      this.nisaHanbaigakuFM   = raw.nisaHanbaigakuFM;
//      this.nisaHanbaigakuRank = raw.nisaHanbaigakuRank;
//      this.returnRank         = raw.returnRank;
//      this.sbFundCode         = raw.sbFundCode;
        this.sintakHosyu        = DoubleUtil.round(Double.parseDouble(raw.sintakHosyu) * 0.01, 4);
//      this.sintakHosyuFM      = raw.sintakHosyuFM;
        this.sougouRating       = raw.sougouRating.equals("-") ? -1 : Integer.parseInt(raw.sougouRating);
//      this.sougouRatingFM     = raw.sougouRatingFM;
        this.toshiArea          = Area.get(raw.toshiArea);
        this.toshiTarget        = Target.get(raw.toshiTarget);
        this.totalReturn        = raw.totalReturn.equals("-") ? -1 : DoubleUtil.round(Double.parseDouble(raw.totalReturn) * 0.01, 4);
//      this.totalReturnFM      = raw.totalReturnFM;
        this.tukaCode           = raw.tukaCode;
//      this.tumitateKensu      = raw.tumitateKensu;
//      this.tumitateKensuRank  = raw.tumitateKensuRank;
//      this.tumitatePlan       = raw.tumitatePlan;
//      this.tumitatePlanFM     = raw.tumitatePlanFM;
        this.zenjituhiFM        = raw.zenjituhiFM.equals("-") ? -1 : Double.parseDouble(raw.zenjituhiFM.replace(",", "").replace("円", "").replace("USD", ""));
	}
	public FundData() {
		this.dateTime           = null;
		this.isinCode           = null;
		//
	    this.bunpaiKinriFM      = 0;
//	    this.bunpaiKinriRank    = null;
	    this.categoryMeisyo     = null;
//	    this.flgFromSSeimei     = null;
//	    this.flgHanbaiteisi     = null;
//	    this.flgHRiskFund       = null;
//	    this.flgSWOnly          = null;
//	    this.fundAisyo          = null;
	    this.fundMei            = null;
//	    this.fundRyaku          = null;
//	    this.hanbaigakuFM       = null;
//	    this.hanbaigakuRanking  = null;
//	    this.hanbaiTesuryo      = null;
//	    this.hanbaiTesuryoFM    = null;
	    this.hyokaKijyunbi      = null;
	    this.jyunsisanEn        = 0;
//	    this.jyunsisanEnFM      = null;
//	    this.jyunsisanRank      = null;
	    this.kanaCode           = null;
	    this.kessanHindo        = 0;
//	    this.kessanHindoFM      = null;
	    this.kijyunKagaku       = 0;
//	    this.kijyunKagakuFM     = null;
//      this.msCategoryCodeDai  = null;
//	    this.nisaHanbaigakuFM   = null;
//	    this.nisaHanbaigakuRank = null;
//	    this.returnRank         = null;
//	    this.sbFundCode         = null;
	    this.sintakHosyu        = 0;
//	    this.sintakHosyuFM      = null;
	    this.sougouRating       = 0;
//	    this.sougouRatingFM     = null;
	    this.toshiArea          = null;
	    this.toshiTarget        = null;
	    this.totalReturn        = 0;
//	    this.totalReturnFM      = null;
	    this.tukaCode           = null;
//	    this.tumitateKensu      = null;
//	    this.tumitateKensuRank  = null;
//	    this.tumitatePlan       = null;
//	    this.tumitatePlanFM     = null;
	    this.zenjituhiFM        = 0;
	}
	
	public FundData(JsonObject jsonObject) {
		super(jsonObject);
	}
	
	
	//
	//
	//
	public static class RawFundData extends JSONBase {
	    @JSONName("BunpaiKinriFM")       public String bunpaiKinriFM;      // 14.97%
	    @JSONName("BunpaiKinriRank")     public String bunpaiKinriRank;    // 6
	    @JSONName("CategoryMeisyo")      public String categoryMeisyo;     // 国際REIT型
	    @JSONName("FlgFromSSeimei")      public String flgFromSSeimei;     // 0
	    @JSONName("FlgHanbaiteisi")      public String flgHanbaiteisi;     // 0
	    @JSONName("FlgHRiskFund")        public String flgHRiskFund;       // 0
	    @JSONName("FlgSWOnly")           public String flgSWOnly;          // 0
	    @JSONName("FundAisyo")           public String fundAisyo;          // −
	    @JSONName("FundMei")             public String fundMei;            // ワールド・リート・オープン（毎月決算型）
	    @JSONName("FundRyaku")           public String fundRyaku;          // ワールド・リート・オープン（毎月決算型）
	    @JSONName("HanbaigakuFM")        public String hanbaigakuFM;       // 4,359,962円
	    @JSONName("HanbaigakuRanking")   public String hanbaigakuRanking;  // 127
	    @JSONName("HanbaiTesuryo")       public String hanbaiTesuryo;      // -
	    @JSONName("HanbaiTesuryoFM")     public String hanbaiTesuryoFM;    // なし
	    @JSONName("HyokaKijyunbi")       public String hyokaKijyunbi;      // 2020年03月
	    @JSONName("JyunsisanEn")         public String jyunsisanEn;        // 142588
	    @JSONName("JyunsisanEnFM")       public String jyunsisanEnFM;      // 142,588
	    @JSONName("JyunsisanRank")       public String jyunsisanRank;      // 15
	    @JSONName("KanaCode")            public String kanaCode;           // 081
	    @JSONName("KessanHindo")         public String kessanHindo;        // 12
	    @JSONName("KessanHindoFM")       public String kessanHindoFM;      // 毎月
	    @JSONName("KijyunKagaku")        public String kijyunKagaku;       // 1403
	    @JSONName("KijyunKagakuFM")      public String kijyunKagakuFM;     // 1,403円
	    @JSONName("MSCategoryCodeDai")   public String msCategoryCodeDai;  // 13
	    @JSONName("NISAHanbaigakuFM")    public String nisaHanbaigakuFM;   // 696,961円
	    @JSONName("NISAHanbaigakuRank")  public String nisaHanbaigakuRank; // 97
	    @JSONName("ReturnRank")          public String returnRank;         // 221
	    @JSONName("SBFundCode")          public String sbFundCode;         // 09111041
	    @JSONName("SintakHosyu")         public String sintakHosyu;        // 1.705
	    @JSONName("SintakHosyuFM")       public String sintakHosyuFM;      // 1.705%
	    @JSONName("SougouRating")        public String sougouRating;       // 2
	    @JSONName("SougouRatingFM")      public String sougouRatingFM;     // ★★
	    @JSONName("ToshiArea")           public String toshiArea;          // 01
	    @JSONName("ToshiTarget")         public String toshiTarget;        // 20
	    @JSONName("TotalReturn")         public String totalReturn;        // -33.36
	    @JSONName("TotalReturnFM")       public String totalReturnFM;      // -33.36%
	    @JSONName("TukaCode")            public String tukaCode;           // JPY
	    @JSONName("TumitateKensu")       public String tumitateKensu;      // 96
	    @JSONName("TumitateKensuRank")   public String tumitateKensuRank;  // 77
	    @JSONName("TumitatePlan")        public String tumitatePlan;       // 1
	    @JSONName("TumitatePlanFM")      public String tumitatePlanFM;     // ○
	    @JSONName("ZenjituhiFM")         public String zenjituhiFM;        // -47円

		public RawFundData() {
		    this.bunpaiKinriFM      = null;
		    this.bunpaiKinriRank    = null;
		    this.categoryMeisyo     = null;
		    this.flgFromSSeimei     = null;
		    this.flgHanbaiteisi     = null;
		    this.flgHRiskFund       = null;
		    this.flgSWOnly          = null;
		    this.fundAisyo          = null;
		    this.fundMei            = null;
		    this.fundRyaku          = null;
		    this.hanbaigakuFM       = null;
		    this.hanbaigakuRanking  = null;
		    this.hanbaiTesuryo      = null;
		    this.hanbaiTesuryoFM    = null;
		    this.hyokaKijyunbi      = null;
		    this.jyunsisanEn        = null;
		    this.jyunsisanEnFM      = null;
		    this.jyunsisanRank      = null;
		    this.kanaCode           = null;
		    this.kessanHindo        = null;
		    this.kessanHindoFM      = null;
		    this.kijyunKagaku       = null;
		    this.kijyunKagakuFM     = null;
		    this.msCategoryCodeDai  = null;
		    this.nisaHanbaigakuFM   = null;
		    this.nisaHanbaigakuRank = null;
		    this.returnRank         = null;
		    this.sbFundCode         = null;
		    this.sintakHosyu        = null;
		    this.sintakHosyuFM      = null;
		    this.sougouRating       = null;
		    this.sougouRatingFM     = null;
		    this.toshiArea          = null;
		    this.toshiTarget        = null;
		    this.totalReturn        = null;
		    this.totalReturnFM      = null;
		    this.tukaCode           = null;
		    this.tumitateKensu      = null;
		    this.tumitateKensuRank  = null;
		    this.tumitatePlan       = null;
		    this.tumitatePlanFM     = null;
		    this.zenjituhiFM        = null;
		}

		public RawFundData(JsonObject jsonObject) {
			super(jsonObject);
		}
	}
	
	public static class RawData extends JSONBase {
		public Map<String, RawFundData> map;
		
		public RawData() {
			map = null;
		}
		
		public RawData(JsonObject jsonObject) {
			super(jsonObject);
		}
	}
	
	public static final String URL = "https://moneykit.net/data/fund/SFBA1700F471.js";
	public static Pattern PAT_YRMONDATE = Pattern.compile("YrMonDate='(?<yyyy>2[0-9]{3})/(?<mm>[0-9]{2})/(?<dd>[0-9]{2}) (?<hhmmss>[0-9:]{8})';");
	public static Pattern PAT_FUNDDATA  = Pattern.compile(";FundData= (?<json>\\{.+\\});");
	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static void update() {
		HttpUtil.Result result = HttpUtil.getInstance().withCharset("MS932").download(URL);
		
		String string = result.result;
		
		LocalDateTime dateTime;
		{
			Matcher m = PAT_YRMONDATE.matcher(string);
			if (m.find()) {
				String yyyy   = m.group("yyyy");
				String mm     = m.group("mm");
				String dd     = m.group("dd");
				String hhmmss = m.group("hhmmss");
				
				String dateTimeString = String.format("%s-%s-%s %s", yyyy, mm, dd, hhmmss);
				dateTime = LocalDateTime.parse(dateTimeString, DATETIME_FORMATTER);
			} else {
				throw new UnexpectedException("no match PAT_YRMONDATE");
			}
		}

		String jsonString;
		{
			Matcher m = PAT_FUNDDATA.matcher(string);
			if (m.find()) {
				jsonString = String.format("{\"map\": %s}", m.group("json").replace("[", "").replace("]", ""));
			} else {
				throw new UnexpectedException("no match PAT_FUNDDATA");
			}
		}
		RawData rawData = JSONBase.getInstance(RawData.class, jsonString);

		List<FundData> list = new ArrayList<>();
		for(Map.Entry<String, RawFundData> entry: rawData.map.entrySet()) {
			String      isinCode = entry.getKey();
			RawFundData raw      = entry.getValue();
			list.add(new FundData(dateTime, isinCode, raw));
		}
		
		logger.info("save {} {}", list.size(), PATH_FILE);
		save(list);
	}
	public static void main(String[] arsg) {
		logger.info("START");

		update();
		
		{
			List<FundData> list = getList();
			logger.info("list {}", list.size());
		}
		
		logger.info("STOP");
	}

	@Override
	public int compareTo(FundData that) {
		int ret = this.dateTime.compareTo(that.dateTime);
		if (ret == 0) ret = this.isinCode.compareTo(that.isinCode);
		return ret;
	}
}
