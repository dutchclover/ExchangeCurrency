package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.content.Context;
import android.util.Log;
import android.view.View;




public class HolderFactory {

    public static final int HEADER_NAME = 7;

    public static final int HEADER_TEXT = 8;

    public static final int HEADER_PRICE_CHANGE = 9;

    public static BaseTableHolder createHolder(Context context, int type, View.OnClickListener onChangePriceListener) {
        Log.i("HolderFactory", "createHolder " + type);
        BaseTableHolder baseTableHolder;
        switch (type) {
            case 0:
                baseTableHolder = new NameTableHolder(context);
                break;
            case 1:
                baseTableHolder = new PriceTableHolder(context);
                break;
            case 2:
                baseTableHolder = new PriceChangeTableHolder(context);
                break;
            case 3:
                baseTableHolder = new CapitalTableHolder(context);
                break;
            case 4:
                baseTableHolder = new SupplyTableHolder(context);
                break;
            case 5:
                baseTableHolder = new VolumeTableHolder(context);
                break;
            case 6:
                baseTableHolder = new GraphTableHolder(context);
                break;
            case HEADER_NAME:
                baseTableHolder = new HeaderNameHolder(context);
                break;
            case HEADER_TEXT:
                baseTableHolder = new HeaderTextHolder(context);
                break;
            case HEADER_PRICE_CHANGE:
                baseTableHolder = new HeaderPriceChangeHolder(context,onChangePriceListener);
                break;
            default:
                throw new RuntimeException("holder type not declared");
        }
        return baseTableHolder;
    }
}
