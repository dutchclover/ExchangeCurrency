package com.dgroup.exchangerates.features.main.di;

import com.dgroup.exchangerates.api.Api;
import com.dgroup.exchangerates.features.main.MainPresenter;
import com.dgroup.exchangerates.utils.ActivityScoped;

import java.util.Map;

import dagger.Module;
import dagger.Provides;
import rx.Observable;


@Module
public class MainNavigationModule {

    @Provides
    @ActivityScoped
    MainPresenter provideMainPresenter(Observable<Map<String, Integer>> cityCodesObsrv, Observable<Api> api){
        return new MainPresenter(cityCodesObsrv, api);
    }
}
