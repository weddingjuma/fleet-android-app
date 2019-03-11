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

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mapotempo.fleet.core.accessor.LiveAccessToken;
import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;

import java.util.ArrayList;
import java.util.List;

public class MissionsListFragment extends MapotempoBaseFragment
{
    private RecyclerView mRecyclerView;

    private OnMissionSelectedListener mListener;

    private MissionsRecyclerViewAdapter mRecyclerAdapter;

    private List<Mission> mMissions = new ArrayList<>();

    private FrameLayout mDefaultFrameLayout;

    private LiveAccessToken missionChangeListenerToken;

    private ListBehavior mBehavior = ListBehavior.FOCUS;

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState)
    {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MissionsListFragment);

        CharSequence behaviorType = a.getText(R.styleable.MissionsListFragment_ViewStyle);
        if (behaviorType != null)
        {
            mBehavior = ListBehavior.fromInteger(Integer.parseInt(behaviorType.toString()));
        }
        a.recycle();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnMissionSelectedListener)
        {
            mListener = (OnMissionSelectedListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement " + OnMissionSelectedListener.class.getName());
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
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme);
        if (false) // FIXME to finish when theme switch will be implemented
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme_Night);

        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.fragment_missions_list, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new MissionsRecyclerViewAdapter(getContext(), mListener, mMissions, mBehavior);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mDefaultFrameLayout = view.findViewById(R.id.default_layout);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    // ==============
    // ==  Public  ==
    // ==============
    public void setMission(@NonNull List<Mission> missions)
    {
        mMissions = missions;
        mRecyclerAdapter.notifyDataSyncHasChanged(mMissions);
        if (mMissions == null || mMissions.size() == 0)
        {
            mDefaultFrameLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
        else
        {
            mDefaultFrameLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void notifyDataChange()
    {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setCurrentItem(int position)
    {
        mRecyclerView.smoothScrollToPosition(position);
        if (mRecyclerAdapter != null)
            mRecyclerAdapter.setMissionFocus(position);
    }
}
