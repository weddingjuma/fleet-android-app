package com.mapotempo.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.submodel.LocationDetailsInterface;
import com.mapotempo.lib.menu.MainMenuFragment;
import com.mapotempo.lib.mission.MissionDetailsFragment;
import com.mapotempo.lib.mission.MissionsPagerFragment;
import com.mapotempo.lib.missions.MissionsListFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MissionsListFragment.OnMissionSelectedListener,
        MissionsPagerFragment.OnMissionFocusListener,
        MissionDetailsFragment.OnMissionDetailsFragmentListener,
        MainMenuFragment.OnMenuInteractionListener
        /* LocationListener */ {

    ArrayList<LocationDetailsInterface> mLocationPool = new ArrayList<LocationDetailsInterface>();

    // ===================================
    // ==  Android Activity Life cycle  ==
    // ===================================
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        addDrawableHandler(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
        refreshMissionListFragment();
        refreshMissionsPagerFragment();
    }

    // ========================================
    // ==  OnMissionFocusListener Interface  ==
    // ========================================

    @Override
    public void onMissionFocus(int position) {
        MissionsListFragment missionsListFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
        if (missionsListFragment != null)
            missionsListFragment.missionFocus(position);
    }

    // ===========================================
    // ==  OnMissionSelectedListener Interface  ==
    // ===========================================

    @Override
    public void onMissionSelected(final int position) {
        MissionsPagerFragment missionsPagerFragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
        if (missionsPagerFragment != null && missionsPagerFragment.isVisible()) {
            MissionsPagerFragment fragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
            fragment.setCurrentItem(position);
        } else {
            Intent intent = new Intent(this, MissionActivity.class);
            intent.putExtra("mission_id", position);
            startActivity(intent);
        }
    }

    // ==================================================
    // ==  OnMissionDetailsFragmentListener Interface  ==
    // ==================================================
    @Override
    public void onMapImageViewClick(MissionInterface mission) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    // ===========================================
    // ==  OnMenuInteractionListener Interface  ==
    // ===========================================

    @Override
    public void onMap() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onTracking(boolean tracking_status) {
        return tracking_status;
    }

    @Override
    public void onLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        MapotempoApplication mapotempoApplication = (MapotempoApplication) getApplicationContext();
        mapotempoApplication.setManager(null);
        finish();
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void refreshMissionListFragment() {
        MissionsListFragment missionsListFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
        if (missionsListFragment != null && missionsListFragment.isVisible())
            missionsListFragment.notifyDataChange();
    }

    private void refreshMissionsPagerFragment() {
        MissionsPagerFragment missionsPagerFragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
        if (missionsPagerFragment != null && missionsPagerFragment.isVisible())
            missionsPagerFragment.notifyDataChange();
    }

    private void addDrawableHandler(Toolbar toolbar) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {

                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    supportInvalidateOptionsMenu();
                }
            };

            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
    }
}


