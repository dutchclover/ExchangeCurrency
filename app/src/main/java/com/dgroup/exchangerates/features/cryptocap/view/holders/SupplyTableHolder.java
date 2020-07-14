package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.content.Context;
import android.graphics.Typeface;

import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;

import static com.dgroup.exchangerates.utils.Utils.customFormat;



public class SupplyTableHolder extends TextTableHolder {

    public SupplyTableHolder(Context context) {
        super(context);
    }

    @Override
    public void bind(CryptoMarketCurrencyInfo cryptoMarketCurrencyInfo) {
        mTextView.setText(
                String.format("%s %s", customFormat("###,###.# M", cryptoMarketCurrencyInfo.getAvailable_supply() / 1000000),
                        cryptoMarketCurrencyInfo.getSymbol()));
    }
}
