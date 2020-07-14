package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.dgroup.exchangerates.features.cryptocap.data.CryptoCurrencyHeader;



public class HeaderNameHolder extends BaseTableHolder{

    private TextView number;
    private TextView name;

    @SuppressLint("InflateParams")
    public HeaderNameHolder(Context context) {
        root = LayoutInflater.from(context).inflate(R.layout.holder_header_name, null);
        number = (TextView) root.findViewById(R.id.number);
        name = (TextView) root.findViewById(R.id.name);
    }

    @Override
    public void bind(CryptoMarketCurrencyInfo cryptoMarketCurrencyInfo) {
        number.setText(CryptoCurrencyHeader.getNumber());
        name.setText(CryptoCurrencyHeader.getName());
    }
}
