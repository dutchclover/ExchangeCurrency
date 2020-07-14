package com.dgroup.exchangerates.features.main;

import android.util.Log;

import com.dgroup.exchangerates.api.Api;
import com.dgroup.exchangerates.ui.base.BasePresenter;
import com.dgroup.exchangerates.utils.constants.TinyDbKeys;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.subscriptions.CompositeSubscription;

public class MainPresenter extends BasePresenter<MainMvpView, Void> {

    private Observable<Map<String, Integer>> cityCodesObsrv;
    private Observable<Api> api;
    private CompositeSubscription mCompositeSubscription;
    private String[] cities;


    @Inject
    public MainPresenter(Observable<Map<String, Integer>> cityCodesObsrv, Observable<Api> api) {
        mCompositeSubscription = new CompositeSubscription();
        this.cityCodesObsrv = cityCodesObsrv.cache();
        this.api = api.cache();
        obtainCities();
        obtainCurrentCity();
    }

    public void subscribe(MainMvpView mainMvpView) {
        attachView(mainMvpView);
        if (cities != null) {
            getMvpView().setAutoCompleteData(cities);
        }
    }

    public void unsubscribe() {
        detachView();
        mCompositeSubscription.clear();
    }

    private void obtainCities() {
        mCompositeSubscription.add(
                cityCodesObsrv.map(stringIntegerMap ->
                        stringIntegerMap.keySet().toArray(
                                new String[stringIntegerMap.values().size()])).
                        subscribe(strings -> {
                            if (isViewAttached()) {
                                getMvpView().setAutoCompleteData(strings);
                            } else {
                                cities = strings;
                            }
                        }, Throwable::printStackTrace));
    }

    private void obtainCurrentCity() {
        mCompositeSubscription.add(
                Observable.zip(api, cityCodesObsrv,
                        (api, stringIntegerMap) -> {
                            try {
                                String currentCity = api.getCurrentCity();
                                Integer code = stringIntegerMap.get(currentCity);
                                if (code != null) {
                                    TinyDbWrap.getInstance().putInt(TinyDbKeys.KEYS.SAVED_CITY_CODE, code);
                                    TinyDbWrap.getInstance().putString(TinyDbKeys.KEYS.SAVED_CITY_NAME, currentCity);
                                    return code;
                                }
                                throw Exceptions.propagate(new NullPointerException());
                            } catch (Exception e) {
                                throw Exceptions.propagate(e);
                            }
                        }).
                        subscribe(integer -> {
                            Log.i("obtainCurrentCity", "current city code " + integer);
                        }, Throwable::printStackTrace));

    }
}
