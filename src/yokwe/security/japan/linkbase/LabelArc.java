package yokwe.security.japan.linkbase;

import javax.xml.bind.annotation.XmlAttribute;

// This arc role value is for use on a <labelArc> from a concept Locator (<loc> element) to a <label> element
// and it indicates that the label conveys human-readable information about the Concept.
public class LabelArc {
	// The @xlink:from attribute on an Arc MUST be equal to the value of an @xlink:label attribute of Locator.
	@XmlAttribute(name = "from", namespace="http://www.w3.org/1999/xlink", required = true)
	public String from;
	
	// The @xlink:to attribute on an Arc MUST be equal to the value of an @xlink:label attribute of Label.
	@XmlAttribute(name = "to", namespace="http://www.w3.org/1999/xlink", required = true)
	public String to;
	
	// xlink:arcrole must be "http://www.xbrl.org/2003/arcrole/concept-label"
	
	@Override
	public String toString() {
		return String.format("{%s %s}", from, to);
	}
}

//<linkbase xmlns="http://www.xbrl.org/2003/linkbase" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xbrli="http://www.xbrl.org/2003/instance">
//<labelLink xlink:type="extended" xlink:role="http://www.xbrl.org/2003/role/link">
//  <loc xlink:type="locator" xlink:href="tse-ed-t-2014-01-12.xsd#tse-ed-t_AmountChangeGrossOperatingRevenues" xlink:label="AmountChangeGrossOperatingRevenues"/>
//  <label xlink:type="resource" xlink:label="label_AmountChangeGrossOperatingRevenues" xlink:role="http://www.xbrl.org/2003/role/label" xml:lang="ja" id="label_AmountChangeGrossOperatingRevenues">増減額</label>
//  <labelArc xlink:type="arc" xlink:arcrole="http://www.xbrl.org/2003/arcrole/concept-label" xlink:from="AmountChangeGrossOperatingRevenues" xlink:to="label_AmountChangeGrossOperatingRevenues"/>
