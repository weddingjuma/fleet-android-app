package mapotempo.com.mapotempo_fleet_android;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import mapotempo.com.mapotempo_fleet_android.dummy.MissionModel;

/**
 * Created by julien on 07/08/17.
 */

public class MapotempoFragmentsController {

    protected MissionsFragment missionsFragment = null;
    protected MissionFragment missionFragment = null;
    protected PagerAdapter mPagerAdapter = null;
    protected ViewPager mPager = null;


    public enum ActionOnMission {
        DELETE,
        UPDATE,
        CREATE,
        SHOW
    }

    public MapotempoFragmentsController() {
        // Silence is golden
    }

//    @Override
//    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
//        View view = super.onCreateView(parent, name, context, attrs);
//
//        this.missionsFragment = (MissionsFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
//        this.missionFragment = (MissionFragment) getSupportFragmentManager().findFragmentById(R.id.singleMission);
//
//        return view;
//    }

    /************************************************
     *
     * Overriding interface for fragment implementations ( MISSIONS )
     */

    public void onListMissionsInteraction(MissionModel mission, ActionOnMission action) {
        if (action != null)
            stateMachine(mission, action);
    }

    public void onSingleMissionInteraction(MissionModel mission, ActionOnMission action) {
        if (action != null)
            stateMachine(mission, action);
    }


    private void stateMachine(MissionModel mission, ActionOnMission action) {
        switch (action) {
            case DELETE:
                if (missionsFragment != null)
                    missionsFragment.recyclerView.getAdapter().notifyDataSetChanged();

                if (mPagerAdapter != null)
                    mPagerAdapter.notifyDataSetChanged();
                break;
            case UPDATE:
                if (missionsFragment != null)
                    missionsFragment.recyclerView.getAdapter().notifyDataSetChanged();

                if (missionFragment != null)
                    missionFragment.fillViewFromActivity(mission.id);
                break;
            case CREATE:
                if (missionsFragment != null)
                    missionsFragment.recyclerView.getAdapter().notifyDataSetChanged();

                if (missionFragment != null)
                    missionFragment.fillViewFromActivity(0);
                break;
            case SHOW:
                missionFragment.fillViewFromActivity(mission.id);
                break;
        }
    }
}
