package com.dgroup.exchangerates.features.cryptocap.view;

import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.dgroup.exchangerates.ui.base.MvpView;

import java.util.List;



public interface CryptoCapitalView extends MvpView{

    void showContent(List<CryptoMarketCurrencyInfo> content, long date);

    void showError(Throwable e);

    void showLoading(boolean show);
}
