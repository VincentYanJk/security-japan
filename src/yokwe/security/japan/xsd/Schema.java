package yokwe.security.japan.xsd;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "schema")
public class Schema {
	@XmlAttribute
	String targetNamespace;
	
	@XmlElement(name = "element")
	List<Element> elementList;
	
	@Override
	public String toString() {
		return String.format("{%s %s}", targetNamespace, elementList);
	}
}

//  <schema targetNamespace="http://www.xbrl.tdnet.info/taxonomy/jp/tse/tdnet/ed/t/2014-01-12" attributeFormDefault="unqualified" elementFormDefault="qualified" xmlns="http://www.w3.org/2001/XMLSchema" xmlns:tse-ed-t="http://www.xbrl.tdnet.info/taxonomy/jp/tse/tdnet/ed/t/2014-01-12" xmlns:link="http://www.xbrl.org/2003/linkbase" xmlns:num="http://www.xbrl.org/dtr/type/numeric" xmlns:nonnum="http://www.xbrl.org/dtr/type/non-numeric" xmlns:xbrldt="http://xbrl.org/2005/xbrldt" xmlns:deprecated="http://www.xbrl.org/2009/role/deprecated" xmlns:xl="http://www.xbrl.org/2003/XLink" xmlns:xbrli="http://www.xbrl.org/2003/instance" xmlns:tse-ed-types="http://www.xbrl.tdnet.info/taxonomy/jp/tse/tdnet/ed/o/types/2014-01-12" xmlns:xlink="http://www.w3.org/1999/xlink">
//    <element name="FilingDate" id="tse-ed-t_FilingDate" type="xbrli:dateItemType" substitutionGroup="xbrli:item" abstract="false" nillable="true" xbrli:periodType="instant"/>
