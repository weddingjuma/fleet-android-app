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

package com.mapotempo.lib.fragments.missions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.accessor.AccessInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment is responsible for display all missions assigned to the user currently connected.
 * Each view displayed by this fragment is composed of 3 main UI elements :
 * <ul>
 * <li>The name </li>
 * <li>The delivery date</li>
 * <li>The delivery hour</li>
 * </ul>
 * <p>
 * <h3>Integration</h3>
 * First and foremost, it is needed to implement the fragment through XML using the following class : <code> {@literal <fragment class="mapotempo.com.mapotempo_fleet_android.mission.MissionsPagerFragment"} </code>
 * <p>
 * This fragment require the implementation of {@link OnMissionSelectedListener} directly in the Activity that hold the List Fragment.
 * Then Override the {@link OnMissionSelectedListener#onMissionSelected(int)}
 * which force to return an Android's onMissionFocus Listener. If you don't know about Event Listeners please check the documentation here : <a href="https://developer.android.com/reference/android/view/View.OnClickListener.html" target="_blank">Android Listener</a> <br>
 * Feel free to put any logic inside the listener. Keep in mind that the position returned can be used to get the detailed view of the mission triggered by a onMissionFocus.
 * </p>
 * <p>
 * <b>Here is an example of usability: </b>
 * <pre>
 *     {@literal @Override}
 *     public View.OnClickListener onMissionSelected(final int position) {
 *         View.OnClickListener onClick = new View.OnClickListener() {
 *             {@literal @Override}
 *             public void onClick(View v) {
 *                 intent = new Intent(v.getContext(), YouNewActivity.class);
 *                 intent.putExtra("mission_position", position);
 *                 v.getContext().startActivity(intent);
 *             }
 *         }
 *         return onClick;
 *    }
 * </pre>
 */
public class MissionsListFragment extends MapotempoBaseFragment {

    private RecyclerView mRecyclerView;

    private OnMissionSelectedListener mListener;

    private MissionsRecyclerViewAdapter mRecyclerAdapter;

    private List<MissionInterface> mMissions = new ArrayList<>();

    private FrameLayout mDefaultFrameLayout;

    private AccessInterface.ChangeListener<MissionInterface> missionChangeListener = new AccessInterface.ChangeListener<MissionInterface>() {
        @Override
        public void changed(final List<MissionInterface> missions) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setMissions(missions);
                }
            });
        }
    };

    private ListBehavior mBehavior = ListBehavior.FOCUS;

    final static int REQUEST_ACCESS_FINE_LOCATION = 666;

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MissionsListFragment);

        CharSequence behaviorType = a.getText(R.styleable.MissionsListFragment_ViewStyle);
        if (behaviorType != null) {
            mBehavior = ListBehavior.fromInteger(Integer.parseInt(behaviorType.toString()));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMissionSelectedListener) {
            mListener = (OnMissionSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMissionSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme);
        if (false) // FIXME to finish when theme switch will be implemented
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme_Night);

        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.fragment_missions_list, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new MissionsRecyclerViewAdapter(getContext(), new OnMissionSelectedListener() {
            @Override
            public void onMissionSelected(int position) {
                mListener.onMissionSelected(position);
            }
        }, mMissions, mBehavior);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mDefaultFrameLayout = view.findViewById(R.id.default_layout);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MapotempoFleetManagerInterface mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
        if (mapotempoFleetManagerInterface != null) {
            mapotempoFleetManagerInterface.getMissionAccess().addChangeListener(missionChangeListener);
            setMissions(mapotempoFleetManagerInterface.getMissionAccess().getAll());
        }
    }

    @Override
    public void onPause() {
        MapotempoFleetManagerInterface mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
        if (mapotempoFleetManagerInterface != null) {
            mapotempoFleetManagerInterface.getMissionAccess().removeChangeListener(missionChangeListener);
        }
        super.onPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                explain();
            else
                askForPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION)
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                if (!shouldShowRequestPermissionRationale(permissions[0]))
                    displayOptions();
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void notifyDataChange() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void missionFocus(int position) {
        mRecyclerView.smoothScrollToPosition(position);
        if (mRecyclerAdapter != null)
            mRecyclerAdapter.setMissionFocus(position);
    }

    /**
     * This interface must be implemented by activities that contain {@link MissionsListFragment}
     */
    public interface OnMissionSelectedListener {
        /**
         * A Callback triggered when an item list is created. Use it to set a onMissionFocus listener to each of them.
         *
         * @param position return the item list position
         */
        void onMissionSelected(int position);
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void setMissions(List<MissionInterface> missions) {
        mRecyclerAdapter.notifyDataSyncHasChanged(missions);
        if (missions == null || missions.size() == 0) {
            mDefaultFrameLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mDefaultFrameLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void displayOptions() {
        Snackbar.make(getView(), com.mapotempo.lib.R.string.disabled_permission, Snackbar.LENGTH_LONG).setAction(com.mapotempo.lib.R.string.settings, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                final Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }).show();
    }

    private void explain() {
        askForPermission();

        Snackbar.make(getView(), com.mapotempo.lib.R.string.explaination_location_permission, Snackbar.LENGTH_LONG).setAction(com.mapotempo.lib.R.string.Activate, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPermission();
            }
        }).show();
    }

    private void askForPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
    }
}
