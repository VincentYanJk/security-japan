package yokwe.security.japan.fsa;

import java.util.List;

import yokwe.util.CSVUtil;

public class EDINET implements Comparable<EDINET> {
	public static final String URL_DOWNLOAD     = "https://disclosure.edinet-fsa.go.jp/E01EW/download?uji.verb=W1E62071EdinetCodeDownload&uji.bean=ee.bean.W1E62071.EEW1E62071Bean&TID=W1E62071&PID=W1E62071&SESSIONKEY=9999&downloadFileName=&lgKbn=2&dflg=0&iflg=0&dispKbn=1";
	public static final String CHARSET_DOWNLOAD = "MS932";
	
	public static final String PATH_DOWNLOAD = "tmp/download/Edinetcode.zip";
	public static final String PATH_DATA     = "tmp/data/edinet.csv";
	public static final String ENTRY_NAME    = "EdinetcodeDlInfo.csv";
	
	public static List<EDINET> load() {
		return CSVUtil.read(EDINET.class).file(PATH_DATA);
	}
	
	@CSVUtil.ColumnName("ＥＤＩＮＥＴコード")
	public String edinetCode;
	
	@CSVUtil.ColumnName("提出者種別")
	public String category;

	@CSVUtil.ColumnName("上場区分")
	public String listingType;

	@CSVUtil.ColumnName("連結の有無")
	public String hasConsolidateSubsidary;

	@CSVUtil.ColumnName("資本金")
	public String capital;
	
	@CSVUtil.ColumnName("決算日")
	public String closingDate;
	
	@CSVUtil.ColumnName("提出者名")
	public String name;
	
	@CSVUtil.ColumnName("提出者名（英字）")
	public String nameEnglish;
	
	@CSVUtil.ColumnName("提出者名（ヨミ）")
	public String nameRuby;
	
	@CSVUtil.ColumnName("所在地")
	public String address;
	
	@CSVUtil.ColumnName("提出者業種")
	public String sector;
	
	@CSVUtil.ColumnName("証券コード")
	public String stockCode; // can be NNNN0 or empty
	
	@CSVUtil.ColumnName("提出者法人番号")
	public String corporateCode;
	
	@Override
	public String toString() {
		return String.format("%s %s %s %s %s %s %s %s %s %s %s %s %s",
				edinetCode, category, listingType, hasConsolidateSubsidary, capital, closingDate,
				name, nameEnglish, nameRuby, address, sector, stockCode, corporateCode);
	}
	
	@Override
	public int compareTo(EDINET that) {
		int ret = this.edinetCode.compareTo(that.edinetCode);
		return ret;
	}
}
