package com.dgroup.exchangerates.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Bank")
public class ServBankCourse {

    @Element(name = "Name")
    public String name;

    @Element(name = "Url", required=false)
    public String url;

    @Element(name = "USD")
    public USD USD;

    @Element(name = "EUR")
    public EUR EUR;

    @Element(name = "ChangeTime")
    public String changeTime;

    public String getUsdBuy() {
        return USD.buy;
    }

    public String getUsdSell() {
        return USD.sell;
    }


    public String getEurBuy() {
        return EUR.buy;
    }


    public String getEurSell() {
        return EUR.sell;
    }
}

@Root(name = "USD")
class USD{

    @Element(name = "Buy")
    public String buy;

    @Element(name = "Sell")
    public String sell;
}

@Root(name = "EUR")
class EUR{
    @Element(name = "Buy")
    public String buy;

    @Element(name = "Sell")
    public String sell;
}
