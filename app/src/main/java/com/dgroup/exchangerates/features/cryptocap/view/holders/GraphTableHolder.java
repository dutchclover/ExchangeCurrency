package com.dgroup.exchangerates.features.cryptocap.view.holders;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.MemKeyGenerator;



public class GraphTableHolder extends BaseTableHolder {

    private ImageView mImageView;
    private String graphUrl;

    public GraphTableHolder(Context context) {
        mImageView = new ImageView(context);
        mImageView.setBackgroundResource(R.drawable.bg_border);
        root = mImageView;
    }

    @Override
    public void bind(CryptoMarketCurrencyInfo cryptoMarketCurrencyInfo) {
        root = mImageView;
        if(cryptoMarketCurrencyInfo.getAndClearRedraw()){
            String memoryCacheKey = MemKeyGenerator.getkey(cryptoMarketCurrencyInfo.getGraph(), ExRatesApp.getApp().getImageLoaderConfig());
//            Log.i("GraphTableHolder","remove key "+memoryCacheKey);
            ImageLoader.getInstance().getMemoryCache().remove(memoryCacheKey);
            ImageLoader.getInstance().getDiskCache().remove(cryptoMarketCurrencyInfo.getGraph());
        }else if (cryptoMarketCurrencyInfo.getGraph() == null || cryptoMarketCurrencyInfo.getGraph().equals(graphUrl)){
//            Log.i("GraphTableHolder","already showed "+cryptoMarketCurrencyInfo.getGraph());
            return;
        }
        graphUrl = cryptoMarketCurrencyInfo.getGraph();
        ImageLoader.getInstance().displayImage(graphUrl, mImageView);
    }
}