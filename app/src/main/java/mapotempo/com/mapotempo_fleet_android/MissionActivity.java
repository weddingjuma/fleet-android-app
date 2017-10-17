package mapotempo.com.mapotempo_fleet_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import mapotempo.com.mapotempo_fleet_android.mission.MissionsPagerFragment;

public class MissionActivity extends AppCompatActivity implements MissionsPagerFragment.OnMissionFocusListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mission_view);

        getSupportActionBar().setTitle(R.string.mission);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }

    @Override
    public void onMissionFocus(int position) {
    }
}
