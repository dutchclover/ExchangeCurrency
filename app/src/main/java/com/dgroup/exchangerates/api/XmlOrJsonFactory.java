package com.dgroup.exchangerates.api;




import android.util.Log;
import android.util.Xml;

import com.dgroup.exchangerates.data.model.db.CryptoMarketCurrencyInfo;
import com.dgroup.exchangerates.utils.converter.XmlCharsetConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.annotation.RetentionPolicy.RUNTIME;



public class XmlOrJsonFactory extends Converter.Factory {

    @Retention(RUNTIME)
    public @interface Json {}

    @Retention(RUNTIME)
    public @interface Xml {}

    private final XmlCharsetConverterFactory xmlFactory = XmlCharsetConverterFactory.create("windows-1251");

    private final Converter.Factory jsonFactory = GsonConverterFactory.create(new GsonBuilder()
            .registerTypeAdapter(CryptoMarketCurrencyInfo.class, new CryptoCurrencyDeserializer()).create());
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for(Annotation annotation : annotations){
            if(annotation instanceof Xml){
                return xmlFactory.responseBodyConverter(type, annotations, retrofit);
            }
            if(annotation instanceof Json){
                return jsonFactory.responseBodyConverter(type, annotations, retrofit);
            }
        }
        return null;
    }

    public void setCharset(String charset) {
        xmlFactory.setCharset(charset);
    }
}
