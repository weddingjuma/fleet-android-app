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
import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.fleet.dao.model.UserSettings;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.lib.fragments.menu.MainMenuFragment;
import com.mapotempo.lib.fragments.mission.MissionDetailsFragment;
import com.mapotempo.lib.fragments.mission.MissionsPagerFragment;
import com.mapotempo.lib.fragments.missions.MissionsListFragment;

public class MainActivity extends MapotempoBaseActivity implements MissionsListFragment.OnMissionSelectedListener,
    MissionsPagerFragment.OnMissionFocusListener,
    MissionDetailsFragment.OnMissionDetailsFragmentListener,
    MainMenuFragment.OnMenuInteractionListener
    /* LocationListener */
{

    //TODO
    //    ArrayList<LocationDetailsInterface> mLocationPool = new ArrayList<LocationDetailsInterface>();

    // ===================================
    // ==  Android Activity Life cycle  ==
    // ===================================
    private DrawerLayout mDrawerLayout;

    private ConnexionReceiver mConnexionReceiver;

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
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawers();
        }
        refreshMissionListFragment();
        refreshMissionsPagerFragment();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(mConnexionReceiver);
    }

    // ========================================
    // ==  OnMissionFocusListener Interface  ==
    // ========================================

    @Override
    public void onMissionFocus(int position)
    {
        MissionsListFragment missionsListFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
        if (missionsListFragment != null)
            missionsListFragment.missionFocus(position);
    }

    // ===========================================
    // ==  OnMissionSelectedListener Interface  ==
    // ===========================================

    @Override
    public void onMissionSelected(final int position)
    {
        MissionsPagerFragment missionsPagerFragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
        if (missionsPagerFragment != null && missionsPagerFragment.isVisible())
        {
            MissionsPagerFragment fragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
            fragment.setCurrentItem(position);
        }
        else
        {
            Intent intent = new Intent(this, MissionActivity.class);
            intent.putExtra("mission_id", position);
            startActivity(intent);
        }
    }

    // ==================================================
    // ==  OnMissionDetailsFragmentListener Interface  ==
    // ==================================================
    @Override
    public void onMapImageViewClick(Mission mission)
    {
        Intent intent = new Intent(this, MapActivity.class);
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
        mapotempoApplication.setManager(null);
        finish();
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void refreshMissionListFragment()
    {
        MissionsListFragment missionsListFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
        if (missionsListFragment != null && missionsListFragment.isVisible())
            missionsListFragment.notifyDataChange();
    }

    private void refreshMissionsPagerFragment()
    {
        MissionsPagerFragment missionsPagerFragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
        if (missionsPagerFragment != null && missionsPagerFragment.isVisible())
            missionsPagerFragment.notifyDataChange();
    }

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
