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

package com.mapotempo.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.mapotempo.app.utils.ConnectionManager;
import com.mapotempo.fleet.api.ConnectionVerifyStatus;
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.fragments.login.LoginFragment;
import com.mapotempo.lib.fragments.settings.SettingsHelper;
import com.mapotempo.lib.utils.AlertMessageHelper;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentImplementation, ConnectionManager.OnConnectionSetup {

    // ===================================
    // ==  Android Activity Life cycle  ==
    // ===================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setTheme(R.style.AppTheme_Night);
    }

    // =====================================================
    // ==  ConnectionManager.OnConnectionSetup Interface  ==
    // =====================================================

    @Override
    public void onSelectedDataProviderWifi(ConnectionManager.ConnectionType connectionType, Context context) {
        intentNext();
    }

    @Override
    public void onSelectedDataProviderBoth(ConnectionManager.ConnectionType connectionType, Context context) {
        intentNext();
    }

    // =============================================================
    // ==  LoginFragment.OnLoginFragmentImplementation Interface  ==
    // =============================================================

    /**
     * An interface which is trigger when the connection has been processed.
     *
     * @param status  The current status of connection
     * @param manager The connexion associate manager
     */
    @Override
    public void onLogin(ConnectionVerifyStatus status, MapotempoFleetManagerInterface manager) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.hook_login_fragment);
        switch (status) {
            case VERIFY:
                MapotempoApplication mapotempoApplication = (MapotempoApplication) getApplicationContext();
                mapotempoApplication.setManager(manager);
                mapotempoApplication.setOfflineMapManager();
                intentNext();
                break;
            case LOGIN_ERROR:
                loginFragment.toogleLogginView(false);
                AlertMessageHelper.errorAlert(this, null, getString(R.string.login_error_title), getString(R.string.login_error_short_text), getString(R.string.login_error_invalid));
                break;
            default:
                loginFragment.toogleLogginView(false);
                AlertMessageHelper.errorAlert(this, null, getString(R.string.login_error_title), getString(R.string.login_error_short_text), getString(R.string.login_error_server) + "\ncode : " + status.getCode());
                break;
        }
    }

    private void intentNext() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

