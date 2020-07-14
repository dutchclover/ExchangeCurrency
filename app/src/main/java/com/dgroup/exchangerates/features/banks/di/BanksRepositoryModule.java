package com.dgroup.exchangerates.features.banks.di;

import com.dgroup.exchangerates.api.Api;
import com.dgroup.exchangerates.data.BanksRepository;
import com.dgroup.exchangerates.utils.ActivityScoped;

import java.util.concurrent.Executor;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

@Module
public class BanksRepositoryModule {

    @Provides
    @ActivityScoped
    BanksRepository provideBanksRepository(Observable<Api> api, @Named("MultiThread") Executor executor){
        return new BanksRepository(api, executor);
    }
}
