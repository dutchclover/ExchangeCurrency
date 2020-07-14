package com.dgroup.exchangerates.ui.base;

public interface Presenter <V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}

