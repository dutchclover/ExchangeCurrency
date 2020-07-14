package com.dgroup.exchangerates.ui.view.holders;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.model.BasicContent;
import com.dgroup.exchangerates.data.model.db.Valute;
import com.dgroup.exchangerates.features.cb_rates.listeners.ItemTouchHelperViewHolder;
import com.dgroup.exchangerates.ui.adapter.ValuteRatesAdapter;
import com.dgroup.exchangerates.utils.Utils;

import java.util.Locale;

/**
 * Created by dmitriy on 10.02.15.
 */
public class ValuteViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    private ValuteRatesAdapter.OnItemClickListener mOnClickListener;

    private View rootView;

    private TextView name;
    private TextView value;
    private ImageView flag;
    private View dragImage;
    private TextView count;

    private Valute currentValute;
    private BasicContent basicContent;

    @SuppressLint("ClickableViewAccessibility")
    public ValuteViewHolder(View view, ValuteRatesAdapter.OnItemClickListener onItemTouchListener) {
        super(view);
        this.mOnClickListener = onItemTouchListener;
        rootView = view;
        name = (TextView) view.findViewById(R.id.name);
        value = (TextView) view.findViewById(R.id.value);
        flag = (ImageView) view.findViewById(R.id.flag);
        //diffImage = (ImageView) view.findViewById(R.id.diffImage);
        count = (TextView) view.findViewById(R.id.count);
        dragImage = view.findViewById(R.id.drag);
        dragImage.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                mOnClickListener.startDrug(ValuteViewHolder.this);
            }
            return false;
        });
        rootView.setOnClickListener(v -> mOnClickListener.onClick(v));
    }

    public void bind(Valute valute, BasicContent basicContent, boolean dragMode) {
        rootView.setEnabled(!dragMode);
        dragImage.setVisibility(dragMode ? View.VISIBLE : View.GONE);
        long start = System.currentTimeMillis();
        this.currentValute = valute;
        this.basicContent = basicContent;
        rootView.setTag(valute);
        name.setText(valute.getName());

        if (!valute.equals(flag.getTag())) {
            flag.setImageDrawable(ExRatesApp.getApp().getDrawable(valute.getCharCode().toLowerCase()));
            flag.setTag(valute);
        }

        bindValue();

        Log.i("ValuteViewHolder", "bind valute time " + (System.currentTimeMillis() - start));
    }

    private void bindValue() {
        String symbol = Utils.getCurrencySymbol(currentValute.getCharCode());
        if (currentValute.getCharCode().equals(basicContent.getCharCodeBasicCount())) {
            value.setText(itemView.getContext().getString(R.string.valute_basic, currentValute.getNominal(), currentValute.getCharCode()));
            count.setText(String.format(Locale.getDefault(), "%s %.2f", symbol,
                    basicContent.getCount()));
        } else {
            double relBasic = currentValute.getValue() / basicContent.getBasicValute().getValue() * basicContent.getBasicValute().getIntNominal();
            value.setText(
                    itemView.getContext().getString(R.string.valute_equals, currentValute.getNominal(), currentValute.getCharCode(),
                            String.format(Locale.getDefault(), "%.4f", relBasic), basicContent.getBasicValute().getCharCode()));
            String val = String.format(Locale.getDefault(), "%s %.2f",
                    symbol, basicContent.getCount() / relBasic * currentValute.getIntNominal());
            Log.i("ValuteViewHolder", " name " + currentValute.getName() + " val " + val);
            count.setText(val);
        }
    }

    @Override
    public void onItemSelected() {
        Log.i("ValuteViewHolder", "onItemSelected pos " + getAdapterPosition());
    }

    @Override
    public void onItemClear() {
        Log.i("ValuteViewHolder", "onItemClear  pos " + getAdapterPosition());
        bindValue();
    }

    public Valute getCurrentValute() {
        return currentValute;
    }
}
