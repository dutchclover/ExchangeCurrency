package com.dgroup.exchangerates.ui.view.holders;

import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.model.db.BankCourse;
import com.dgroup.exchangerates.ui.adapter.BanksCoursesAdapter;
import com.dgroup.exchangerates.utils.TimeFormatter;

import java.util.Locale;


public class BankCourseHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private TextView usdSelText;
    private TextView usdBuyText;
    private TextView eurSellText;
    private TextView eurBuyText;
    private TextView time;

    private ForegroundColorSpan searchSpan;

    public BankCourseHolder(View itemView, BanksCoursesAdapter.OnBankClickListener onBankClickListener) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.bank_name);
        name.setPaintFlags(name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        usdBuyText = (TextView) itemView.findViewById(R.id.buy_usd);
        usdSelText = (TextView) itemView.findViewById(R.id.sell_usd);
        eurBuyText = (TextView) itemView.findViewById(R.id.buy_eur);
        eurSellText = (TextView) itemView.findViewById(R.id.sell_eur);
        time = (TextView) itemView.findViewById(R.id.date);
        searchSpan = new ForegroundColorSpan(ContextCompat.getColor(itemView.getContext(), R.color.select_text));
        itemView.setOnClickListener(v -> onBankClickListener.onBankClicked((BankCourse) itemView.getTag()));
    }

    public void bind(final BankCourse bankCourse, CharSequence searchString) {
        //  long startTime = System.currentTimeMillis();
        itemView.setTag(bankCourse);
        if (TextUtils.isEmpty(searchString)) {
            name.setText(bankCourse.getName());
        } else {
            int start = bankCourse.getName().toLowerCase().indexOf(searchString.toString().toLowerCase());
            if(start==-1){
                name.setText(bankCourse.getName());
                ExRatesApp.logException(new RuntimeException("BankCourseHolder "+bankCourse.getName()+" search "+searchString.toString().toLowerCase()));
            }else {
                Spannable spannable = new SpannableString(bankCourse.getName());
                spannable.setSpan(searchSpan, start, start + searchString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                name.setText(spannable, TextView.BufferType.SPANNABLE);
            }
        }
        usdBuyText.setText(String.format(Locale.getDefault(), "%.2f", bankCourse.getUsdBuy()));
        usdSelText.setText(String.format(Locale.getDefault(), "%.2f", bankCourse.getUsdSell()));
        eurBuyText.setText(String.format(Locale.getDefault(), "%.2f", bankCourse.getEurBuy()));
        eurSellText.setText(String.format(Locale.getDefault(), "%.2f", bankCourse.getEurSell()));
        time.setText(TimeFormatter.getInstance().getStringServTime(bankCourse.getTimestamp()));
        //  Log.i("BankCourseHolder", "bind time = " + (System.currentTimeMillis() - startTime));
    }

}
