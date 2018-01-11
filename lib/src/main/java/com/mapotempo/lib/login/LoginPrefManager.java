package com.mapotempo.lib.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.mapotempo.lib.R;

public class LoginPrefManager {

    private Context mContext;

    private SharedPreferences mSharedPreference;

    private static final String SHARED_BASE_NAME = "Mapotempo";

    private static final String USER_LOGIN_KEY = "UserLogin";

    private static final String USER_PASSWORD_KEY = "UserPassword";

    private static final String AUTO_LOGIN = "AutoLogin";

    private static final String URL_CONFIGURATION = "UrlConf";

    public LoginPrefManager(Activity activity) {
        mContext = activity;
        mSharedPreference = activity.getSharedPreferences(LoginPrefManager.SHARED_BASE_NAME, 0);
    }

    // ===================
    // ==  URL Builder  ==
    // ===================

    public String getUrlPref() {
        String default_url = mContext.getResources().getString(R.string.default_syncgateway_url);
        return mSharedPreference.getString(LoginPrefManager.URL_CONFIGURATION, default_url);
    }

    public void setUrlPref(String url) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(LoginPrefManager.URL_CONFIGURATION, url);
        editor.apply();
    }

    public void resetUrlPref() {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.remove(LoginPrefManager.URL_CONFIGURATION);
        editor.apply();
    }

    // ==================
    // ==  Login pref  ==
    // ==================

    public String getLoginPref() {
        return mSharedPreference.getString(LoginPrefManager.USER_LOGIN_KEY, "");
    }

    public String getPasswordPref() {
        return mSharedPreference.getString(LoginPrefManager.USER_PASSWORD_KEY, "");
    }

    public void setLoginPasswordPref(String login, String password) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(LoginPrefManager.USER_LOGIN_KEY, login);
        editor.putString(LoginPrefManager.USER_PASSWORD_KEY, password);
        editor.apply(); // Apply is async, while commit isn't.
    }

    public boolean getAutoLoginPref() {
        return mSharedPreference.getBoolean(LoginPrefManager.AUTO_LOGIN, false);
    }

    public void setAutoLoginPref(boolean status) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putBoolean(LoginPrefManager.AUTO_LOGIN, status);
        editor.apply();
    }
}
