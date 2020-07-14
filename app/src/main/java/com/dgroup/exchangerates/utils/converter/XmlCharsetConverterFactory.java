package com.dgroup.exchangerates.utils.converter;

import android.util.Log;

import com.google.gson.JsonObject;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


public class XmlCharsetConverterFactory extends Converter.Factory {

    private String charset;

    /**
     * Create an instance using a default {@link Persister} instance for conversion.
     */
    public static XmlCharsetConverterFactory create(String charset) {
        return create(new Persister(), charset);
    }

    /**
     * Create an instance using {@code serializer} for conversion.
     */
    public static XmlCharsetConverterFactory create(Serializer serializer) {
        return new XmlCharsetConverterFactory(serializer, true);
    }

    /**
     * Create an instance using {@code serializer} for conversion and charset.
     */
    public static XmlCharsetConverterFactory create(Serializer serializer, String charset) {
        return new XmlCharsetConverterFactory(serializer, true, charset);
    }

    /**
     * Create an instance using {@code serializer} for non-strict conversion.
     */
    public static XmlCharsetConverterFactory createNonStrict(Serializer serializer) {
        return new XmlCharsetConverterFactory(serializer, false);
    }

    private final Serializer serializer;
    private final boolean strict;

    private XmlCharsetConverterFactory(Serializer serializer, boolean strict) {
        if (serializer == null) throw new NullPointerException("serializer == null");
        this.serializer = serializer;
        this.strict = strict;
    }

    private XmlCharsetConverterFactory(Serializer serializer, boolean strict, String charset) {
        if (serializer == null) throw new NullPointerException("serializer == null");
        this.serializer = serializer;
        this.strict = strict;
        this.charset = charset;
    }

    public boolean isStrict() {
        return strict;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (type.equals(JsonObject.class)){
            return null;
        }
        Class<?> cls = (Class<?>) type;
        return new SimpleXmlResponseBodyConverter<>(cls, serializer, strict, charset);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (!(type instanceof Class)) {
            return null;
        }
        return new SimpleXmlRequestBodyConverter<>(serializer, charset);
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}