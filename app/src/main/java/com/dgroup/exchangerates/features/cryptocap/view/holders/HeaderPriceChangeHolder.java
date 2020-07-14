package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.content.Context;
import android.view.View;

import com.dgroup.exchangerates.R;



public class HeaderPriceChangeHolder extends TextTableHolder{

    public HeaderPriceChangeHolder(Context context, View.OnClickListener onClickListener) {
        super(context);
        root.setOnClickListener(onClickListener);
        mTextView.setTextColor(context.getResources().getColor(R.color.text_uri));
        root.setClickable(true);

    }
}
