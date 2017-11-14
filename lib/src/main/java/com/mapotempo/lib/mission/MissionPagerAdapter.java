package com.mapotempo.lib.mission;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.core.model.Mission;

import java.util.ArrayList;
import java.util.List;

class MissionPagerAdapter extends FragmentStatePagerAdapter {

    private int mCount;
    private List<MissionInterface> mMissions;

    public MissionPagerAdapter(FragmentManager fm, int count, List<MissionInterface> missions) {
        super(fm);
        mCount = count;
        mMissions = new ArrayList<>();
        mMissions = missions;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = MissionDetailsFragment.create(position, mMissions.get(position));
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

    public void updateMissions(List<MissionInterface> missions) {
        mMissions = missions;
        mCount = missions.size();

        notifyDataSetChanged();
    }

}
