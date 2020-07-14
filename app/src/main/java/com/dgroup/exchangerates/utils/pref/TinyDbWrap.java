package com.dgroup.exchangerates.utils.pref;

import android.content.Context;

import com.dgroup.exchangerates.utils.constants.TinyDbKeys;

import java.util.ArrayList;
import java.util.Map;

/**
 * See:
 * http://stackoverflow.com/questions/5734721/android-shared-preferences
 * https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo/
 * <p/>
 * Usage:
 * <p/>
 * 1)
 * import com.snaappy.pref.TinyDbWrap;
 * ...
 * TinyDbWrap tinydb = TinyDbWrap.getInstance();
 * <p/>
 * tinydb.putInt("clickCount", 2);
 * tinydb.putFloat("xPoint", 3.6f);
 * tinydb.putLong("userCount", 39832L);
 * tinydb.putString("userName", "john");
 * tinydb.putBoolean("isUserMale", true);
 * tinydb.putList("MyUsers", mUsersArray);
 * <p/>
 * 2)
 * import com.snaappy.pref.TinyDbWrap;
 * ...
 * TinyDbWrap.getInstance().putInt("clickCount", 2);
 * TinyDbWrap.getInstance().putFloat("xPoint", 3.6f);
 * <p/>
 * and etc.
 */

public class TinyDbWrap {

    private TinyDB mTinyDB;

    private TinyDbWrap() {
    }

    public void init(Context appContext) {
        if (mTinyDB == null) {
            mTinyDB = new TinyDB(appContext);
        }
    }

    private static class InstanceHolder {
        private static final TinyDbWrap INSTANCE = new TinyDbWrap();
    }

    public static TinyDbWrap getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public int getInt(String key, int defaultValue) {
        return mTinyDB.getInt(key, defaultValue);
    }

    public ArrayList<Integer> getListInt(String key) {
        return mTinyDB.getListInt(key);
    }

    public long getLong(String key, long defaultValue) {
        return mTinyDB.getLong(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return mTinyDB.getFloat(key, defaultValue);
    }

    public double getDouble(String key, double defaultValue) {
        return mTinyDB.getDouble(key, defaultValue);
    }

    public ArrayList<Double> getListDouble(String key) {
        return mTinyDB.getListDouble(key);
    }

    public String getString(String key) {
        return mTinyDB.getString(key);
    }

    public String getString(String key, String defaultValue) {
        return mTinyDB.getString(key, defaultValue);
    }

    public ArrayList<String> getListString(String key) {
        return mTinyDB.getListString(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mTinyDB.getBoolean(key, defaultValue);
    }

    public ArrayList<Boolean> getListBoolean(String key) {
        return mTinyDB.getListBoolean(key);
    }

    public void putInt(String key, int value) {
        mTinyDB.putInt(key, value);
    }

    public void putListInt(String key, ArrayList<Integer> intList) {
        mTinyDB.putListInt(key, intList);
    }

    public void putLong(String key, long value) {
        mTinyDB.putLong(key, value);
    }

    public void putFloat(String key, float value) {
        mTinyDB.putFloat(key, value);
    }

    public void putDouble(String key, double value) {
        mTinyDB.putDouble(key, value);
    }

    public void putListDouble(String key, ArrayList<Double> doubleList) {
        mTinyDB.putListDouble(key, doubleList);
    }

    public void putString(String key, String value) {
        mTinyDB.putString(key, value);
    }

    public void putListString(String key, ArrayList<String> stringList) {
        mTinyDB.putListString(key, stringList);
    }

    public void putBoolean(String key, boolean value) {
        mTinyDB.putBoolean(key, value);
    }

    public boolean putBooleanCommit(String key, boolean value) {
        return mTinyDB.putBooleanCommit(key, value);
    }

    public void putListBoolean(String key, ArrayList<Boolean> boolList) {
        mTinyDB.putListBoolean(key, boolList);
    }

    public void remove(String key) {
        mTinyDB.remove(key);
    }

    public void clear() {
        mTinyDB.clear();
    }

    public boolean clearCommit() {
        return mTinyDB.clearCommit();
    }

    public Map<String, ?> getAll() {
        return mTinyDB.getAll();
    }

    public float getBasicCount(){
        return getFloat(TinyDbKeys.KEYS.COUNT_VALUTE, 1000f);
    }

    public void setBasicCount(float count){
        putFloat(TinyDbKeys.KEYS.COUNT_VALUTE, count);
    }

}
