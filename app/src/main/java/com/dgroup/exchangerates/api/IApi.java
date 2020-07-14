package com.dgroup.exchangerates.api;


import com.dgroup.exchangerates.data.model.BanksCourses;
import com.dgroup.exchangerates.data.model.ValCurses;
import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * API interface
 */
public interface IApi {

    /**
     * Get all valutes rates from CBRF by date
     */
    @Headers(("Content-type: text/plain; charset=utf-8"))
    @GET(com.dgroup.exchangerates.api.UrlConstants.CBRF_ALL_RATES_BY_DATE)
    @XmlOrJsonFactory.Xml
    Call<ValCurses> getRatesFromRCB(
            @Query(value = "date_req") String date
    );

    /**
     * Get crypto
     */
    @Headers(("Content-type: text/plain; charset=utf-8"))
    @GET(UrlConstants.CRYPTO_CURRENCY_OLD)
    @XmlOrJsonFactory.Json
    Call<ValCurses> getCryptoCurreny(
            @Query(value = "date_req") String date
    );

    /**
     * Get all bank courses by city
     */
    @Headers("Content-Type: application/xml")
    @GET
    @XmlOrJsonFactory.Xml
    Call<BanksCourses> getBankCourses(
            @Url String url,
            @Query(value = "kod") int cityCode
    );

    /**
     * Get current location
     */
    @Headers("Content-Type: application/json")
    @GET
    @XmlOrJsonFactory.Json
    Call<JsonObject> getLocation(
            @Url String url
    );

    /**
     * Get crypto currency
     */
    @Headers("Content-Type: application/json")
    @GET
    @XmlOrJsonFactory.Json
    Call<List<CryptoMarketCurrencyInfo>> getCryptoCurrency(
            @Url String url,
            @Query(value = "start") int start
    );
}
