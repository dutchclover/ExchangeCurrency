package com.dgroup.exchangerates.data;


import android.util.Log;

import com.dgroup.exchangerates.api.Api;
import com.dgroup.exchangerates.data.db_wrapper.DaoTaskMain;
import com.dgroup.exchangerates.data.model.BasicContent;
import com.dgroup.exchangerates.data.model.ValCurses;
import com.dgroup.exchangerates.data.model.ValutesSource;
import com.dgroup.exchangerates.data.model.WValute;
import com.dgroup.exchangerates.data.model.db.Valute;
import com.dgroup.exchangerates.data.model.db.ValutePosition;
import com.dgroup.exchangerates.utils.ActivityScoped;
import com.dgroup.exchangerates.utils.TimeFormatter;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.dgroup.exchangerates.utils.constants.TinyDbKeys.KEYS.VALUTE_UPDATED;



@ActivityScoped
public class RatesManager extends Interactor<List<Valute>, RatesManager.Params> {

    private final Observable<Api> apiProvider;

    private BasicContent basicContent;
    private Valute rubValute;

    @Inject
    public RatesManager(Observable<Api> apiProvider, Executor executor, BasicContent basicContent) {
        super(executor);
        this.apiProvider = apiProvider.cache();
        this.basicContent = basicContent;
    }

    private Observable<ValutesSource> loadRatesFromRCB() {
        return apiProvider
                .map((Func1<Api, ValutesSource>) api -> {
                    try {
                        Log.i("RatesLoader", "Api loadRatesFromRCB " + Thread.currentThread().getName());
                        ValCurses valCurses = api.getRatesFromRCB(TimeFormatter.getInstance().getApiCBDate(System.currentTimeMillis()));
                        Collections.sort(valCurses.valutes, (o1, o2) -> o1.name.compareTo(o2.name));
                        ArrayList<Valute> valutes = new ArrayList<>();
                        int k = 0;
                        boolean isFirst = DaoTaskMain.getSession().getValutePositionDao().count() < 2;
                        Log.i("RatesLoader", "isFirst " + isFirst);
                        for (WValute wValute : valCurses.valutes) {
                            Valute valute = new Valute(wValute);
                            if (isFirst) {
                                ValutePosition valutePosition = new ValutePosition(valute.getNumCode());
                                valutePosition.setPosition(++k);
                                valute.setPosition(valutePosition);
                                DaoTaskMain.getSession().getValutePositionDao().insert(valutePosition);
                            } else {
                                valute.__setDaoSession(DaoTaskMain.getSession());
                                valute.getPosition();
                            }

                            valutes.add(valute);
                        }
                        TinyDbWrap.getInstance().putLong(VALUTE_UPDATED, System.currentTimeMillis());
                        valutes.add(obtainRubValute());
                        return new ValutesSource(valutes, true);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        throw Exceptions.propagate(e);
                    }
                }).doOnNext(valutesSource -> {
                    Log.i("RatesLoader", "save to db " + Thread.currentThread().getName() + " valutes size " + valutesSource.getValutes().size());

                    DaoTaskMain.getSession().getValuteDao().insertOrReplaceInTx(valutesSource.getValutes());
                });
    }

    private Observable<ValutesSource> loadRatesFromCache() {
        Log.i("RatesLoader", " loadRatesFromCache ");
        return Observable.defer(() ->
                Observable.just(DaoTaskMain.getSession().getValuteDao().loadAll()))
                .map(valutes -> {
                    for (Valute valute : valutes) {
                        valute.getPosition();
                    }
                    long lastUpdate = TinyDbWrap.getInstance().getLong(VALUTE_UPDATED, 0);
                    boolean isUpToDate = Math.abs(System.currentTimeMillis() - lastUpdate) < TimeFormatter.HOUR * 6;

                    return new ValutesSource(valutes, isUpToDate);
                });
    }

    public BasicContent getBasicContent() {
        return basicContent;
    }

    public boolean changeBasicContent(Valute valute) {
        if (basicContent.getBasicValute().equals(valute)) {
            return false;
        }
        basicContent.setBasicValute(valute);
        basicContent.setCharCodeBasicCount(valute.getCharCode());
        return true;
    }

    @Override
    protected Observable<List<Valute>> buildObservable(Params parameter) {
        Observable<ValutesSource> observable = parameter.isForce() ? loadRatesFromRCB() :
                Observable.concat(
                        loadRatesFromCache(),
                        loadRatesFromRCB())
                        .first(ValutesSource::isUpToDate)
                        .onErrorResumeNext(loadRatesFromCache());

        return observable.flatMap(valutesSource -> Observable.just(valutesSource.getValutes()));
    }

    public void swapPositions(HashMap<Valute, Integer> valuteIntegerHashMap, Action1<Boolean> onNext) {
        customExecute(Observable.defer(() ->
                Observable.just(updatePositions(valuteIntegerHashMap))), onNext);
    }

    private boolean updatePositions(HashMap<Valute, Integer> valuteIntegerHashMap) {
        for (Map.Entry<Valute, Integer> entity : valuteIntegerHashMap.entrySet()) {
            if (entity.getKey().getPosition().getPosition() != entity.getValue()) {
                entity.getKey().getPosition().setPosition(entity.getValue());
                Log.i("RatesManager", "updatePositions " + entity.getKey().getName() + " pos " + entity.getValue());
                DaoTaskMain.getSession().getValutePositionDao().update(entity.getKey().getPosition());
            }
        }
        return true;
    }

    private Valute obtainRubValute() {
        if (rubValute == null) {
            if (basicContent.getBasicValute().getCharCode().equals("RUB")) {
                rubValute = basicContent.getBasicValute();
            } else {
                rubValute = DaoTaskMain.getSession().getValuteDao().load(-1L);
            }
        }
        return rubValute;
    }

    public static class Params {
        private boolean force;

        public Params(boolean force) {
            this.force = force;
        }

        public boolean isForce() {
            return force;
        }
    }
}
