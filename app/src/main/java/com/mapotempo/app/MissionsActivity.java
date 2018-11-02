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

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mapotempo.app.base.MapotempoBaseActivity;
import com.mapotempo.fleet.core.accessor.LiveAccessChangeListener;
import com.mapotempo.fleet.core.accessor.LiveAccessToken;
import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.fleet.dao.model.Route;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.fragments.mission.MissionsPagerFragment;
import com.mapotempo.lib.fragments.mission.OnMissionDetailsFragmentListener;
import com.mapotempo.lib.fragments.mission.OnMissionFocusListener;
import com.mapotempo.lib.fragments.missions.MissionsListFragment;
import com.mapotempo.lib.fragments.missions.OnMissionSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class MissionsActivity extends MapotempoBaseActivity implements OnMissionSelectedListener, OnMissionFocusListener, OnMissionDetailsFragmentListener
{
    private String mRouteId = "";

    private MissionsListFragment mMissionListFragment;

    private MissionsPagerFragment mMissionPagerFragment;

    private LiveAccessToken mLiveAccessToken;

    private List<Mission> mMissions = new ArrayList<>();

    private String CURRENT_INDEX_TAG = "current_index";

    private int mCurrentIndex = 0;

    private String MISSION_IS_SELECTED_TAG = "portrait_pager_visibility";

    private boolean mMissionIsSelected = false;

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (mMissionIsSelected && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            mMissionIsSelected = false;
            refreshMissionPagerFragmentVisibility();
        }
        else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.survey_location:
            if (getCurrentMission() != null)
            {
                Intent intent = new Intent(this, EditLocationActivity.class);
                intent.putExtra("mission_id", getCurrentMission().getId());
                startActivity(intent);
            }
            return true;
        case R.id.edit_address:
            if (getCurrentMission() != null)
            {
                Intent intent = new Intent(this, EditAddressActivity.class);
                intent.putExtra("mission_id", getCurrentMission().getId());
                startActivity(intent);
            }
            return true;
        case R.id.get_signature:
            if (getCurrentMission() != null)
            {
                mMissionPagerFragment.goSignatureFragment();
            }
            return true;
        case R.id.take_picture:
            if (getCurrentMission() != null)
            {
                mMissionPagerFragment.goPictureFragment();
            }

            return true;
        case R.id.write_comment:
            if (getCurrentMission() != null)
            {
                Intent intent = new Intent(this, EditCommentActivity.class);
                intent.putExtra("mission_id", getCurrentMission().getId());
                startActivity(intent);
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    // ===================================
    // ==  Android Activity Life cycle  ==
    // ===================================

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.missions_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.mission);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState != null)
        {
            mMissionIsSelected = savedInstanceState.getBoolean(MISSION_IS_SELECTED_TAG, false);
            mCurrentIndex = savedInstanceState.getInt(CURRENT_INDEX_TAG, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (mMissionIsSelected || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            getMenuInflater().inflate(com.mapotempo.app.R.menu.menu_mission, menu);
            for (int i = 0; i < menu.size(); i++)
            {
                Drawable drawable = menu.getItem(i).getIcon();
                if (drawable != null)
                {
                    drawable.mutate();
                    drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }
            }
        }
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mMissionListFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.missionsList);
        mMissionPagerFragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.missionsPager);

        mRouteId = getIntent().getStringExtra("route_id");
        MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getApplicationContext()).getManager();
        Route route = mapotempoFleetManagerInterface.getRouteAccess().get(mRouteId);

        if (route == null)
        {
            super.onBackPressed();
            return;
        }

        mLiveAccessToken = mapotempoFleetManagerInterface.getMissionAccess().byRoute_AddListener(new LiveAccessChangeListener<Mission>()
        {
            @Override
            public void changed(List<Mission> missions)
            {
                mMissions = missions;
                refreshFragments();
            }
        }, route);

        refreshMissionPagerFragmentVisibility();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getApplicationContext()).getManager();
        mapotempoFleetManagerInterface.getMissionAccess().removeListener(mLiveAccessToken);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putBoolean(MISSION_IS_SELECTED_TAG, mMissionIsSelected);
        savedInstanceState.putInt(CURRENT_INDEX_TAG, mCurrentIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    // ===========================================
    // ==  OnMissionSelectedListener Interface  ==
    // ===========================================
    @Override
    public void onMissionSelected(final Mission mission)
    {
        mMissionIsSelected = true;
        mCurrentIndex = findIndex(mission);
        refreshMissionPagerFragmentVisibility();
        refreshFragments();
    }

    // ========================================
    // ==  OnMissionFocusListener Interface  ==
    // ========================================
    @Override
    public void onMissionFocus(Mission mission)
    {
        mCurrentIndex = findIndex(mission);
        refreshFragments();
    }

    // ==================================================
    // ==  OnMissionDetailsFragmentListener Interface  ==
    // ==================================================
    @Override
    public void onMapImageViewClick(Mission mission)
    {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("route_id", mRouteId);
        intent.putExtra("mission_id", mission.getId());
        startActivity(intent);
    }

    // ===============
    // ==  Private  ==
    // ===============
    private void refreshMissionPagerFragmentVisibility()
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mMissionIsSelected || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            transaction.show(mMissionPagerFragment).commit();
        else
            transaction.hide(mMissionPagerFragment).commit();
        invalidateOptionsMenu();
    }

    private void refreshFragments()
    {
        if (mMissions.isEmpty())
        {
            finish();
            return;
        }

        if (mMissionListFragment.isAdded())
        {
            mMissionListFragment.setMission(mMissions);
            mMissionListFragment.setCurrentItem(mCurrentIndex);
        }
        if (mMissionPagerFragment.isAdded())
        {
            mMissionPagerFragment.setMissions(mMissions);
            mMissionPagerFragment.setCurrentItem(mCurrentIndex);
        }
    }

    private int findIndex(Mission mission)
    {
        int res = 0;
        for (int i = 0; i < mMissions.size(); i++)
        {
            if (mission.getId() == mMissions.get(i).getId())
                res = i;
        }
        return res;
    }

    private @Nullable
    Mission getCurrentMission()
    {
        if (mCurrentIndex < mMissions.size())
            return mMissions.get(mCurrentIndex);
        return null;
    }
}
