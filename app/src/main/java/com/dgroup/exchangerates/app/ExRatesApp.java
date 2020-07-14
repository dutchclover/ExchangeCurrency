package com.dgroup.exchangerates.app;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.dgroup.exchangerates.BuildConfig;
import com.dgroup.exchangerates.R;
import com.dgroup.exchangerates.app.di.ApiModule;
import com.dgroup.exchangerates.app.di.ApplicationModule;
import com.dgroup.exchangerates.app.di.DaggerIExRatesAppComponent;
import com.dgroup.exchangerates.app.di.IExRatesAppComponent;
import com.dgroup.exchangerates.data.db_wrapper.DaoTaskMain;
import com.dgroup.exchangerates.data.model.db.DaoSession;
import com.dgroup.exchangerates.features.banks.di.BankCoursesComponent;
import com.dgroup.exchangerates.features.banks.di.BankCoursesPresenterModule;
import com.dgroup.exchangerates.features.banks.di.BanksRepositoryModule;
import com.dgroup.exchangerates.features.cb_rates.di.BasicContentModule;
import com.dgroup.exchangerates.features.cb_rates.di.CBRatesComponent;
import com.dgroup.exchangerates.features.cb_rates.di.CBRatesPresenterModule;
import com.dgroup.exchangerates.features.cb_rates.di.CBRepositoryModule;
import com.dgroup.exchangerates.features.cryptocap.di.CryptoCapitalComponent;
import com.dgroup.exchangerates.features.cryptocap.di.CryptoCapitalModule;
import com.dgroup.exchangerates.features.main.di.MainNavigationComponent;
import com.dgroup.exchangerates.features.main.di.MainNavigationModule;
import com.dgroup.exchangerates.features.main.di.RepositoryComponent;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;
import com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.squareup.leakcanary.LeakCanary;


import java.util.Locale;

import io.fabric.sdk.android.Fabric;


public class ExRatesApp extends MultiDexApplication {

    public static final String TAG = ExRatesApp.class.getSimpleName();

    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

    private IExRatesAppComponent mIExRatesAppComponent;
    private RepositoryComponent mRepositoryComponent;
    private BankCoursesComponent mBankCoursesComponent;
    private CBRatesComponent mCBRatesComponent;
    private CryptoCapitalComponent mCryptoCapitalComponent;
    private MainNavigationComponent mMainNavigationComponent;

    private static ExRatesApp instance;
    private ImageLoaderConfiguration mImageLoaderConfig;

    public static ExRatesApp getApp() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        if (BuildConfig.DEBUG) {
            AndroidDevMetrics.initWith(this);
        }

        long start = System.currentTimeMillis();

        TinyDbWrap.getInstance().init(getApplicationContext());

        if (BuildConfig.IS_USE_CRASHLYTICS) {
            Fabric.with(this, new Crashlytics());
        }
        new Thread(new DaoTaskMain() {
            @Override
            protected void onExecuteBackground(DaoSession daoSession) throws Throwable {
            }
        }).start();

        Log.i("locale", Locale.getDefault().toString());

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(android.R.color.transparent)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(200, true, true, false))
                .build();

        ImageLoaderConfiguration.Builder lBuilder = new ImageLoaderConfiguration.Builder(this);

        lBuilder.defaultDisplayImageOptions(defaultOptions)
                .threadPoolSize(3)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCacheSizePercentage(22)
                .diskCacheSize(DISK_CACHE_SIZE);
        if (BuildConfig.DEBUG) {
            lBuilder.writeDebugLogs();
        }
        mImageLoaderConfig = lBuilder.build();

        ImageLoader.getInstance().init(mImageLoaderConfig);

        obtainAppComponent();
        Log.i(TAG, " time = " + (System.currentTimeMillis() - start));
    }

    public static void logException(Throwable throwable) {
        throwable.printStackTrace();
        if (!BuildConfig.DEBUG) {
            Crashlytics.logException(throwable);
        }
    }

    public Drawable getDrawable(String name) {
        if (name.equals("try")) {
            name += "1";
        }

        int resourceId = getResources().getIdentifier(name, "drawable", getPackageName());
        Drawable drawable;
        try {
            drawable = ContextCompat.getDrawable(this, resourceId);
        } catch (Exception e) {
            drawable = ContextCompat.getDrawable(this, R.drawable.ic_launcher);
        }
        return drawable;
    }

    public IExRatesAppComponent obtainAppComponent() {
        if (mIExRatesAppComponent == null) {
            mIExRatesAppComponent = DaggerIExRatesAppComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .apiModule(new ApiModule())
                    .basicContentModule(new BasicContentModule())
                    .build();
        }
        return mIExRatesAppComponent;
    }

    public RepositoryComponent plusRepositoryComponent() {
        if (mRepositoryComponent == null) {
            mRepositoryComponent = mIExRatesAppComponent.plus(
                    new BanksRepositoryModule(), new CBRepositoryModule());
        }
        return mRepositoryComponent;
    }

    public BankCoursesComponent plusBankCoursesComponent() {
        if (mBankCoursesComponent == null) {
            mBankCoursesComponent = plusRepositoryComponent().plus(new BankCoursesPresenterModule());
        }
        return mBankCoursesComponent;
    }

    public CBRatesComponent plusCBRatesComponent() {
        if (mCBRatesComponent == null) {
            mCBRatesComponent = plusRepositoryComponent().plus(new CBRatesPresenterModule());
        }
        return mCBRatesComponent;
    }

    public CryptoCapitalComponent plusCryptoCapitalComponent() {
        if (mCryptoCapitalComponent == null) {
            mCryptoCapitalComponent = plusRepositoryComponent().plus(new CryptoCapitalModule());
        }
        return mCryptoCapitalComponent;
    }

    public MainNavigationComponent plusMainNavigationComponent() {
        if (mMainNavigationComponent == null) {
            mMainNavigationComponent = mIExRatesAppComponent.plus(new MainNavigationModule());
        }
        return mMainNavigationComponent;
    }

    public void clearBankCoursesComponent() {
        mBankCoursesComponent = null;
    }

    public ImageLoaderConfiguration getImageLoaderConfig(){
        return mImageLoaderConfig;
    }
}
