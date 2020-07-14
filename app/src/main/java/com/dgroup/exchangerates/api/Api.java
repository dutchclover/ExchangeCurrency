package com.dgroup.exchangerates.api;

import android.content.Context;
import android.util.Log;

import com.dgroup.exchangerates.data.model.BanksCourses;
import com.dgroup.exchangerates.data.model.ValCurses;
import com.dgroup.exchangerates.data.model.db.BankCourse;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.dgroup.exchangerates.utils.Utils;
import com.dgroup.exchangerates.utils.converter.XmlCharsetConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.dgroup.exchangerates.api.UrlConstants.BANKS_URL;
import static com.dgroup.exchangerates.api.UrlConstants.CRYPTO_CURRENCY;
import static com.dgroup.exchangerates.api.UrlConstants.LOCATION_URL;
import static com.dgroup.exchangerates.utils.ValUtils.copyCourses;


/**
 * API
 */
@Singleton
public class Api {

    private static final String TAG = Api.class.getSimpleName();

    private Context mContext;
    private IApi mApiInterface;

    private XmlOrJsonFactory mXmlOrJsonFactory;

    @Inject
    public Api(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
//        long start = System.currentTimeMillis();
//        Log.i(TAG,"init Thread = "+Thread.currentThread().getName());
        mXmlOrJsonFactory = new XmlOrJsonFactory();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(mXmlOrJsonFactory)
                .baseUrl(("http://www.cbr.ru"))
                .client(getClient())
                .build();
        mApiInterface = retrofit.create(IApi.class);
//        SystemClock.sleep(5000);
//        Log.i(TAG,"init complete time: "+(System.currentTimeMillis() - start));
    }

    private OkHttpClient getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("OkHttp", message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.connectTimeout(15, TimeUnit.SECONDS);
        b.readTimeout(15, TimeUnit.SECONDS);
        b.writeTimeout(15, TimeUnit.SECONDS);
        return b.addInterceptor(logging).build();
    }

    public void checkNetwork() throws NetworkFailException {
        if (!Utils.isNetworkAvailable(mContext)) {
            throw new NetworkFailException("No network");
        }
    }


    public ValCurses getRatesFromRCB(String date) throws Exception {
        mXmlOrJsonFactory.setCharset("windows-1251");
        Call<ValCurses> cursesCall = mApiInterface.getRatesFromRCB(date);
        ValCurses valCurses = cursesCall.execute().body();
        return valCurses;
    }

    public List<BankCourse> getBankCourses(final int cityCode, final boolean actual) throws Exception {
        try {
            checkNetwork();
            mXmlOrJsonFactory.setCharset("UTF-8");
            Response<BanksCourses> response = mApiInterface.getBankCourses(BANKS_URL, cityCode).execute();
            if (response.isSuccessful()) {
                BanksCourses banksCourses = response.body();
                Log.i("BanksRepository"," getBankCourses getEurBuy "+banksCourses.getActualCourses().get(0).getEurBuy());
                List<BankCourse> dbModelCourses = new ArrayList<>();
                copyCourses(banksCourses, dbModelCourses, cityCode, actual);
                return dbModelCourses;
            }else{
                throw new RuntimeException(response.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String getCurrentCity() throws Exception {
        Call<JsonObject> cursesCall = mApiInterface.getLocation(LOCATION_URL);
        JsonObject resp = cursesCall.execute().body();
        return resp.getAsJsonObject("city").get("name_ru").getAsString();
    }

    public List<CryptoMarketCurrencyInfo> getCryptoCurrency(int start) throws Exception {
        Call<List<CryptoMarketCurrencyInfo>> cursesCall = mApiInterface.getCryptoCurrency(CRYPTO_CURRENCY, start);
        return cursesCall.execute().body();
    }

}