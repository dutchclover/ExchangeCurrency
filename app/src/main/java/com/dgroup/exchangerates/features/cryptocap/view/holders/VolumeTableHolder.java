package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.content.Context;

import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;

import java.util.Locale;

import static com.dgroup.exchangerates.utils.Utils.customFormat;



public class VolumeTableHolder extends TextTableHolder {

    public VolumeTableHolder(Context context) {
        super(context);
    }

    @Override
    public void bind(CryptoMarketCurrencyInfo cryptoMarketCurrencyInfo) {
        mTextView.setText(customFormat("$###,###.# M", cryptoMarketCurrencyInfo.getVolume_usd_24h() / 1000000));

       // mTextView.setText(String.format(Locale.getDefault(), "$%.1f M", (cryptoMarketCurrencyInfo.getVolume_usd_24h() / 1000000)));
    }
}
