package com.dgroup.exchangerates.features.cb_rates;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.dgroup.exchangerates.data.RatesManager;
import com.dgroup.exchangerates.data.model.db.Valute;
import com.dgroup.exchangerates.features.cb_rates.listeners.ItemTouchHelperAdapter;
import com.dgroup.exchangerates.ui.base.BasePresenter;
import com.dgroup.exchangerates.ui.view.holders.ValuteViewHolder;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;

public class CBRatesPresenter extends BasePresenter<CBRatesMvpView, CBRouter> implements ItemTouchHelperAdapter {

    public static String TAG = CBRatesPresenter.class.getSimpleName();

    private RatesManager mRatesManager;

    private HashMap<Valute, Integer> updatedPositions;

    @Inject
    public CBRatesPresenter(RatesManager ratesManager) {
        this.mRatesManager = ratesManager;
        updatedPositions = new HashMap<>();
    }

    public void subscribe(CBRatesMvpView CBRatesMvpView, CBRouter cbRouter) {
        setRouter(cbRouter);
        attachView(CBRatesMvpView);
        getMvpView().setBasicContent(mRatesManager.getBasicContent(), false);
        loadRates();
    }

    public void unsubscribe() {
        setRouter(null);
        mRatesManager.unSubscribe();
        detachView();
    }

    public void updateCBRates() {
        loadRates(new RatesManager.Params(true));
    }

    private void loadRates() {
        getMvpView().showLoading(true);
        loadRates(new RatesManager.Params(false));
    }

    private void loadRates(final RatesManager.Params params) {
        mRatesManager.execute(params, new Subscriber<List<Valute>>() {
            @Override
            public void onCompleted() {
                Log.i("CBRatesPresenter", "loadRates onCompleted " + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.i("CBRatesPresenter", "loadRates onError " + Thread.currentThread().getName());
                e.printStackTrace();
                CBRatesMvpView CBRatesMvpView = getMvpView();
                if (CBRatesMvpView != null) {
                    CBRatesMvpView.showError();
                }
            }

            @Override
            public void onNext(List<Valute> valutes) {
                Log.i("CBRatesPresenter", "loadRates onNext size " + valutes.size());
                CBRatesMvpView mvpView = getMvpView();
                if (mvpView != null) {
                    mvpView.showLoading(false);
                    mvpView.showList(valutes, params.isForce());
                }
            }
        });
    }

    public void recalculationCount(String charCode, float count, float value) {
        mRatesManager.getBasicContent().setCharCodeBasicCount(charCode);
        mRatesManager.getBasicContent().setCount(count);
        getMvpView().setBasicContent(mRatesManager.getBasicContent(), false);
    }

    @Override
    public boolean onItemMove(Valute fromPosition, Valute toPosition, int fromPos, int toPos) {
        Log.i(TAG, "onItemMove from " + fromPos + " to " + toPos);
        updatedPositions.put(fromPosition, toPos);
        updatedPositions.put(toPosition, fromPos);
        getMvpView().notifyMovedItems(fromPos, toPos);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        //todo swipe to dismiss callback
//        mValutes.remove(position);
//        notifyItemRemoved(position);
    }

    @Override
    public void onItemsTransactionEnd(RecyclerView.ViewHolder firstHolder, RecyclerView.ViewHolder holder) {
        if(firstHolder!=null) {
            Valute firstValute = ((ValuteViewHolder) firstHolder).getCurrentValute();
            Log.i(TAG, "onItemsTransactionEnd firstValute " + firstValute.getName());
            if (!mRatesManager.getBasicContent().getBasicValute().equals(firstValute)) {
                if (mRatesManager.changeBasicContent(firstValute)) {
                    getMvpView().setBasicContent(mRatesManager.getBasicContent(), true);
                }
            }
        }
        mRatesManager.swapPositions(updatedPositions, aBoolean -> updatedPositions.clear());
    }

    public void showCalculator(Valute valute) {
        getRouter().showKeyboard(valute);
    }
}
