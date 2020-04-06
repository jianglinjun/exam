package com.jlj.exam.http;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheUtils {
    private final static String SP_NAME = "http_cache";

    public static void save(Context context, String key, String value){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String get(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }
}
