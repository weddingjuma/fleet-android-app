package mapotempo.com.mapotempo_fleet_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapotempo.fleet.api.model.MissionInterface;

import mapotempo.com.mapotempo_fleet_android.R;
import mapotempo.com.mapotempo_fleet_android.mission.MissionDetailsFragment;
import mapotempo.com.mapotempo_fleet_android.mission.MissionFragment;

public class MissionActivity extends AppCompatActivity implements MissionDetailsFragment.OnFragmentInteractionListener, MissionFragment.ContainerFragmentMission
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mission_view);

        getSupportActionBar().setTitle(R.string.mission);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSingleMissionInteraction(MissionInterface mission) {
    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }

    @Override
    public int wichViewIsTheCurrent(int position) {
        return position;
    }
}
