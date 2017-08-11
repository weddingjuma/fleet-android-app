package mapotempo.com.mapotempo_fleet_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapotempo.fleet.core.model.Mission;

public class SingleMissionView extends AppCompatActivity implements MissionFragment.OnFragmentInteractionListener, MissionContainerFragment.ContainerFragmentMission {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mission_view);
    }

    @Override
    public void onSingleMissionInteraction(Mission mission) {
        MissionsFragment missionsFragment = (MissionsFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
        MissionContainerFragment fragment = (MissionContainerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);

        if (missionsFragment != null)
            missionsFragment.recyclerView.getAdapter().notifyDataSetChanged();

        if (fragment != null)
            fragment.notifyDataChange();
    }

    @Override
    public int wichViewIsTheCurrent(int position) {
        return position;
    }
}
