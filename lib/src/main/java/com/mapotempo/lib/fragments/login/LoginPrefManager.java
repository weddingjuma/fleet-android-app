/*
 * Copyright © Mapotempo, 2018
 *
 * This file is part of Mapotempo.
 *
 * Mapotempo is free software. You can redistribute it and/or
 * modify since you respect the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Mapotempo is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the Licenses for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Mapotempo. If not, see:
 * <http://www.gnu.org/licenses/agpl.html>
 */

package com.mapotempo.lib.fragments.login;

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

    public LoginPrefManager(Context context) {
        mContext = context;
        mSharedPreference = context.getSharedPreferences(LoginPrefManager.SHARED_BASE_NAME, 0);
    }

    // ===================
    // ==  URL Builder  ==
    // ===================

    public String getFullURL() {
        return String.format("%s:%s/%s", getUrlPref(), getPortPref(), getDBPref());
    }


    public String getUrlPref() {
        String default_url = mContext.getResources().getString(R.string.default_fleet_url_default);
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

    public String getPortPref() {
        return mContext.getResources().getString(R.string.default_fleet_port_default);
    }

    public String getDBPref() {
        return mContext.getResources().getString(R.string.default_fleet_db_default);
    }

    // ============================
    // ==  Login pref ACCESSORS  ==
    // ============================

    // GETTERS:

    public String getLoginPref() {
        return mSharedPreference.getString(LoginPrefManager.USER_LOGIN_KEY, "");
    }

    public String getPasswordPref() {
        return mSharedPreference.getString(LoginPrefManager.USER_PASSWORD_KEY, "");
    }

    public boolean getAutoLoginPref() {
        return mSharedPreference.getBoolean(LoginPrefManager.AUTO_LOGIN, false);
    }

    // SETTERS:

    public void setAutoLoginPref(boolean status) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putBoolean(LoginPrefManager.AUTO_LOGIN, status);
        editor.apply();
    }

    public void setLoginPasswordPref(String login, String password) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(LoginPrefManager.USER_LOGIN_KEY, login);
        editor.putString(LoginPrefManager.USER_PASSWORD_KEY, password);
        editor.apply(); // Apply is async, while commit isn't.
    }
}
