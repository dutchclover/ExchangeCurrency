package com.dgroup.exchangerates.data.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name = "Valute")
public class WValute {

    @Element(name = "NumCode")
    public Long numCode;
    @Attribute(name = "ID", empty = "-1", required = false)
    public String id;
    @Element(name = "CharCode")
    public String charCode;
    @Element(name = "Nominal")
    public String nominal;
    @Element(name = "Name")
    public String name;
    @Element(name = "Value")
    public String value;

}
