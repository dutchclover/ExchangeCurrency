package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.content.Context;
import android.graphics.Typeface;

import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;



public class PriceTableHolder extends TextTableHolder{

    public PriceTableHolder(Context context) {
        super(context);
    }

    @Override
    public void bind(CryptoMarketCurrencyInfo cryptoMarketCurrencyInfo) {
        mTextView.setText(String.format("$%s", String.valueOf(cryptoMarketCurrencyInfo.getPrice_usd())));
    }
}
