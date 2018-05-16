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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.accessor.AccessInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This fragment act as a manger for the {@link MissionDetailsFragment}.
 * It is used to :
 * <ul>
 * <li>Detect if a ViewPager must be used</li>
 * <li>Return the current view displayed</li>
 * <li>Provide the {@link MissionInterface} object to {@link MissionDetailsFragment}</li>
 * </ul>
 * <p>
 * <h3>Integration</h3>
 * First and foremost, it is needed to implement the fragment through XML using the following class :
 * {@literal <fragment class="mapotempo.com.mapotempo_fleet_android.mission.MissionsPagerFragment"} <br>
 * {@literal android:id="@+id/base_fragment"} <br>
 * {@literal app:ViewStyle="SCROLLVIEW"} <br>
 * {@literal android:layout_width="match_parent"} <br>
 * {@literal android:layout_height="match_parent" />} <br>
 * <p>
 * It is Highly recommended to set up the enum "ViewStyle" to "SCROLLVIEW" in order to benefit of full features.
 * <p>
 * This fragment require the implementation of {@link OnMissionFocusListener} directly in the Activity that hold the List Fragment.
 * Then Override the {@link OnMissionFocusListener#onMissionFocus(int)} is required to use this fragment.
 * </p>
 * <p>
 * <b>Here is an example of usability: </b>
 * <pre>
 * @Override
 * public int onMissionFocus(int position) {
 *      MissionsListFragment missionsFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
 *
 *      if (missionsFragment != null)
 *      missionsFragment.setMissionFocus(position);
 *
 *      return position;
 *  }
 * </pre>
 */
public class MissionsPagerFragment extends MapotempoBaseFragment {

    private static final String CURRENT_POSITION = "current_position";

    private ViewPager mViewPager;

    private MissionPagerAdapter mPagerAdapter;

    private OnMissionFocusListener mListener;

    private int mCurrentPosition = 0;

    private AccessInterface.ChangeListener<MissionInterface> missionChangeListener;

    public MissionsPagerFragment() {
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnMissionFocusListener) context;
        if (mListener == null)
            throw new RuntimeException("You must implement OnMissionFocusListener Interface");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get extra argument and savedInstanceState can override it.
        mCurrentPosition = getActivity().getIntent().getIntExtra("mission_id", 0);
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(CURRENT_POSITION, 0);
        }

        missionChangeListener = new AccessInterface.ChangeListener<MissionInterface>() {
            @Override
            public void changed(final List<MissionInterface> missions) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<MissionInterface> oldDeprecatedMissions = mPagerAdapter.getMissionsList(); // Keep track of old data using a shallow copy
                        MissionInterface mission = oldDeprecatedMissions.get(mViewPager.getCurrentItem()); // Keep the mission currently displayed
                        int newMissionPosition = 0;

                        // DO NOTHING IF DATA NEED TO BE REFRESHED
                        if (oldDeprecatedMissions.equals(missions)) {
                            return;
                        } else if (missions.size() == 0) {
                            MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
                            mapotempoApplication.getManager().getMissionAccess().removeChangeListener(missionChangeListener);
                            getActivity().finish();
                            return;
                        } else {
                            mPagerAdapter.refreshMissions(missions);
                        }

//                        // Update previous list to prevent useless updates
//                        for (MissionInterface loopMission : missions) {
//                            if (!oldDeprecatedMissions.contains(loopMission)) {
//                                mPagerAdapter.addMission(loopMission);
//                            } else {
//                                int id = oldDeprecatedMissions.indexOf(loopMission);
//                                oldDeprecatedMissions.remove(id);
//                            }
//                        }
//
//                        // Remove deprecated mission
//                        mPagerAdapter.removeMissions(oldDeprecatedMissions);
//
//                        mPagerAdapter.notifyDataSetChanged();

                        // Search for new index
                        Iterator<MissionInterface> iterable = missions.iterator();
                        while (iterable.hasNext()) {
                            if (mission.getId().equals(iterable.next().getId())) {
                                newMissionPosition = mPagerAdapter.getItemPosition(iterable.next());
                                break;
                            }
                        }

                        // Update pager position according to the mission new position
                        mViewPager.setCurrentItem(newMissionPosition);
                    }
                });
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        List<MissionInterface> missions = new ArrayList<>();
        if (mapotempoApplication.getManager() != null) {
            missions = mapotempoApplication.getManager().getMissionAccess().getAll();
            mapotempoApplication.getManager().getMissionAccess().addChangeListener(missionChangeListener);
        }

        View view = inflater.inflate(R.layout.fragment_mission_pager, container, false);
        LinearLayout content = view.findViewById(R.id.mission_view_content);

        mPagerAdapter = new MissionPagerAdapter(getFragmentManager(), missions.size(), missions);
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
    public void onDetach() {
        super.onDetach();
        MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        mapotempoApplication.getManager().getMissionAccess().removeChangeListener(missionChangeListener);
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, mCurrentPosition);
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void notifyDataChange() {
        mPagerAdapter.notifyDataSetChanged();
    }

    @Nullable
    public MissionInterface getCurrentMission() {
        MapotempoFleetManagerInterface mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
        List<MissionInterface> missions = mapotempoFleetManagerInterface.getMissionAccess().getAll();
        MissionInterface ms = missions.get(mViewPager.getCurrentItem());
        return ms;
    }

    public void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position, true);
    }

    public interface OnMissionFocusListener {
        void onMissionFocus(int page);
    }

    public boolean onBackPressed() {
        MissionDetailsFragment missionDetailsFragment = mPagerAdapter.getFragment(mViewPager.getCurrentItem());
        if (missionDetailsFragment != null)
            return missionDetailsFragment.onBackPressed();
        return false;
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void setPagerChangeListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mListener.onMissionFocus(position);
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
