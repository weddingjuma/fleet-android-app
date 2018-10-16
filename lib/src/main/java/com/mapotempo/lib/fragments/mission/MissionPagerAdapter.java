/*
 * Copyright © Mapotempo, 2018
 *
 * This file is part of Mapotempo.
 *
 * Mapotempo is free software. You can redistribute it and/or
 * modify since you respect the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Mapotempo is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the Licenses for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Mapotempo. If not, see:
 * <http://www.gnu.org/licenses/agpl.html>
 */

package com.mapotempo.lib.fragments.mission;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.mapotempo.fleet.dao.model.Mission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MissionPagerAdapter extends FragmentStatePagerAdapter
{

    private int mCount;
    private List<Mission> mMissions;
    private Map<Integer, MissionDetailsFragment> mPageReferenceMap = new HashMap<>();

    public MissionPagerAdapter(FragmentManager fm, List<Mission> missions)
    {
        super(fm);
        mCount = missions.size();
        mMissions = new ArrayList<>(missions);
    }

    public MissionDetailsFragment getFragment(int position)
    {
        return mPageReferenceMap.get(position);
    }

    @Override
    public Fragment getItem(int position)
    {
        MissionDetailsFragment fragment = MissionDetailsFragment.create(position, mMissions.get(position));
        mPageReferenceMap.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    @Override
    public int getCount()
    {
        return mMissions.size();
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public Parcelable saveState()
    {
        return null;
    }

    public void refreshMissions(List<Mission> newMissions)
    {
        mMissions = newMissions;
        notifyDataSetChanged();
    }


    public List<Mission> getMissionsList()
    {
        return mMissions;
    }


}
