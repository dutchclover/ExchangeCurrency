package com.dgroup.exchangerates.features.cryptocap;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.dgroup.exchangerates.features.cryptocap.data.CryptoCurrencyHeader;
import com.dgroup.exchangerates.features.cryptocap.view.holders.BaseTableHolder;
import com.dgroup.exchangerates.features.cryptocap.view.holders.HolderFactory;
import com.dgroup.exchangerates.features.cryptocap.view.holders.TextTableHolder;
import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.dgroup.exchangerates.features.cryptocap.view.holders.HolderFactory.HEADER_NAME;
import static com.dgroup.exchangerates.features.cryptocap.view.holders.HolderFactory.HEADER_PRICE_CHANGE;
import static com.dgroup.exchangerates.features.cryptocap.view.holders.HolderFactory.HEADER_TEXT;



public class TableAdapter extends BaseTableAdapter {

    private final Context context;

    private List<CryptoMarketCurrencyInfo> content;
    private View.OnClickListener onChangePriceListener;

    private int changePriceInterval;

    public TableAdapter(Context context, int changePriceInterval) {
        this.context = context;
        this.changePriceInterval = changePriceInterval;
        content = new ArrayList<>();
    }

    public void setContent(List<CryptoMarketCurrencyInfo> content) {
        this.content = content;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        return content.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {
//        row += 1;
//        column += 1;
        BaseTableHolder holder;
        if (convertView == null) {
            holder = HolderFactory.createHolder(context, getItemViewType(row, column), onChangePriceListener);
        } else {
            holder = (BaseTableHolder) convertView.getTag(R.id.holder_tag);
        }

        if (row != -1) {
            holder.setPriceChangeInterval(changePriceInterval);
            holder.bind(content.get(row));
        } else if (column == -1) {
            //header first column
            holder.bind(null);
        } else {
            //headers
            TextTableHolder textTableHolder = (TextTableHolder) holder;
            switch (column) {
                case 0:
                    textTableHolder.bind(CryptoCurrencyHeader.getPrice());
                    break;
                case 1:
                    textTableHolder.bind(getHeaderForPriceChange());
                    break;
                case 2:
                    textTableHolder.bind(CryptoCurrencyHeader.getCapital());
                    break;
                case 3:
                    textTableHolder.bind(CryptoCurrencyHeader.getSupply());
                    break;
                case 4:
                    textTableHolder.bind(CryptoCurrencyHeader.getVolume());
                    break;
                case 5:
                    textTableHolder.bind(CryptoCurrencyHeader.getGraph());
                    break;
            }

        }
        convertView = holder.getView();
        convertView.setTag(R.id.holder_tag, holder);
        return convertView;
    }

    @Override
    public int getHeight(int row) {
        if (row == -1) {
            return 150;
        }
        return 200;
    }

    @Override
    public int getWidth(int column) {
        column++;
        switch (column) {
            case 0:
                return context.getResources().getDimensionPixelSize(R.dimen.holder_name_w);
            case 1:
                return context.getResources().getDimensionPixelSize(R.dimen.holder_price_w);
            case 2:
                return context.getResources().getDimensionPixelSize(R.dimen.holder_change_w);
            case 3:
                return context.getResources().getDimensionPixelSize(R.dimen.holder_capital_w);
            case 4:
                return context.getResources().getDimensionPixelSize(R.dimen.holder_supply_w);
            case 5:
                return context.getResources().getDimensionPixelSize(R.dimen.holder_volume_w);
            case 6:
                return context.getResources().getDimensionPixelSize(R.dimen.holder_graph_w);
            default:
                throw new RuntimeException("column not exist " + column);
        }
    }

    @Override
    public int getItemViewType(int row, int column) {
        if (row == -1) {
            if (column == -1) {
                return HEADER_NAME;
            } else if(column == 1){
                return HEADER_PRICE_CHANGE;
            }else{
                return HEADER_TEXT;
            }
        } else {
            return column + 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 10;
    }

    public void setOnChancePriceClickListener(View.OnClickListener onChancePriceClickListener) {
        this.onChangePriceListener = onChancePriceClickListener;
    }


    public void setChangePriceInterval(int interval) {
        this.changePriceInterval = interval;
        notifyDataSetChanged();
    }

    private String getHeaderForPriceChange() {
        switch (changePriceInterval) {
            case 0:
                return CryptoCurrencyHeader.getChange1h();
            case 1:
                return CryptoCurrencyHeader.getChange24h();
            case 2:
                return CryptoCurrencyHeader.getChange7d();
            default:
                return CryptoCurrencyHeader.getChange24h();
        }
    }

}

