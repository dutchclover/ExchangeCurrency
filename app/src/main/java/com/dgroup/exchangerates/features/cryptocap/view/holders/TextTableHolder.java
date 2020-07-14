package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;


import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;



public class TextTableHolder extends BaseTableHolder{

    protected TextView mTextView;

    public TextTableHolder(Context context) {
        root = LayoutInflater.from(context).inflate(R.layout.holder_text, null);
        mTextView = (TextView) root.findViewById(R.id.text);
    }

    @Override
    public void bind(CryptoMarketCurrencyInfo cryptoMarketCurrencyInfo) {

    }

    public void bind(String text){
        mTextView.setText(text);
    }
}
