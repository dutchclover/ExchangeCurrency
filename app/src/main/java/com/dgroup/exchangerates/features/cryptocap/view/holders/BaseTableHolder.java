package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.view.View;

import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.dgroup.exchangerates.features.cryptocap.data.CryptoCurrencyHeader;



public abstract class BaseTableHolder {

    protected View root;

    public abstract void bind(CryptoMarketCurrencyInfo cryptoMarketCurrencyInfo);

    public View getView(){
        return root;
    }

    public void setPriceChangeInterval(int interval){}
}
