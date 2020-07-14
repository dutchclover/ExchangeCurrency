package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.nostra13.universalimageloader.core.ImageLoader;



public class NameTableHolder extends BaseTableHolder{

    private TextView number;
    private TextView name;
    private TextView code;
    private ImageView icon;

    @SuppressLint("InflateParams")
    public NameTableHolder(Context context) {
        root = LayoutInflater.from(context).inflate(R.layout.holder_table_name, null);
        number = (TextView) root.findViewById(R.id.number);
        name = (TextView) root.findViewById(R.id.name);
        code = (TextView) root.findViewById(R.id.code);
        icon = (ImageView) root.findViewById(R.id.icon);
    }

    @Override
    public void bind(CryptoMarketCurrencyInfo cryptoMarketCurrencyInfo) {
        number.setText(String.valueOf(cryptoMarketCurrencyInfo.getRank()));
        name.setText(cryptoMarketCurrencyInfo.getName());
        code.setText(cryptoMarketCurrencyInfo.getSymbol());
        ImageLoader.getInstance().displayImage(cryptoMarketCurrencyInfo.getIcon(), icon);
    }
}
