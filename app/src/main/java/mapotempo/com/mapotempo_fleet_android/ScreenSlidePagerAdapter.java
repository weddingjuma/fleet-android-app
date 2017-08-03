package mapotempo.com.mapotempo_fleet_android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import mapotempo.com.mapotempo_fleet_android.dummy.MissionsManager;

class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MissionFragment.create(position);
    }

    @Override
    public int getCount() {
        return MissionsManager.getInstance().getMissionsCount();
    }
}