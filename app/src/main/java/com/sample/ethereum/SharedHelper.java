package com.sample.ethereum;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SharedHelper {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static void putKey(Context context, String Key, String Value) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.apply();

    }

    public static String getKey(Context context, String Key) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key, "");
    }

    public static void putKey(Context context, String Key, boolean Value) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(Key, Value);
        editor.apply();
    }

    public static void putKey(Context context, String Key, Integer value) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(Key, value);
        editor.apply();
    }

    public static void putListKey(Context context,  String Key, List<String> list) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(Key, json);
        editor.apply();
    }

    public static List<String> getListKey(Context context, String Key) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Key, "");
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    public static Integer getIntKey(Context context, String Key) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Key, -1);
    }

    public static boolean getBoolKey(Context context, String Key, boolean defalultValue) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Key, defalultValue);
    }

    public static String getKey(Context context, String Key, String defaultValue) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key, defaultValue);
    }

    public static void clearSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
