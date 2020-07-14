package com.dgroup.exchangerates.features.cb_rates.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.model.BasicContent;
import com.dgroup.exchangerates.data.model.db.Valute;
import com.dgroup.exchangerates.ui.base.BaseActivity;
import com.dgroup.exchangerates.ui.view.custom.CustomEditText;
import com.dgroup.exchangerates.utils.Utils;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by dimon for Snaappy.
 */
public class KeyboardActivity extends BaseActivity implements CustomEditText.HandleDismissingKeyboard {

    public static final String PARAM_VALUTE = "valute";

    private CustomEditText mCount;
    private double relBasic;
    private double oldValue;
    private String currencySymbol;

    @Inject
    protected BasicContent basicContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        ExRatesApp.getApp().obtainAppComponent().inject(this);
        oldValue = basicContent.getCount();

        Valute valute = getIntent().getParcelableExtra(PARAM_VALUTE);
        currencySymbol = Utils.getCurrencySymbol(valute.getCharCode());

        ImageView mFlag = (ImageView) findViewById(R.id.flag);
        TextView mName = (TextView) findViewById(R.id.name);

        findViewById(R.id.clear).setOnClickListener(v ->
                mCount.setText(mCount.getText().subSequence(0, currencySymbol.length() + 1))
        );

        mCount = (CustomEditText) findViewById(R.id.count);
        mCount.setStartSelection(currencySymbol.length() + 1);

        int maxLength = 10 + currencySymbol.length() + 1;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        mCount.setFilters(fArray);

        mFlag.setImageDrawable(ExRatesApp.getApp().getDrawable(valute.getCharCode().toLowerCase()));
        mName.setText(valute.getName());

        relBasic = valute.getValue() / basicContent.getBasicValute().getValue() * basicContent.getBasicValute().getIntNominal();
        Log.i("KeyboardActivity", "relBasic " + relBasic);
        mCount.setText(String.format(Locale.getDefault(), "%s %.2f", currencySymbol, basicContent.getCount() / relBasic * valute.getIntNominal()));
        mCount.setSelection(mCount.getText().length());
        mCount.setHandleDismissingKeyboard(this);
        mCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (s.charAt(s.length() - 1) == '.') {
                        mCount.removeTextChangedListener(this);
                        mCount.setText(s.toString().replace(".", ","));
                        mCount.addTextChangedListener(this);
                        mCount.setSelection(s.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().contains(currencySymbol)) {
                    mCount.setText(String.format("%s %s", currencySymbol, s.toString()));
                    mCount.setSelection(s.length());
                } else if (s.length() == currencySymbol.length()) {
                    mCount.setText(mCount.getText() + " ");
                }
                if (s.length() > currencySymbol.length()) {
                    try {
                        double result = Utils.parseDouble(s.toString().substring(currencySymbol.length())) * relBasic / valute.getIntNominal();
                        Log.i("KeyboardActivity", "result " + result);
                        basicContent.setCount(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    basicContent.setCount(oldValue);
                }

            }
        });
    }

    @Override
    public void dismissKeyboard() {
        finish();
    }

    @Override
    public void finish() {
        Log.i("KeyboardActivity", "finish oldValue " + oldValue + " new value " + basicContent.getCount());
        if (Math.abs(oldValue - basicContent.getCount()) > 0.001) {
            setResult(RESULT_OK);
        }
        super.finish();
    }
}
