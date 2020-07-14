package com.dgroup.exchangerates.app.di;

import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.api.Api;
import com.dgroup.exchangerates.app.ExRatesApp;
import com.dgroup.exchangerates.data.CryptoCurrencyRepoImpl;
import com.dgroup.exchangerates.features.cryptocap.domain.CryptoCurrencyRepo;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.schedulers.Schedulers;

@Module
public class ApplicationModule {

    private final Context mContext;

    private static final int KEEP_ALIVE = 10;

    public ApplicationModule(Context context) {
        this.mContext = context;
    }

    @Provides
    @Singleton
    Map<String, Integer> provideCityCodes(Context context) {
        Map<String, Integer> cityCodes = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.codes);
            ObjectInputStream ois = new ObjectInputStream(is);
            cityCodes = (Map<String, Integer>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        return cityCodes;
    }

    @Provides
    @Singleton
    Observable<Map<String, Integer>> provideCityCodesObsrv(
            final Lazy<Map<String, Integer>> cityCodesProvider) {
        return Observable.fromCallable(cityCodesProvider::get)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    @Provides
    @Singleton
    public Context provideApplication() {
        return mContext;
    }

    @Provides
    @Singleton
    @Named("MultiThread")
    public Executor getBasicThreadPoolExecutor() {
        BlockingQueue<Runnable> sPoolWorkQueue =
                new LinkedBlockingQueue<>(128);

        ThreadFactory sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "BasicThreadPool_" + mCount.getAndIncrement());
            }
        };
        final int cpuCount = Runtime.getRuntime().availableProcessors();
        final int corePoolSize = cpuCount + 1;
        int maximumPoolSize = cpuCount * 2;
        if (maximumPoolSize == 0) {
            ExRatesApp.logException(new RuntimeException("availableProcessors = 0"));
            maximumPoolSize = 4;
        }
        Executor executor;
        try {
            executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, KEEP_ALIVE,
                    TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        } catch (IllegalArgumentException e) {
            ExRatesApp.logException(e);
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    @Provides
    @Singleton
    @Named("SingleThread")
    public Executor getWebThreadPoolExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Singleton
    CryptoCurrencyRepo provideCryptoCurrencyRepo(
            Observable<Api> apiObservable) {
        return new CryptoCurrencyRepoImpl(apiObservable);
    }
}
