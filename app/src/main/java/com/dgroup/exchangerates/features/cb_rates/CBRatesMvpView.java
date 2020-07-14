package com.dgroup.exchangerates.features.cb_rates;

import com.dgroup.exchangerates.data.model.BasicContent;
import com.dgroup.exchangerates.data.model.db.Valute;
import com.dgroup.exchangerates.ui.base.MvpView;

import java.util.List;


public interface CBRatesMvpView extends MvpView{

    void showList(List<Valute> valutes, boolean showHint);

    void showLoading(boolean isShown);

    void showError();

    void setBasicContent(BasicContent basicContent, boolean showHint);

    void notifyMovedItems(int from, int to);

}
