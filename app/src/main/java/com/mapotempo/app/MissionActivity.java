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

import com.mapotempo.app.base.MapotempoBaseActivity;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.lib.fragments.mission.MissionDetailsFragment;
import com.mapotempo.lib.fragments.mission.MissionsPagerFragment;

public class MissionActivity extends MapotempoBaseActivity implements
        MissionsPagerFragment.OnMissionFocusListener,
        MissionDetailsFragment.OnMissionDetailsFragmentListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mission_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.mission);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMissionFocus(int position) {
    }

    @Override
    public void onBackPressed() {
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
    public void onMapImageViewClick(MissionInterface mission) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("mission_id", mission.getId());
        startActivity(intent);
    }
}
