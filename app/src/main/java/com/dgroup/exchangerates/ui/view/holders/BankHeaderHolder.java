package com.dgroup.exchangerates.ui.view.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dgroup.exchangerates.R;

/**
 * Created by gabber on 17.12.17.
 */

public class BankHeaderHolder extends RecyclerView.ViewHolder{

    private TextView headerText;

    public BankHeaderHolder(View itemView, int width) {
        super(itemView);
        headerText = (TextView) itemView.findViewById(R.id.header_text);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        params.width = width;
        itemView.setLayoutParams(params);
    }

    public void bind(String text){

        headerText.setText(text);
    }
}
