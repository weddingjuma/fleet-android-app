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
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mapotempo.app.base.MapotempoBaseActivity;
import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.lib.fragments.mission.MissionsPagerFragment;
import com.mapotempo.lib.fragments.mission.OnMissionDetailsFragmentListener;
import com.mapotempo.lib.fragments.mission.OnMissionFocusListener;

public class MissionActivity extends MapotempoBaseActivity implements
    OnMissionFocusListener,
    OnMissionDetailsFragmentListener
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mission_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.mission);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(com.mapotempo.app.R.menu.menu_mission, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        MissionsPagerFragment missionDetailsFragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);

        switch (item.getItemId())
        {
        case R.id.edit_location:
            Mission ms = missionDetailsFragment.getCurrentMission();
            if (ms != null)
            {
                Intent intent = new Intent(this, EditLocationActivity.class);
                intent.putExtra("mission_id", ms.getId());
                startActivity(intent);
            }
            return true;
        case R.id.edit_mission_address:
            Mission mission = missionDetailsFragment.getCurrentMission();
            if (mission != null)
            {
                Intent intent = new Intent(this, EditAddressActivity.class);
                intent.putExtra("mission_id", mission.getId());
                startActivity(intent);
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMissionFocus(Mission mission)
    {
    }

    @Override
    public void onBackPressed()
    {
        MissionsPagerFragment missionDetailsFragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
        boolean handle = false;
        if (missionDetailsFragment != null)
            handle = missionDetailsFragment.onBackPressed();

        if (!handle)
            super.onBackPressed();
    }

    // ==================================================
    // ==  OnMissionDetailsFragmentListener Interface  ==
    // ==================================================

    @Override
    public void onMapImageViewClick(Mission mission)
    {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("mission_id", mission.getId());
        startActivity(intent);
    }
}
