package yokwe.security.japan.xbrl;

import yokwe.util.xml.QValue;

public class XBRL {
	// Namespace
	public static final String NS_IX      = "http://www.xbrl.org/2008/inlineXBRL";
	public static final String NS_IXT     = "http://www.xbrl.org/inlineXBRL/transformation/2011-07-31";
	public static final String NS_LINK    = "http://www.xbrl.org/2003/linkbase";
	public static final String NS_NUM     = "http://www.xbrl.org/dtr/type/numeric";
	public static final String NS_NON_NUM = "http://www.xbrl.org/dtr/type/non-numeric";
	
	public static final String NS_XBRLDI   = "http://xbrl.org/2006/xbrldi";
	public static final String NS_XBRLI    = "http://www.xbrl.org/2003/instance";
	public static final String NS_ISO_4217 = "http://www.xbrl.org/2003/iso4217";

	public static final String NS_TSE_AT_T = "http://www.xbrl.tdnet.info/taxonomy/jp/tse/tdnet/at/t/2014-01-12";
	public static final String NS_TSE_ED_T = "http://www.xbrl.tdnet.info/taxonomy/jp/tse/tdnet/ed/t/2014-01-12";
	public static final String NS_TSE_RE_T = "http://www.xbrl.tdnet.info/taxonomy/jp/tse/tdnet/re/t/2014-01-12";
	
	public static final String NS_TSE_T_CG = "http://www.xbrl.tdnet.info/jp/br/tdnet/t/cg/2007-06-30";


	public static final String NS_TSE_SCEDJPSM_87250 = "http://www.xbrl.tdnet.info/jp/tse/tdnet/sc/edjp/sm/87250/20191010407057";
	
	// Value	
	public static final QValue IX_NON_NUMERIC  = new QValue(NS_IX, "nonNumeric");
	public static final QValue IX_NON_FRACTION = new QValue(NS_IX, "nonFraction");
	
	public static final QValue IXT_BOOLEAN_TRUE               = new QValue(NS_IXT, "booleantrue");
	public static final QValue IXT_BOOLEAN_FALSE              = new QValue(NS_IXT, "booleanfalse");
	public static final QValue IXT_DATE_YEAR_MONTH_DAY_CJK    = new QValue(NS_IXT, "dateyearmonthdaycjk");
	public static final QValue IXT_DATE_ERA_YEAR_MONTH_DAY_JP = new QValue(NS_IXT, "dateerayearmonthdayjp");
	public static final QValue IXT_NUM_DOT_DECIMAL            = new QValue(NS_IXT, "numdotdecimal");
	public static final QValue IXT_NUM_UNIT_DECIMAL           = new QValue(NS_IXT, "numunitdecimal");
	
	// Role
	public static final String ROLE_LABLE                         = "http://www.xbrl.org/2003/role/label";
	public static final String ROLE_TERSE_LABEL                   = "http://www.xbrl.org/2003/role/terseLabel";
	public static final String ROLE_VERBOSE_LABEL                 = "http://www.xbrl.org/2003/role/verboseLabel";
	public static final String ROLE_QUARTERLY_VERBOSE_LABEL       = "http://www.xbrl.tdnet.info/jp/tse/tdnet/role/Quarterly/verboseLabel";
	public static final String ROLE_INTERIM_VERBOSE_LABEL         = "http://www.xbrl.tdnet.info/jp/tse/tdnet/role/Interim/verboseLabel";
	public static final String ROLE_NON_CONSOLIDATED_LABEL        = "http://www.xbrl.tdnet.info/jp/tse/tdnet/role/NonConsolidated/label";
	public static final String ROLE_NON_CONSOLIDATED_VERBOSELABEL = "http://www.xbrl.tdnet.info/jp/tse/tdnet/role/NonConsolidated/verboseLabel";
	public static final String ROLE_QUARTERLYLABEL                = "http://www.xbrl.tdnet.info/jp/tse/tdnet/role/Quarterly/label";
	public static final String ROLE_INTERLIM_LABEL                = "http://www.xbrl.tdnet.info/jp/tse/tdnet/role/Interim/label";

	// Lang
	public static final String LANG_EN = "en";
	public static final String LANG_JA = "ja";

}
