package com.dgroup.exchangerates.features.cryptocap.data;



public class CryptoCurrencyHeader {

    public static String getNumber(){
        return "#";
    }

    public static String getName(){
        return "Name";
    }

    public static String getCapital(){
        return "Market Cap";
    }

    public static String getPrice(){
        return "Price";
    }

    public static String getVolume(){
        return "Volume (24h)";
    }

    public static String getSupply(){
        return "Circulating Supply";
    }

    public static String getChange1h(){
        return "Change\n(1h)";
    }

    public static String getChange24h(){
        return "Change\n(24h)";
    }

    public static String getChange7d(){
        return "Change\n(7d)";
    }

    public static String getGraph(){
        return "Price Graph\n(7d)";
    }
}
