package com.dgroup.exchangerates.utils.converter;

import android.support.annotation.NonNull;
import android.util.Log;

import org.simpleframework.xml.Serializer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import okhttp3.ResponseBody;
import retrofit2.Converter;


public class SimpleXmlResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Class<T> cls;
    private final Serializer serializer;
    private final boolean strict;
    private String charset;

    SimpleXmlResponseBodyConverter(Class<T> cls, Serializer serializer, boolean strict) {
        this.cls = cls;
        this.serializer = serializer;
        this.strict = strict;
    }

    SimpleXmlResponseBodyConverter(Class<T> cls, Serializer serializer, boolean strict, String charset) {
        this.cls = cls;
        this.serializer = serializer;
        this.strict = strict;
        this.charset = charset;
    }

    @Override
    public T convert(@NonNull ResponseBody value) throws IOException {
        try {
            StringReader stringReader = new StringReader(value.string());
            T read = serializer.read(cls, stringReader, strict);
            if (read == null) {
                throw new IllegalStateException("Could not deserialize body as " + cls);
            }
            return read;
        } catch (RuntimeException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            value.close();
        }
    }
}