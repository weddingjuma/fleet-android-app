package com.mapotempo.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.lib.fragments.mission.MissionDetailsFragment;
import com.mapotempo.lib.fragments.mission.MissionsPagerFragment;

public class MissionActivity extends AppCompatActivity implements
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
