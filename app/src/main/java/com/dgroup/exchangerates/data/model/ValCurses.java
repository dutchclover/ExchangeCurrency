package com.dgroup.exchangerates.data.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;


@Root(name = "ValCurs")
public class ValCurses {

    @Attribute(name = "Date", empty = "-1", required = false)
    private String Date;

    @Attribute(name = "name", empty = "-1", required = false)
    private String name;

    @ElementList(inline = true)
    public List<WValute> valutes;

}
