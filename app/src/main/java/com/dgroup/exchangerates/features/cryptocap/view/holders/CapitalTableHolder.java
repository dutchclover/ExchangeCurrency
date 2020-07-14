package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.content.Context;
import android.graphics.Typeface;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;

import static com.dgroup.exchangerates.utils.Utils.customFormat;



public class CapitalTableHolder extends TextTableHolder {

    public CapitalTableHolder(Context context) {
        super(context);
    }

    @Override
    public void bind(CryptoMarketCurrencyInfo cryptoMarketCurrencyInfo) {
        mTextView.setText(customFormat("$###,###.# M", cryptoMarketCurrencyInfo.getMarket_cap_usd() / 1000000));
    }
}
