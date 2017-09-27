package mapotempo.com.mapotempo_fleet_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.core.model.Mission;

public class SingleMissionView extends AppCompatActivity implements MissionContainerFragment.ContainerFragmentMission {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
    public int wichViewIsTheCurrent(int position) {
        return position;
    }
}
