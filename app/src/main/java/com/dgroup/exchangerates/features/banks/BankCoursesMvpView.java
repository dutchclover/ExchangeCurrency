package com.dgroup.exchangerates.features.banks;

import android.support.v4.util.Pair;

import com.dgroup.exchangerates.data.model.db.BankCourse;
import com.dgroup.exchangerates.ui.base.MvpView;
import com.dgroup.exchangerates.ui.view.custom.SearchBar;

import java.util.List;



public interface BankCoursesMvpView extends MvpView {

    void showLoading();

    void showData(List<BankCourse> courses);

    void showError();

    void filterBanks(CharSequence bank);

    void showCity(CharSequence city);

    void showSearch(Pair<Integer, Integer> coords, String hint, boolean autoComplete);

    void setSearchCallback(SearchBar.OnSearchListener searchCallback);

}
