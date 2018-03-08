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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.fleet.api.model.MissionActionTypeInterface;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.missions.MissionsListFragment;
import com.mapotempo.lib.utils.SVGDrawableHelper;
import com.mapotempo.lib.view.action.MissionActionPanel;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MissionInterface} and makes a call to the
 * specified {@link MissionsListFragment.OnMissionSelectedListener}.
 */
public class ActionsRecyclerViewAdapter extends RecyclerView.Adapter<ActionsRecyclerViewAdapter.ViewHolder> {

    /**
     * This interface must be implemented by activities that contain {@link MissionsListFragment}
     */
    public interface OnMissionActionSelectedListener {
        /**
         * A Callback triggered when an item list is created. Use it to set a onMissionFocus listener to each of them.
         *
         * @param newStatus return the next newStatus item selected
         */
        void onMissionActionSelected(MissionActionTypeInterface newStatus);
    }

    private OnMissionActionSelectedListener mListener;
    private int mActionsCount = 0;
    private List<MissionActionTypeInterface> mActions;
    private Context mContext;

    // ===================
    // ==  Constructor  ==
    // ===================

    public ActionsRecyclerViewAdapter(Context context, OnMissionActionSelectedListener listener, List<MissionActionTypeInterface> actions) {
        mActions = actions;
        mActionsCount = actions.size();
        mListener = listener;
        mContext = context;
    }

    // ======================================
    // ==  RecyclerView.Adapter Interface  ==
    // ======================================

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_actions_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MissionActionTypeInterface action = mActions.get(position);
        holder.setMission(action, position);
    }

    @Override
    public int getItemCount() {
        return mActionsCount;
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void notifyDataSyncHasChanged(List<MissionActionTypeInterface> actions) {
        mActions = actions;
        mActionsCount = actions.size();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MissionActionTypeInterface mItem;
        final MissionActionPanel mActionView;

        public ViewHolder(View view) {
            super(view);
            mActionView = view.findViewById(R.id.status_panel);
            mActionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMissionActionSelected(mItem);
                }
            });
        }

        void setMission(MissionActionTypeInterface action, final int position) {
            mItem = mActions.get(position);
            Drawable d = new BitmapDrawable();
            Drawable drawable = SVGDrawableHelper.getDrawableFromSVGPath(action.getNextStatus().getSVGPath(), "#FFFFFF", d);
            mActionView.setImageDrawable(drawable);
            mActionView.setText(action.getNextStatus().getLabel());
            mActionView.setBackgroundColor(Color.parseColor(action.getNextStatus().getColor()));
        }
    }
}
