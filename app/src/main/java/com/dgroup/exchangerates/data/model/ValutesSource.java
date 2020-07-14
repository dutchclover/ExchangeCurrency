package com.dgroup.exchangerates.data.model;

import com.dgroup.exchangerates.data.model.db.Valute;

import java.util.List;


public class ValutesSource {
    private List<Valute> valutes;
    private boolean isUpToDate;

    public ValutesSource(List<Valute> valutes, boolean isUpToDate) {
        this.valutes = valutes;
        this.isUpToDate = isUpToDate;
    }

    public boolean isUpToDate() {
        return isUpToDate;
    }

    public List<Valute> getValutes() {
        return valutes;
    }
}
