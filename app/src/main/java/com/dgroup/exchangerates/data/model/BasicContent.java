package com.dgroup.exchangerates.data.model;

import com.dgroup.exchangerates.data.model.db.Valute;
import com.dgroup.exchangerates.utils.constants.TinyDbKeys;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;


public class BasicContent {

    private Valute basicValute;

    private double count;

    private String charCodeBasicCount;

    public Valute getBasicValute() {
        return basicValute;
    }

    public void setCount(double count) {
        this.count = count;
        TinyDbWrap.getInstance().putDouble(TinyDbKeys.KEYS.COUNT_VALUTE, count);
    }

    public double getCount() {
        return count;
    }

    public String getCharCodeBasicCount() {
        return charCodeBasicCount;
    }

    public void setCharCodeBasicCount(String charCodeBasicCount) {
        this.charCodeBasicCount = charCodeBasicCount.toUpperCase();
        TinyDbWrap.getInstance().putString(TinyDbKeys.KEYS.BASIC_CODE_COUNT_VALUTE, charCodeBasicCount.toUpperCase());
    }

    public void setBasicValute(Valute valute) {
        this.basicValute = valute;
        TinyDbWrap.getInstance().putLong(TinyDbKeys.KEYS.BASIC_VALUTE_CODE, valute.getNumCode());
    }
}
