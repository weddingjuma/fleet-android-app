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
import android.net.NetworkInfo;
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
import com.mapotempo.lib.fragments.dialogs.ArchiveDayDialogFragment;
import com.mapotempo.lib.fragments.menu.MainMenuFragment;
import com.mapotempo.lib.fragments.routes.OnRouteSelectedListener;
import com.mapotempo.lib.fragments.routes.RoutesListFragment;

import java.util.Date;
import java.util.List;

public class MainActivity extends MapotempoBaseActivity implements
    OnRouteSelectedListener,
    MainMenuFragment.OnMenuInteractionListener
{
    String TAG = MainActivity.class.getCanonicalName();

    LiveAccessToken mLiveAccessToken;

    private DrawerLayout mDrawerLayout;

    private ConnexionReceiver mConnexionReceiver;

    private String ARCHIVED_BEHAVIOR_TAG = "ARCHIVED_BEHAVIOR";

    private boolean mArchivedMode = false;

    private LiveAccessChangeListener mLiveAccessChangeListener = new LiveAccessChangeListener<Route>()
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
                    fragment.setRoutes(routes, mArchivedMode);
                }
            });
        }
    };

    // ===================================
    // ==  Android Activity Life cycle  ==
    // ===================================

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        mArchivedMode = savedInstanceState.getBoolean(ARCHIVED_BEHAVIOR_TAG, false);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        addDrawableHandler(toolbar);
        mConnexionReceiver = new ConnexionReceiver();
        if (savedInstanceState == null)
        {
            MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getApplicationContext()).getManager();
            ArchiveDayDialogFragment alertDialog = new ArchiveDayDialogFragment();
            boolean process = alertDialog.setRoutes(mapotempoFleetManagerInterface.getRouteAccess().archived(false));
            if (process)
            {
                alertDialog.show(getSupportFragmentManager(), "ARCHIVE_DIALOG_FRAGMENT");
            }
        }

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
        setRouteListener(mArchivedMode);
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onPause()
    {
        removeRouteListener();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(mConnexionReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putBoolean(ARCHIVED_BEHAVIOR_TAG, mArchivedMode);
        super.onSaveInstanceState(outState);
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
    public void onMain()
    {
        mDrawerLayout.closeDrawers();
        setRouteListener(false);
    }

    @Override
    public void onArchived()
    {
        mDrawerLayout.closeDrawers();
        setRouteListener(true);
    }

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

    private void setRouteListener(boolean archived)
    {
        MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getApplicationContext()).getManager();
        if (mapotempoFleetManagerInterface != null)
        {
            removeRouteListener();
            mArchivedMode = archived;
            mLiveAccessToken = mapotempoFleetManagerInterface.getRouteAccess().archived_AddListener(mLiveAccessChangeListener, archived);

            // Set the activity title
            if (!archived)
                getSupportActionBar().setTitle(R.string.to_do);
            else
                getSupportActionBar().setTitle(R.string.archive);
        }
        else
        {
            Log.e(TAG, "can't found a" + MapotempoApplicationInterface.class.getCanonicalName());
        }

    }

    private void removeRouteListener()

    {
        MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getApplicationContext()).getManager();
        if (mapotempoFleetManagerInterface == null)
        {
            Log.e(TAG, "can't found a" + MapotempoApplicationInterface.class.getCanonicalName());
            return;
        }

        if (mLiveAccessToken != null)
        {
            mapotempoFleetManagerInterface.getRouteAccess().removeListener(mLiveAccessToken);
            mLiveAccessToken = null;
        }
    }


    private void addDrawableHandler(Toolbar toolbar)
    {
        mDrawerLayout = findViewById(R.id.drawer_layout);
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
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            String connexionType = "";
            if (networkInfo != null)
            {
                boolean status = true;
                switch (networkInfo.getType())
                {
                case ConnectivityManager.TYPE_WIFI:
                    connexionType = "wifi";
                    Log.d(TAG, intent.getAction() + " wifi");
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    Log.d(TAG, intent.getAction() + " mobile_data");
                    connexionType = "mobile_data";
                    status = manager.getUserPreference().getBoolPreference(UserSettings.Preference.MOBILE_DATA_USAGE);
                    break;
                default:
                    Log.d(TAG, intent.getAction() + " network down");
                    break;
                }
                UserSettings userSettings = manager.getUserPreference();
                userSettings.setConnexionInformation(new Date(), connexionType, "", BuildConfig.VERSION_NAME);
                userSettings.save();
                manager.onlineStatus(status);
            }
        }
    }
}
