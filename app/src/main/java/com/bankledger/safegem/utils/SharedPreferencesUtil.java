package com.bankledger.safegem.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

	private SharedPreferences preferences;

    private static SharedPreferencesUtil spUtils;

    public SharedPreferencesUtil(Context context, String fileName) {
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }
    public SharedPreferences getSharedPreferences(){
        return preferences;
    }


    public synchronized static SharedPreferencesUtil getDefaultPreferences(Context context){
        if(spUtils == null){
            spUtils = new SharedPreferencesUtil(context, "default");
        }
        return spUtils;
    }

    /**
     * *************** get ******************
     */

    public String get(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public boolean get(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public float get(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public long get(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    /**
     * *************** put ******************
     */
    public void put(String key, String value) {
        if (value == null) {
            preferences.edit().remove(key).commit();
        } else {
            preferences.edit().putString(key, value).commit();
        }
    }

    public void put(String key, boolean value) {
        preferences.edit().putBoolean(key, value).commit();
    }

    public void put(String key, float value) {
        preferences.edit().putFloat(key, value).commit();
    }

    public void put(String key, long value) {
        preferences.edit().putLong(key, value).commit();
    }

    public void putInt(String key, int value) {
        preferences.edit().putInt(key, value).commit();
    }
    
}