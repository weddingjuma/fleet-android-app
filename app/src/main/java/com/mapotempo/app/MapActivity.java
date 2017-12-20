package com.mapotempo.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.lib.mission.MissionDetailsFragment;

public class MapActivity extends AppCompatActivity implements MissionDetailsFragment.OnMissionDetailsFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapImageViewClick(MissionInterface mission) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}


