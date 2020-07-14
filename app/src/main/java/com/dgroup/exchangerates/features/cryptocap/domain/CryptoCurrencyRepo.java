package com.dgroup.exchangerates.features.cryptocap.domain;

import com.dgroup.exchangerates.data.model.Provider;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;

import java.util.List;

import rx.Observable;



public interface CryptoCurrencyRepo {

    class Result {
        private Provider mProvider;
        private boolean actual;
        private List<CryptoMarketCurrencyInfo> mCryptoMarketCurrencyInfos;
        private long lastUpdate;

        public Result(Provider provider, boolean actual, List<CryptoMarketCurrencyInfo> cryptoMarketCurrencyInfos) {
            mProvider = provider;
            this.actual = actual;
            mCryptoMarketCurrencyInfos = cryptoMarketCurrencyInfos;
        }

        public Provider getProvider() {
            return mProvider;
        }

        public boolean isActual() {
            return actual;
        }

        public List<CryptoMarketCurrencyInfo> getCryptoMarketCurrencyInfos() {
            return mCryptoMarketCurrencyInfos;
        }

        public long getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
    }

    Observable<Result> loadAllFromMemory();

    Observable<Result> loadAllFromDB();

    Observable<Result> loadFromWeb(int page);
}
