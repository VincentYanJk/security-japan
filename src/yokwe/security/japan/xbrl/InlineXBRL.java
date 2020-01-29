package yokwe.security.japan.xbrl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import yokwe.UnexpectedException;
import yokwe.security.japan.xbrl.taxonomy.LabelData;
import yokwe.util.XMLUtil.QValue;
import yokwe.util.XMLUtil.XMLAttribute;
import yokwe.util.XMLUtil.XMLElement;

public abstract class InlineXBRL {
	static final org.slf4j.Logger logger = LoggerFactory.getLogger(InlineXBRL.class);

	public enum Kind {
		STRING, BOOLEAN, NUMBER
	}
	
	private interface Builder {
		public InlineXBRL getInstance(XMLElement xmlElement);
	}
	
	private static class BooleanBuilder implements Builder {
		private final boolean value;
		BooleanBuilder(boolean newValue) {
			value = newValue;
		}
		public InlineXBRL getInstance(XMLElement xmlElement) {
			return new BooleanValue(xmlElement, value);
		}
	}
	private static class StringBuilder implements Builder {
		public InlineXBRL getInstance(XMLElement xmlElement) {
			return new StringValue(xmlElement);
		}
	}
	private static Map<QValue, Builder> nonNumericBuilderMap = new TreeMap<>();
	static {
		nonNumericBuilderMap.put(XBRL.IXT_BOOLEAN_TRUE,            new BooleanBuilder(true));
		nonNumericBuilderMap.put(XBRL.IXT_BOOLEAN_FALSE,           new BooleanBuilder(false));
		nonNumericBuilderMap.put(XBRL.IXT_DATE_YEAR_MONTH_DAY_CJK, new StringBuilder());
	}
	private static class NonNumericBuilder implements Builder {
		public InlineXBRL getInstance(XMLElement xmlElement) {
			String format = xmlElement.getAttributeOrNull("format");
			if (format == null) {
				return new StringValue(xmlElement);
			} else {
				QValue qValue = xmlElement.expandNamespacePrefix(format);
				if (nonNumericBuilderMap.containsKey(qValue)) {
					Builder builder = nonNumericBuilderMap.get(qValue);
					return builder.getInstance(xmlElement);
				} else {
					logger.error("Unexpected xmlElement {}", xmlElement);
					logger.error("  format {}", format);
					logger.error("  qValue {}", qValue);
					throw new UnexpectedException("Unexpected xmlElement");	
				}
			}
		}
	}
	
	private static class NumberBuilder implements Builder {
		public InlineXBRL getInstance(XMLElement xmlElement) {
			return new NumberValue(xmlElement);
		}
	}
	private static Map<QValue, Builder> nonFractionalBuilderMap = new TreeMap<>();
	static {
		nonFractionalBuilderMap.put(XBRL.IXT_NUM_DOT_DECIMAL, new NumberBuilder());
	}
	private static class NonFractionBuilder implements Builder {
		public InlineXBRL getInstance(XMLElement xmlElement) {
			String format = xmlElement.getAttributeOrNull("format");
			if (format == null) {
				return new NumberValue(xmlElement);
			} else {
				QValue qValue = xmlElement.expandNamespacePrefix(format);
				if (nonFractionalBuilderMap.containsKey(qValue)) {
					Builder builder = nonFractionalBuilderMap.get(qValue);
					return builder.getInstance(xmlElement);
				} else {
					logger.error("Unexpected qValue {}", qValue);
					throw new UnexpectedException("Unexpected qValue");	
				}
			}
		}
	}
	
	private static Map<QValue, Builder> elementBuilderMap = new TreeMap<>();
	static {
		elementBuilderMap.put(XBRL.IX_NON_NUMERIC,  new NonNumericBuilder());
		elementBuilderMap.put(XBRL.IX_NON_FRACTION, new NonFractionBuilder());
	}
	private static Builder getBuilder(XMLElement xmlElement) {
		QValue key = new QValue(xmlElement);
		if (elementBuilderMap.containsKey(key)) {
			return elementBuilderMap.get(key);
		} else {
			logger.error("Unexpected key {}!", key);
			throw new UnexpectedException("Unexpected key");
		}
	}
	
	public static boolean canGetInstance(XMLElement xmlElement) {
		QValue key = new QValue(xmlElement);
		return elementBuilderMap.containsKey(key);
	}
	public static InlineXBRL getInstance(XMLElement xmlElement) {
		if (canGetInstance(xmlElement)) {
			Builder builder = getBuilder(xmlElement);
			return builder.getInstance(xmlElement);
		} else {
			logger.error("Unexpected name {}", xmlElement.name);
			throw new UnexpectedException("Unexpected name");
		}
	}
	
	
	public final Kind       kind;
	public final XMLElement xmlElement;
	
	public final Set<String> contextSet;
	
	public final String  name;
	public final String  format;
	public final String  value;
	
	public final QValue  qName;
	public final boolean isNull;

	private InlineXBRL(Kind kind, XMLElement xmlElement) {
		this.kind         = kind;
		this.xmlElement   = xmlElement;
		
		{
			String contextRef = xmlElement.getAttribute("contextRef");
			TreeSet<String> set = new TreeSet<>(Arrays.asList(contextRef.split("_")));
			this.contextSet = Collections.unmodifiableSet(set);
		}
		
		this.name   = xmlElement.getAttribute("name");
		this.format = xmlElement.getAttributeOrNull("format");
		this.value  = xmlElement.content;
		
		this.qName  = xmlElement.expandNamespacePrefix(this.name);
		
		// check nil
		String nilValue = xmlElement.getAttributeOrNull(XML.XSI_NIL);
		if (nilValue == null) {
			this.isNull = false;
		} else {
			switch(nilValue) {
			case "true":
				this.isNull = true;
				break;
//			case "false":
//				this.isNull = false;
//				break;
			default:
				logger.error("Unexpected nilValue {}!", nilValue);
				throw new UnexpectedException("Unexpected nilValue");
			}
		}
	}
	
	public static class StringValue extends InlineXBRL {
		public static Set<QValue> validAttributeSet = new TreeSet<>();
		static {
			validAttributeSet.add(new QValue("", "contextRef"));
			validAttributeSet.add(new QValue("", "name"));
			validAttributeSet.add(new QValue("", "format"));
			validAttributeSet.add(XML.XSI_NIL);
			//
			validAttributeSet.add(new QValue("", "escape"));
		}
		
		public final String escape;
		public final String stringValue;
		
		public StringValue(XMLElement xmlElement) {
			super(Kind.STRING, xmlElement);
			this.escape = xmlElement.getAttributeOrNull("escape");
			this.stringValue = xmlElement.content;
			
			// Sanity check
			for(XMLAttribute xmlAttribute: xmlElement.attributeList) {
				QValue value = new QValue(xmlAttribute);
				if (validAttributeSet.contains(value)) continue;
				logger.error("Unexpected attribute {}", xmlAttribute.name);
				logger.error("xmlElement {}!", xmlElement);
				throw new UnexpectedException("Unexpected attribute");
			}
		}
		
		@Override
		public String toString() {
			if (isNull) {
				if (format == null) {
					return String.format("{STRING %s %s *NULL*}", name, contextSet);
				} else {
					return String.format("{STRING %s %s %s *NULL*}", name, contextSet, format);
				}
			} else {
				if (format == null) {
					return String.format("{STRING %s %s \"%s\"}", name, contextSet, stringValue);
				} else {
					return String.format("{STRING %s %s %s \"%s\"", name, contextSet, format, stringValue);
				}
			}
		}
	}
	public static class BooleanValue extends InlineXBRL {
		public static Set<QValue> validAttributeSet = new TreeSet<>();
		static {
			validAttributeSet.add(new QValue("", "contextRef"));
			validAttributeSet.add(new QValue("", "name"));
			validAttributeSet.add(new QValue("", "format"));
			//
			validAttributeSet.add(new QValue("", "escape"));
		}

		public final boolean booleanValue;
		
		public BooleanValue(XMLElement xmlElement, boolean booleanValue) {
			super(Kind.BOOLEAN, xmlElement);
			this.booleanValue = booleanValue;
			
			// Sanity check
			for(XMLAttribute xmlAttribute: xmlElement.attributeList) {
				QValue value = new QValue(xmlAttribute);
				if (validAttributeSet.contains(value)) continue;
				logger.error("Unexpected attribute {}!", value);
				logger.error("xmlAttribute {}!", xmlAttribute.name);
				logger.error("xmlElement {}!", xmlElement);
				throw new UnexpectedException("Unexpected attribute");
			}
		}
		@Override
		public String toString() {
			if (isNull) {
				return String.format("{BOOLEAN %s %s *NULL*}", name, contextSet);
			} else {
				return String.format("{BOOLEAN %s %s %s}", name, contextSet, booleanValue);
			}
		}
	}
	public static class NumberValue extends InlineXBRL {
		public static Set<QValue> validAttributeSet = new TreeSet<>();
		static {
			validAttributeSet.add(new QValue("", "contextRef"));
			validAttributeSet.add(new QValue("", "name"));
			validAttributeSet.add(new QValue("", "format"));
			validAttributeSet.add(new QValue("", "unitRef"));
			validAttributeSet.add(new QValue("", "decimals"));
			validAttributeSet.add(new QValue("", "scale"));
			//
			validAttributeSet.add(new QValue("", "sign"));
			validAttributeSet.add(XML.XSI_NIL);
		}

		// ixt:numdotdecimal は 999,000.00
		public final String     unitRef;
		public final int        decimals; // 値の精度情報　3 => 0.001刻み  0 => 1刻み  -3 => 1,000刻み
		public final int        scale;    // 値の意味　6 => 1の値は1,000,000を意味する, -2 => 1の値は0.01を意味する
		public final boolean    isMinus;
		public final BigDecimal unscaledValue;
		public final BigDecimal numberValue;
		public final BigDecimal precision;
		
		public NumberValue(XMLElement xmlElement) {
			super(Kind.NUMBER, xmlElement);
			this.unitRef  = xmlElement.getAttribute("unitRef");
			
			if (isNull) {
				decimals      = 0;
				scale         = 0;
				isMinus       = false;
				unscaledValue = null;
				numberValue   = null;
				precision     = null;
			} else {
				final String decimalsString = xmlElement.getAttribute("decimals");
				final String scaleString    = xmlElement.getAttribute("scale");
				final String signString     = xmlElement.getAttributeOrNull("sign");

				decimals = Integer.parseInt(decimalsString);
				scale    = Integer.parseInt(scaleString);

				if (signString == null) {
					isMinus = false;
				} else {
					switch(signString) {
					case "-":
						isMinus = true;
						break;
					default:
						logger.error("Unexpected signString {}!", signString);
						logger.error("xmlElement {}!", xmlElement);
						throw new UnexpectedException("Unexpected attribute");
					}
				}
				
				// Remove comma
				unscaledValue = new BigDecimal(value.replace(",", ""));
				if (isMinus) {
					numberValue = unscaledValue.scaleByPowerOfTen(scale).negate();
				} else {
					numberValue = unscaledValue.scaleByPowerOfTen(scale);
				}
				
				precision = BigDecimal.valueOf(1, decimals);
			}
		
			// Sanity check
			for(XMLAttribute xmlAttribute: xmlElement.attributeList) {
				QValue value = new QValue(xmlAttribute);
				if (validAttributeSet.contains(value)) continue;
				logger.error("Unexpected attribute {}!", xmlAttribute);
				logger.error("xmlElement {}!", xmlElement);
				throw new UnexpectedException("Unexpected attribute");
			}
		}
		@Override
		public String toString() {
			if (isNull) {
				return String.format("{NUMBER %s %s %s *NULL*}", name, contextSet, unitRef);
			} else {
				return String.format("{NUMBER %s %s %s %s %s}", name, contextSet, unitRef, numberValue, precision);
			}
		}
	}
	
	public String getStringValue() {
		if (kind == Kind.STRING) {
			if (isNull) return null;
			
			InlineXBRL.StringValue stringValue = (InlineXBRL.StringValue)this;
			return stringValue.stringValue;
		} else {
			logger.error("Unexpected kind {} {}", Kind.STRING, this);
			throw new UnexpectedException("Unexpected kind");
		}
	}
	public Boolean getBooleanValue() {
		if (kind == Kind.BOOLEAN) {
			if (isNull) return null;
			
			InlineXBRL.BooleanValue booleanValue = (InlineXBRL.BooleanValue)this;
			return booleanValue.booleanValue;
		} else {
			logger.error("Unexpected kind {} {}", Kind.BOOLEAN, this);
			throw new UnexpectedException("Unexpected kind");
		}
	}
	public BigDecimal getNumberValue() {
		if (kind == Kind.NUMBER) {
			if (isNull) return null;
			
			InlineXBRL.NumberValue numberValue = (InlineXBRL.NumberValue)this;
			return numberValue.numberValue;
		} else {
			logger.error("Unexpected kind {} {}", Kind.NUMBER, this);
			throw new UnexpectedException("Unexpected kind");
		}
	}
	
	public static class Document {
		private static final List<InlineXBRL> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<>());

		private final List<InlineXBRL>              all;
		private final Map<QValue, List<InlineXBRL>> map;
		
		private Document(List<InlineXBRL> all, Map<QValue, List<InlineXBRL>> map) {
			this.all = all;
			this.map = map;
		}

		private static void buildMap(List<InlineXBRL> all, Map<QValue, List<InlineXBRL>> map, InlineXBRL ix) {
			all.add(ix);
			
			QValue qName = ix.qName;
			
			List<InlineXBRL> list;
			if (map.containsKey(qName)) {
				list = map.get(qName);
			} else {
				list = new ArrayList<>();
				map.put(qName, list);
			}
			list.add(ix);
		}
		
		public static Document getInstance(Stream<XMLElement> stream) {
			List<InlineXBRL>              all = new ArrayList<>();
			Map<QValue, List<InlineXBRL>> map = new TreeMap<>();
			
			stream.filter(InlineXBRL::canGetInstance).forEach(o -> buildMap(all, map, InlineXBRL.getInstance(o)));
			
			// Make unmodifiable element of map
			Map<QValue, List<InlineXBRL>> map2 = new TreeMap<>();
			for(Map.Entry<QValue, List<InlineXBRL>> entry: map.entrySet()) {
				QValue key = entry.getKey();
				List<InlineXBRL> value = entry.getValue();
				map2.put(key, Collections.unmodifiableList(value));
			}

			return new Document(Collections.unmodifiableList(all), Collections.unmodifiableMap(map2));
		}

		public List<InlineXBRL> getList() {
			return all;
		}
		public Stream<InlineXBRL> getStream() {
			return getList().stream();
		}
		
		public List<InlineXBRL> getList(QValue qName) {
			if (map.containsKey(qName)) {
				return map.get(qName);
			} else {
				return EMPTY_LIST;
			}
		}
		public Stream<InlineXBRL> getStream(QValue qName) {
			List<InlineXBRL> list = getList(qName);
			return list.stream();
		}
		
		public List<InlineXBRL> getList(LabelData labelData) {
			return getList(labelData.qName);
		}
		public Stream<InlineXBRL> getStream(LabelData labelData) {
			List<InlineXBRL> list = getList(labelData);
			return list.stream();
		}
		
		private Set<String> contextSet = null;
		public Set<String> getContextSet() {
			if (contextSet == null) {
				Set<String> set = new TreeSet<>();
				getList().stream().forEach(o -> set.addAll(o.contextSet));
				contextSet = Collections.unmodifiableSet(set);
			}
			return contextSet;
		}
	}
	
	private static class ContextIncludeAllFilter implements Predicate<InlineXBRL>  {
		private final List<String> contextList;
		private ContextIncludeAllFilter(Context... contexts) {
			contextList = Arrays.stream(contexts).map(o -> o.toString()).collect(Collectors.toList());
		}
		
		@Override
		public boolean test(InlineXBRL ix) {
			return ix.contextSet.containsAll(contextList);
		}
	}
	public static Predicate<InlineXBRL> contextIncludeAll(Context... contexts) {
		return new ContextIncludeAllFilter(contexts);
	}
	
	private static class ContextExcludeAnyFilter implements Predicate<InlineXBRL>  {
		private final List<String> contextList;
		private ContextExcludeAnyFilter(Context... contexts) {
			contextList = Arrays.stream(contexts).map(o -> o.toString()).collect(Collectors.toList());
		}
		
		@Override
		public boolean test(InlineXBRL ix) {
			for(String context: contextList) {
				if (ix.contextSet.contains(context)) return false;
			}
			return true;
		}
	}
	public static Predicate<InlineXBRL> contextExcludeAny(Context... contexts) {
		return new ContextExcludeAnyFilter(contexts);
	}
	
	public static boolean booleanFilter(InlineXBRL ix) {
		return ix.kind == InlineXBRL.Kind.BOOLEAN;
	}
	public static boolean numberFilter(InlineXBRL ix) {
		return ix.kind == InlineXBRL.Kind.NUMBER;
	}
	public static boolean stringFilter(InlineXBRL ix) {
		return ix.kind == InlineXBRL.Kind.STRING;
	}
	
	private static class NullFilter implements Predicate<InlineXBRL>  {
		private final boolean acceptNull;
		private NullFilter(boolean acceptNull) {
			this.acceptNull = acceptNull;
		}
		
		@Override
		public boolean test(InlineXBRL ix) {
			if (ix.isNull) {
				return acceptNull;
			} else {
				return true;
			}
		}
	}
	public static Predicate<InlineXBRL> nullFilter(boolean acceptNull) {
		return new NullFilter(acceptNull);
	}


	public static enum Context {
		ANNUAL_MEMBER                   ("AnnualMember"), 
		CONSOLIDATED_MEMBER             ("ConsolidatedMember"), 
		CURRENT_ACCUMULATED_Q_2_DURATION("CurrentAccumulatedQ2Duration"), 
		CURRENT_ACCUMULATED_Q_2_INSTANT ("CurrentAccumulatedQ2Instant"), 
		CURRENT_YEAR_DURATION           ("CurrentYearDuration"), 
		CURRENT_YEAR_INSTANT            ("CurrentYearInstant"), 
		FIRST_QUARTER_MEMBER            ("FirstQuarterMember"), 
		FORECAST_MEMBER                 ("ForecastMember"), 
		LOWER_MEMBER                    ("LowerMember"), 
		NON_CONSOLIDATED_MEMBER         ("NonConsolidatedMember"), 
		PRIOR_ACCUMULATED_Q_2_DURATION  ("PriorAccumulatedQ2Duration"), 
		PRIOR_YEAR_DURATION             ("PriorYearDuration"), 
		PRIOR_YEAR_INSTANT              ("PriorYearInstant"), 
		RESULT_MEMBER                   ("ResultMember"), 
		SECOND_QUARTER_MEMBER           ("SecondQuarterMember"), 
		THIRD_QUARTER_MEMBER            ("ThirdQuarterMember"), 
		UPPER_MEMBER                    ("UpperMember"), 
		YEAR_END_MEMBER                 ("YearEndMember");
		
		public final String value;
		
		Context(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
}
