package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.content.Context;
import android.view.View;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;

import java.util.Locale;



public class PriceChangeTableHolder extends TextTableHolder {

    private int interval;

    public PriceChangeTableHolder(Context context) {
        super(context);
    }

    @Override
    public void bind(CryptoMarketCurrencyInfo cryptoMarketCurrencyInfo) {
        float change = getChangePriceByInterval(cryptoMarketCurrencyInfo, interval);
        mTextView.setText(String.format(Locale.GERMAN, "%.2f %%", change));
        if (change < 0) {
            mTextView.setTextColor(mTextView.getContext().getResources()
                    .getColor(R.color.red));
        } else {
            mTextView.setTextColor(mTextView.getContext().getResources()
                    .getColor(R.color.green));
        }
    }

    @Override
    public void setPriceChangeInterval(int interval) {
        this.interval = interval;
    }

    private float getChangePriceByInterval(CryptoMarketCurrencyInfo cryptoMarketCurrencyInfo, int interval) {
        switch (interval) {
            case 0:
                return cryptoMarketCurrencyInfo.getPercent_change_1h();
            case 1:
                return cryptoMarketCurrencyInfo.getPercent_change_24h();
            case 2:
                return cryptoMarketCurrencyInfo.getPercent_change_7d();
            default:
                return 0;
        }
    }
}
