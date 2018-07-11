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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mapotempo.app.base.MapotempoBaseActivity;
import com.mapotempo.fleet.core.accessor.LiveAccessChangeListener;
import com.mapotempo.fleet.core.accessor.LiveAccessToken;
import com.mapotempo.fleet.dao.model.Route;
import com.mapotempo.fleet.dao.model.UserSettings;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.fragments.menu.MainMenuFragment;
import com.mapotempo.lib.fragments.routes.OnRouteSelectedListener;
import com.mapotempo.lib.fragments.routes.RoutesListFragment;

import java.util.List;

public class MainActivity extends MapotempoBaseActivity implements
    OnRouteSelectedListener,
    MainMenuFragment.OnMenuInteractionListener
{
    String TAG = MainActivity.class.getCanonicalName();

    LiveAccessToken mLiveAccessToken;

    private DrawerLayout mDrawerLayout;

    private ConnexionReceiver mConnexionReceiver;

    // ===================================
    // ==  Android Activity Life cycle  ==
    // ===================================

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        addDrawableHandler(toolbar);
        mConnexionReceiver = new ConnexionReceiver();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Wifi / Data manager connexion status
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mConnexionReceiver, intentFilter);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getApplicationContext()).getManager();
        if (mapotempoFleetManagerInterface != null)
        {
            mLiveAccessToken = mapotempoFleetManagerInterface.getRouteAccess().notArchived_AddListener(new LiveAccessChangeListener<Route>()
            {
                @Override
                public void changed(final List<Route> routes)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            RoutesListFragment fragment = (RoutesListFragment) getSupportFragmentManager().findFragmentById(R.id.routeList);
                            fragment.setRoutes(routes);
                        }
                    });
                }
            });
        }

        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getApplicationContext()).getManager();
        if (mapotempoFleetManagerInterface != null)
            mapotempoFleetManagerInterface.getRouteAccess().removeListener(mLiveAccessToken);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(mConnexionReceiver);
    }

    // =========================================
    // ==  OnRouteSelectedListener Interface  ==
    // =========================================
    @Override
    public void onRouteSelected(Route route)
    {
        Intent intent = new Intent(this, MissionsActivity.class);
        intent.putExtra("route_id", route.getId());
        startActivity(intent);
    }

    // ===========================================
    // ==  OnMenuInteractionListener Interface  ==
    // ===========================================

    @Override
    public void onMap()
    {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSettings()
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onTracking(boolean tracking_status)
    {
        return tracking_status;
    }

    @Override
    public void onLogout()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        MapotempoApplication mapotempoApplication = (MapotempoApplication) getApplicationContext();
        mapotempoApplication.getManager().getRouteAccess().removeListener(mLiveAccessToken);
        mapotempoApplication.setManager(null);
        finish();
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void addDrawableHandler(Toolbar toolbar)
    {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null)
        {
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name)
            {

                public void onDrawerClosed(View view)
                {
                    supportInvalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView)
                {
                    supportInvalidateOptionsMenu();
                }
            };

            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
    }

    private class ConnexionReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            MapotempoFleetManager manager = ((MapotempoApplication) getApplicationContext()).getManager();

            final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

            final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi != null && wifi.isConnected())
            {
                Log.d(">>>>>>>>>>>>> " + intent.getAction(), "wifi");
                manager.onlineStatus(true);
            }
            else if (mobile != null && mobile.isConnected())
            {
                Log.d(">>>>>>>>>>>>> " + intent.getAction(), "data");
                boolean status = manager.getUserPreference().getBoolPreference(UserSettings.Preference.MOBILE_DATA_USAGE);
                manager.onlineStatus(status);
            }
            else
            {
                Log.d(">>>>>>>>>>>>> " + intent.getAction(), "network down");
                ((MapotempoApplication) getApplicationContext()).getManager().onlineStatus(false);
            }
        }
    }
}
