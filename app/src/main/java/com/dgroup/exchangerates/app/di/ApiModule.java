package com.dgroup.exchangerates.app.di;

import android.content.Context;

import com.dgroup.exchangerates.api.Api;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.schedulers.Schedulers;

@Module
public class ApiModule {

    @Provides
    @Singleton
    Api provideApi(Context context) {
        return new Api(context);
    }

    @Provides
    @Singleton
    Observable<Api> provideHApiObservable(
            final Lazy<Api> apiProvider) {
        return Observable.fromCallable(apiProvider::get)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

}
