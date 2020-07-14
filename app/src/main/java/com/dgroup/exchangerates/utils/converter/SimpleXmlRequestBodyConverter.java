package com.dgroup.exchangerates.utils.converter;

import android.util.Log;

import org.simpleframework.xml.Serializer;

import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

/**
 * ololo Created by dimon for Snaappy .
 */

public class SimpleXmlRequestBodyConverter <T> implements Converter<T, RequestBody> {
    private MediaType MEDIA_TYPE = MediaType.parse("application/xml; charset=UTF-8");
    private String charset = "UTF-8";

    private final Serializer serializer;

    SimpleXmlRequestBodyConverter(Serializer serializer) {
        this.serializer = serializer;
    }

    SimpleXmlRequestBodyConverter(Serializer serializer, String charset) {
        this.serializer = serializer;
        this.charset = charset;
        MEDIA_TYPE = MediaType.parse("application/xml; charset="+charset);
    }

    @Override public RequestBody convert(T value) throws IOException {
        Buffer buffer = new Buffer();
        try {
            OutputStreamWriter osw = new OutputStreamWriter(buffer.outputStream(), charset);
            serializer.write(value, osw);
            osw.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }

    public void setCharset(String charset){
        this.charset = charset;
    }
}
