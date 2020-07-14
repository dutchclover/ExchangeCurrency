package com.dgroup.exchangerates.features.cb_rates.di;

import com.dgroup.exchangerates.data.RatesManager;
import com.dgroup.exchangerates.features.cb_rates.CBRatesPresenter;
import com.dgroup.exchangerates.utils.ActivityScoped;
import com.dgroup.exchangerates.utils.FragmentScoped;

import dagger.Module;
import dagger.Provides;


@Module
public class CBRatesPresenterModule {

    @Provides
    @ActivityScoped
    public CBRatesPresenter provideCBRatesPresenter(RatesManager ratesManager){
        return new CBRatesPresenter(ratesManager);
    }
}
