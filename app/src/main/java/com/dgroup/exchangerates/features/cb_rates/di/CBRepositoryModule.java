package com.dgroup.exchangerates.features.cb_rates.di;


import com.dgroup.exchangerates.api.Api;
import com.dgroup.exchangerates.data.RatesManager;
import com.dgroup.exchangerates.data.model.BasicContent;
import com.dgroup.exchangerates.utils.ActivityScoped;

import java.util.concurrent.Executor;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

@Module
public class CBRepositoryModule {

    @Provides
    @ActivityScoped
    RatesManager provideRatesManager(Observable<Api> api, @Named("MultiThread") Executor executor, BasicContent basicContent){
        return new RatesManager(api, executor, basicContent);
    }
}
