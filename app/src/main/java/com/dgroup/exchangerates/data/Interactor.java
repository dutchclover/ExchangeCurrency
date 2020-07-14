package com.dgroup.exchangerates.data;

import java.util.concurrent.Executor;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public abstract class Interactor<ResultType, ParameterType> {

    private final CompositeSubscription subscription = new CompositeSubscription();

    private Executor mExecutor;

    public Interactor(Executor executor) {
        this.mExecutor = executor;
    }

    protected abstract Observable<ResultType> buildObservable(ParameterType parameter);

    public void execute(ParameterType parameter, Subscriber<ResultType> subscriber) {
        subscription.add(buildObservable(parameter)
                .subscribeOn(Schedulers.from(mExecutor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    public void execute(Subscriber<ResultType> subscriber) {
        execute(null, subscriber);
    }

    void customExecute(Observable<Boolean> observable, Action1<Boolean> onNext) {
        subscription.add(observable
                .subscribeOn(Schedulers.from(mExecutor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, Throwable::printStackTrace));
    }

    public void unSubscribe() {
        subscription.clear();
    }

}