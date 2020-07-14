package com.dgroup.exchangerates.features.cryptocap;

import android.util.Log;

import com.dgroup.exchangerates.data.model.Provider;
import com.dgroup.exchangerates.features.cryptocap.domain.CryptoCurrencyRepo;
import com.dgroup.exchangerates.features.cryptocap.domain.CryptoMarketInteractor;

import com.dgroup.exchangerates.features.cryptocap.view.CryptoCapitalView;
import com.dgroup.exchangerates.ui.base.BasePresenter;
import com.dgroup.exchangerates.utils.DefSubscriber;
import com.dgroup.exchangerates.utils.constants.TinyDbKeys;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;

import javax.inject.Inject;

import static com.dgroup.exchangerates.utils.constants.TinyDbKeys.KEYS.CRYPTO_LAST_UPDATED;



public class CryptoCapitalPresenter extends BasePresenter<CryptoCapitalView, Void> {

    private CryptoMarketInteractor mCryptoMarketInteractor;

    private int pagination;

    private int changePricePosition;

    @Inject
    public CryptoCapitalPresenter(CryptoMarketInteractor cryptoMarketInteractor) {
        mCryptoMarketInteractor = cryptoMarketInteractor;
        pagination = 0;
        changePricePosition = TinyDbWrap.getInstance().getInt(TinyDbKeys.KEYS.INTERVAL_FOR_CHANGE_PRICE, 1);
    }

    public void subscribe(CryptoCapitalView cryptoCapitalView) {
        attachView(cryptoCapitalView);
        loadActualRates();
    }

    public void loadActualRates() {
        getMvpView().showLoading(true);
        mCryptoMarketInteractor.execute(new CryptoMarketInteractor.Params(Provider.DB, pagination),
                new DefSubscriber<CryptoCurrencyRepo.Result>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        getMvpView().showLoading(false);
                        getMvpView().showError(e);
                    }

                    @Override
                    public void onNext(CryptoCurrencyRepo.Result result) {
                        Log.i("CryptoCapitalPresenter", "result "+result.getProvider()+" data "+result.getCryptoMarketCurrencyInfos().size());
                        if (result.getCryptoMarketCurrencyInfos() != null) {
                            getMvpView().showContent(result.getCryptoMarketCurrencyInfos(), result.getLastUpdate());
                            if(result.isActual()){
                                getMvpView().showLoading(false);
                            }
                        }
                    }
                });
    }

    public void unsubscribe() {
        mCryptoMarketInteractor.unSubscribe();
        detachView();
    }

    public int getChangePricePosition() {
        return changePricePosition;
    }

    public void setChangePricePosition(int pos) {
        changePricePosition = pos;
        TinyDbWrap.getInstance().putInt(TinyDbKeys.KEYS.INTERVAL_FOR_CHANGE_PRICE, pos);
    }
}
