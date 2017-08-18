package mapotempo.com.mapotempo_fleet_android;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.mapotempo.fleet.core.model.Mission;

import java.util.ArrayList;
import java.util.List;

class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private int mCount;
    private List<Mission> mMissions;

    public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm, int count, List<Mission> mission) {
        super(fm);
        mCount = count;
        mMissions = new ArrayList<>();
        mMissions = mission;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = MissionFragment.create(position, mMissions.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public void addMission(Mission mission) {
        mMissions.add(mission);
        mCount++;

        notifyDataSetChanged();
    }

    public void updateMissions(List<Mission> missions) {
        mMissions = missions;
        mCount = missions.size();

        notifyDataSetChanged();
    }

}