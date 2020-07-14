package com.dgroup.exchangerates.features.cryptocap.domain;

import android.util.Log;

import com.dgroup.exchangerates.data.Interactor;
import com.dgroup.exchangerates.data.model.Provider;
import com.dgroup.exchangerates.utils.ActivityScoped;
import com.dgroup.exchangerates.utils.SortUtils;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

import static com.dgroup.exchangerates.features.cryptocap.domain.CryptoCurrencyRepo.Result;
import static com.dgroup.exchangerates.utils.SortUtils.CryptoSortVariant;
import static com.dgroup.exchangerates.utils.constants.TinyDbKeys.KEYS.CRYPTO_LAST_UPDATED;


@ActivityScoped
public class CryptoMarketInteractor extends Interactor<Result, CryptoMarketInteractor.Params> {

    private CryptoCurrencyRepo mCryptoCurrencyRepo;

    @Inject
    public CryptoMarketInteractor(Executor multiExecutor, CryptoCurrencyRepo cryptoCurrencyRepo) {
        super(multiExecutor);
        Log.i("CryptoMarketInteractor", "constructor");
        this.mCryptoCurrencyRepo = cryptoCurrencyRepo;
    }

    @Override
    protected Observable<Result> buildObservable(Params params) {
        return mCryptoCurrencyRepo.loadAllFromMemory()
                .switchIfEmpty(mCryptoCurrencyRepo.loadAllFromDB()
                        .switchIfEmpty(mCryptoCurrencyRepo.loadFromWeb(params.pagination)))
                .flatMap(result -> {
                    if (result.isActual()) {
                        return Observable.just(result);
                    }
                    return mCryptoCurrencyRepo.loadFromWeb(params.pagination).startWith(mCryptoCurrencyRepo.loadAllFromDB());
                }).map(result -> {
                    Collections.sort(result.getCryptoMarketCurrencyInfos(),
                            SortUtils.getCryptoComparator(params.getCryptoSortVariant()));
                    result.setLastUpdate(TinyDbWrap.getInstance().getLong(CRYPTO_LAST_UPDATED, 0));
                    return result;
                });
    }

    public static class Params {
        private Provider mProvider;
        private int pagination;
        private CryptoSortVariant mCryptoSortVariant;

        public Params(Provider provider, int pagination) {
            this.mProvider = provider;
            this.pagination = pagination;
            mCryptoSortVariant = CryptoSortVariant.CAPITAL;
        }

        public Params(Provider provider, CryptoSortVariant cryptoSortVariant, int pagination) {
            this.mProvider = provider;
            this.mCryptoSortVariant = cryptoSortVariant;
            this.pagination = pagination;
        }

        public Provider getProvider() {
            return mProvider;
        }

        public int getPagination() {
            return pagination;
        }

        public CryptoSortVariant getCryptoSortVariant() {
            return mCryptoSortVariant;
        }
    }
}
