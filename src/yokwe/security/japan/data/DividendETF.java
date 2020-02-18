package yokwe.security.japan.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import yokwe.util.CSVUtil;
import yokwe.util.CSVUtil.DecimalPlaces;
import yokwe.util.DoubleUtil;
import yokwe.util.libreoffice.Sheet;
import yokwe.util.libreoffice.SpreadSheet;

@Sheet.HeaderRow(0)
@Sheet.DataRow(1)
public class DividendETF extends Sheet implements Comparable<DividendETF> {
	public static final String PATH_FILE        = "tmp/data/dividend-etf.csv";

	public static List<DividendETF> load() {
		return CSVUtil.read(DividendETF.class).file(PATH_FILE);
	}
	public static void save(Collection<DividendETF> collection) {
		List<DividendETF> list = new ArrayList<>(collection);
		save(list);
	}
	public static void save(List<DividendETF> list) {
		// Sort before save
		Collections.sort(list);
		CSVUtil.write(DividendETF.class).file(PATH_FILE, list);
	}


	// record,stockCode,date,unit,dividend,currency,name
	
	@ColumnName("record")
	@NumberFormat(SpreadSheet.FORMAT_DATE)
	public String record;    // YYYY-MM-DD
	
	@ColumnName("stockCode")
	@NumberFormat(SpreadSheet.FORMAT_STRING)
	public String stockCode; // Can be four or five digits
	
	@ColumnName("date")
	@NumberFormat(SpreadSheet.FORMAT_DATE)
	public String date;      // YYYY-MM-DD
	
	@ColumnName("unit")
	public int    unit;      // unit of dividend
	
	@ColumnName("dividend")
	@DecimalPlaces(2)
	public double dividend;  // dividend value of above unit
	
	@ColumnName("currency")
	public String currency;  // currency of dividend
	
	@ColumnName("name")
	public String name;      // name of stockCode
	
	public DividendETF() {
		record    = null;
		stockCode = null;
		date      = null;
		unit      = 0;
		dividend  = 0;
		currency  = null;
		name      = null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else {
			if (o instanceof DividendETF) {
				DividendETF that = (DividendETF)o;
				// Don't consider field file.
				return
					this.record.equals(that.record) &&
					this.stockCode.equals(that.stockCode) &&
					this.date.equals(that.date) &&
					this.unit == that.unit &&
					DoubleUtil.isAlmostEqual(this.dividend, that.dividend) &&
					this.currency.equals(that.currency) &&
					this.name.equals(that.name);
			} else {
				return false;
			}
		}
	}
	@Override
	public String toString() {
		return String.format("{%s %s %s %3d %.2f %s}", record, stockCode, date, unit, dividend, currency);
	}
	@Override
	public int compareTo(DividendETF that) {
		int ret = this.record.compareTo(that.record);
		if (ret == 0) ret = this.stockCode.compareTo(that.stockCode);
		if (ret == 0) ret = this.date.compareTo(that.date);
		return ret;
	}
}