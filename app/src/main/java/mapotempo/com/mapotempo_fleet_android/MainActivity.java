package mapotempo.com.mapotempo_fleet_android;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.content.res.Configuration;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mapotempo.com.mapotempo_fleet_android.dummy.MissionModel;
import mapotempo.com.mapotempo_fleet_android.dummy.MissionsManager;

public class MainActivity extends AppCompatActivity implements MissionsFragment.OnMissionsInteractionListener, MissionFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public View.OnClickListener onListMissionsInteraction(final MissionModel item) {
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    MissionContainerFragment fragment = (MissionContainerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);

                    fragment.setCurrentItem(MissionsManager.getInstance().getIndexOf(item.id));
                } else {
                    Intent intent = new Intent(v.getContext(), SingleMissionView.class);
                    intent.putExtra("mission_id", item.id);

                    v.getContext().startActivity(intent);
                }
            }
        };

        return onClick;
    }

    @Override
    public void onSingleMissionInteraction(MissionModel mission) {
        MissionsFragment missionsFragment = (MissionsFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
        MissionContainerFragment fragment = (MissionContainerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);

        if (missionsFragment != null)
            missionsFragment.recyclerView.getAdapter().notifyDataSetChanged();

        if (fragment != null)
            fragment.notifyDataChange();
    }

    @Override
    public MissionModel wichViewIsTheCurrent(MissionModel m) {
        MissionsFragment missionsFragment = (MissionsFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);

        if (missionsFragment != null)
            missionsFragment.setCurrentMission(m);

        return m;
    }
}


