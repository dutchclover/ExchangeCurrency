package com.dgroup.exchangerates.features.main;


import android.support.v4.util.Pair;
import android.view.View;

import com.dgroup.exchangerates.ui.base.CustomBackPressedAction;
import com.dgroup.exchangerates.ui.base.MvpView;

public interface MainMvpView extends MvpView{

    void setAutoCompleteData(String[] autoCompleteData);

    void showSearch(Pair<Integer, Integer> coords, String string, boolean useCitiesHint);

    void hideSearch();

    void invalidateMenu(View menu);

    void setHoldBackMenu(CustomBackPressedAction customBackPressedAction);

    void setDrawerLockMode(int mode);
}
