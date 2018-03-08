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

package com.mapotempo.lib.fragments.actions;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.fleet.api.model.MissionActionTypeInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;

import java.util.ArrayList;
import java.util.List;

public class ActionsListFragment extends MapotempoBaseFragment {

    private RecyclerView mRecyclerView;

    private ActionsRecyclerViewAdapter mRecyclerAdapter;

    private List<MissionActionTypeInterface> mMissionStatusActions = new ArrayList<>();

    private ActionsRecyclerViewAdapter.OnMissionActionSelectedListener mListener = new ActionsRecyclerViewAdapter.OnMissionActionSelectedListener() {
        @Override
        public void onMissionActionSelected(MissionActionTypeInterface action) {
            // DEFAULT
        }
    };

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme);
        if (false) // TODO to finish when theme switch will be implemented
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme_Night);

        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.fragment_actions, container, false);
        mRecyclerView = view.findViewById(R.id.action_recycler_view);
        mRecyclerAdapter = new ActionsRecyclerViewAdapter(getContext(), new ActionsRecyclerViewAdapter.OnMissionActionSelectedListener() {
            @Override
            public void onMissionActionSelected(MissionActionTypeInterface action) {
                mListener.onMissionActionSelected(action);
            }
        }, mMissionStatusActions);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        return view;
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void setActions(List<MissionActionTypeInterface> missionStatusActions) {
        mMissionStatusActions = new ArrayList<>(missionStatusActions);
        mRecyclerAdapter.notifyDataSyncHasChanged(mMissionStatusActions);
    }


    public void setOnActionSelectedListener(ActionsRecyclerViewAdapter.OnMissionActionSelectedListener onActionSelectedListener) {
        mListener = onActionSelectedListener;
    }
}
