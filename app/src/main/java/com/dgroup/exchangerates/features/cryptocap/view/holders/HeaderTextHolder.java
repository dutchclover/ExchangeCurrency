package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.content.Context;
import android.util.TypedValue;


public class HeaderTextHolder extends TextTableHolder{

    public HeaderTextHolder(Context context) {
        super(context);
        mTextView.setTextColor(mTextView.getContext().getResources()
                .getColor(android.R.color.black));
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
    }
}
