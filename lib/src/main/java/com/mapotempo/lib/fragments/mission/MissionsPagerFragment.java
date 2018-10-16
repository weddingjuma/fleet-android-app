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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mapotempo.fleet.core.accessor.LiveAccessToken;
import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MissionsPagerFragment extends MapotempoBaseFragment
{

    private static final String CURRENT_POSITION = "current_position";

    private ViewPager mViewPager;

    private MissionPagerAdapter mPagerAdapter;

    private OnMissionFocusListener mListener;

    private int mCurrentPosition = 0;

    private LiveAccessToken missionChangeListenerToken;

    public MissionsPagerFragment()
    {
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState)
    {
        super.onInflate(context, attrs, savedInstanceState);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnMissionFocusListener)
        {
            mListener = (OnMissionFocusListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement " + OnMissionFocusListener.class.getName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(this.getClass().getSimpleName(), "++++++++++++" + this.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_mission_pager, container, false);
        LinearLayout content = view.findViewById(R.id.mission_view_content);

        mPagerAdapter = new MissionPagerAdapter(getFragmentManager(), new ArrayList<Mission>());
        ViewPager viewPager = (ViewPager) getActivity().getLayoutInflater().inflate(R.layout.view_pager, null);
        viewPager.setPageTransformer(true, new MissionsPagerBorderTransformer());
        mViewPager = viewPager.findViewById(R.id.mission_viewpager);
        // mViewPager.setPageTransformer(true, new DepthPageTransformer());
        setPagerChangeListener();
        content.addView(viewPager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);
        return view;
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, mCurrentPosition);
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void notifyDataChange()
    {
        mPagerAdapter.notifyDataSetChanged();
    }

    @Nullable
    public Mission getCurrentMission()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(calendar.HOUR, -12);

        MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
        List<Mission> missions = mapotempoFleetManagerInterface.getMissionAccess().byDateGreaterThan(calendar.getTime());
        Mission ms = missions.get(mViewPager.getCurrentItem());
        return ms;
    }

    public void setCurrentItem(int index)
    {
        mViewPager.setCurrentItem(index, true);
    }

    public void setMissions(List<Mission> missions)
    {
        List<Mission> oldDeprecatedMissions = mPagerAdapter.getMissionsList(); // Keep track of old data using a shallow copy
        // DO NOTHING IF DATA NEED TO BE REFRESHED
        if (oldDeprecatedMissions.equals(missions) || missions.size() == 0)
            return;
        else
        {
            mPagerAdapter.refreshMissions(missions);
        }
    }

    public boolean onBackPressed()
    {
        MissionDetailsFragment missionDetailsFragment = mPagerAdapter.getFragment(mViewPager.getCurrentItem());
        if (missionDetailsFragment != null)
            return missionDetailsFragment.onBackPressed();
        return false;
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void setPagerChangeListener()
    {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                List<Mission> missions = mPagerAdapter.getMissionsList();
                mCurrentPosition = position;
                mListener.onMissionFocus(missions.get(mCurrentPosition));
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
    }
}
