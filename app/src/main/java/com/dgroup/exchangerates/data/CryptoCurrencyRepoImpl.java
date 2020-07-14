package com.dgroup.exchangerates.data;

import android.util.Log;

import com.dgroup.exchangerates.api.Api;
import com.dgroup.exchangerates.data.db_wrapper.DaoTaskMain;
import com.dgroup.exchangerates.data.model.Provider;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.dgroup.exchangerates.features.cryptocap.domain.CryptoCurrencyRepo;
import com.dgroup.exchangerates.utils.TimeFormatter;
import com.dgroup.exchangerates.utils.pref.TinyDbWrap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

import static com.dgroup.exchangerates.utils.constants.TinyDbKeys.KEYS.CRYPTO_LAST_UPDATED;



@Singleton
public class CryptoCurrencyRepoImpl implements CryptoCurrencyRepo {

    public static final String COINMARKETCAP_URL = "https://coinmarketcap.com/";
    public static final String TAG = "CryptoMarketInteractor";
    public static final long ACTUAL_TIME = TimeFormatter.MINUTE;
    private Observable<Api> api;

    private List<CryptoMarketCurrencyInfo> cache;

    @Inject
    public CryptoCurrencyRepoImpl(Observable<Api> api) {
        Log.i(TAG, "constructor");
        this.api = api.cache();
    }

    @Override
    public Observable<Result> loadAllFromMemory() {
        Log.i(TAG, "loadAllFromMemory cache exist " + (cache != null));
        if (cache != null) {
            return Observable.fromCallable(() -> new Result(Provider.MEMORY, isCryptoDataActual(), cache));
        } else {
            return Observable.empty();
        }
    }

    @Override
    public Observable<Result> loadAllFromDB() {
        List<CryptoMarketCurrencyInfo> data = DaoTaskMain.getSession().getCryptoMarketCurrencyInfoDao().loadAll();
        if (data.isEmpty()) {
            return Observable.empty();
        } else {
            return Observable.fromCallable(() -> new Result(Provider.DB, isCryptoDataActual(), data));
        }
    }

    public static boolean isCryptoDataActual() {
        long lastUPD = TinyDbWrap.getInstance().getLong(CRYPTO_LAST_UPDATED, 0);
        return System.currentTimeMillis() - lastUPD < ACTUAL_TIME;
    }

    @Override
    public Observable<Result> loadFromWeb(int page) {
        Log.i(TAG, "loadFromWeb page " + page);
        return api.map(api -> {
            try {
                return api.getCryptoCurrency(page);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).map(cryptoMarketCurrencyInfos -> {
            try {
                int currentPage = page / 100 + 1;
                Document doc = Jsoup.connect(COINMARKETCAP_URL + currentPage).get();
                Elements headers = doc.select("tbody");
                headers = headers.get(0).getElementsByTag("tr");
                int i = 0;
                for (Element element : headers) {
                    Elements elements = element.getElementsByTag("tr").get(0).getElementsByTag("td");
                    String graph = elements.get(7).getElementsByAttribute("src").attr("src");
                    if(!graph.endsWith(".png")){
                        String id = elements.get(8).getElementsByTag("td").get(0).attr("data-cc-id");
                        graph = "https://s2.coinmarketcap.com/generated/sparklines/web/7d/usd/"+id+".png";
                    }
                    cryptoMarketCurrencyInfos.get(i).setGraph(graph);
                    cryptoMarketCurrencyInfos.get(i).setRedrawGraph(true);
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Result(Provider.WEB, true, cryptoMarketCurrencyInfos);
        }).doOnNext(result -> {
            Log.i(TAG, "load web doOnNext " + Thread.currentThread().getName());
            cache = result.getCryptoMarketCurrencyInfos();
            DaoTaskMain.getSession().getCryptoMarketCurrencyInfoDao().insertOrReplaceInTx(result.getCryptoMarketCurrencyInfos());
            TinyDbWrap.getInstance().putLong(CRYPTO_LAST_UPDATED, System.currentTimeMillis());
        });
    }
}
