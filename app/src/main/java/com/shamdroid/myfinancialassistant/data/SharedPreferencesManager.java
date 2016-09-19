package com.shamdroid.myfinancialassistant.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mohammad on 18/09/16.
 */

public class SharedPreferencesManager {


    public static final String IS_LOGGED_IN_KEY = "is_logged_in";
    public static final String EMAIL_KEY = "email";
    public static final String NAME_KEY = "name";

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getSharedPreferences(Context context){

        if (sharedPreferences==null){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedPreferences;
    }

    public static SharedPreferences.Editor getEditor(Context context){
        return getSharedPreferences(context).edit();
    }

    public static boolean isLoggedIn(Context context){
        return getSharedPreferences(context).getBoolean(IS_LOGGED_IN_KEY,false);
    }

    public static void setIsLoggedIn(Context context,boolean isLoggedIn){
        getEditor(context).putBoolean(IS_LOGGED_IN_KEY,isLoggedIn).apply();
    }

    public static String getEmail(Context context){
        return getSharedPreferences(context).getString(EMAIL_KEY,"null");
    }

    public static void setEmail(Context context,String email){
        getEditor(context).putString(EMAIL_KEY,email).apply();
    }

    public static String getName(Context context){
        return getSharedPreferences(context).getString(NAME_KEY,"null");
    }

    public static void setName(Context context,String name){
        getEditor(context).putString(NAME_KEY,name);
    }



}
