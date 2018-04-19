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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.lib.R;
import com.mapotempo.lib.utils.DateHelpers;
import com.mapotempo.lib.utils.SVGDrawableHelper;
import com.mapotempo.lib.view.action.MissionActionPanel;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Mission} and makes a call to the
 * specified {@link MissionsListFragment.OnMissionSelectedListener}.
 */
class MissionsRecyclerViewAdapter extends RecyclerView.Adapter<MissionsRecyclerViewAdapter.ViewHolder> {

    private MissionsListFragment.OnMissionSelectedListener mListener;

    private int missionsCount = 0;

    private List<Mission> mMissions;

    private int mMissionFocus = 0;

    private ListBehavior mBehavior = ListBehavior.FOCUS;

    // ===================
    // ==  Constructor  ==
    // ===================

    public MissionsRecyclerViewAdapter(Context context, MissionsListFragment.OnMissionSelectedListener listener, List<Mission> missions, ListBehavior behavior) {
        mMissions = missions;
        missionsCount = missions.size();
        mListener = listener;
        mBehavior = behavior;
    }

    // ======================================
    // ==  RecyclerView.Adapter Interface  ==
    // ======================================

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_missions_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Mission mission = mMissions.get(position);
        holder.setMission(mission, position);
        holder.setBackgroundFocus(position == mMissionFocus);
    }

    @Override
    public int getItemCount() {
        return missionsCount;
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void setMissionFocus(int position) {
        int oldPosition = mMissionFocus;
        mMissionFocus = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(mMissionFocus);
    }

    public void notifyDataSyncHasChanged(List<Mission> missions) {
        mMissions = missions;
        missionsCount = missions.size();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Mission mItem;
        final View mView;
        final TextView mName;
        final TextView mListId;
        final TextView mAddress;
        final TextView mDelivery_hour;
        final TextView mDelivery_date;
        final FrameLayout mSelected;
        final MissionActionPanel mMissionActionPanel;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mSelected = view.findViewById(R.id.selected);
            mName = view.findViewById(R.id.name);
            mListId = view.findViewById(R.id.list_id);
            mAddress = view.findViewById(R.id.address);
            mDelivery_hour = view.findViewById(R.id.delivery_hour);
            mDelivery_date = view.findViewById(R.id.delivery_date);
            mMissionActionPanel = view.findViewById(R.id.status_panel);
        }

        void setMission(Mission mission, final int position) {
            String missionDate = DateHelpers.parse(mission.getETAOrDefault(), DateHelpers.DateStyle.SHORTDATE);
            String missionHour = DateHelpers.parse(mission.getETAOrDefault(), DateHelpers.DateStyle.HOURMINUTES);
            mItem = mission;
            mName.setText(mission.getName());
            mListId.setText(Integer.toString(position + 1));
            mAddress.setText(String.format("%s %s %s", mission.getAddress().getStreet(), mission.getAddress().getPostalcode(), mission.getAddress().getCity()));
            mDelivery_date.setText(missionDate);
            mDelivery_hour.setText(missionHour);
            mMissionActionPanel.setBackgroundColor(Color.parseColor(mission.getStatusType().getColor()));
            Drawable drawable = SVGDrawableHelper.getDrawableFromSVGPath(mission.getStatusType().getSVGPath(), "#ffffff", new BitmapDrawable());
            mMissionActionPanel.setImageDrawable(drawable);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onMissionSelected(position);
                }
            });
        }

        void setBackgroundFocus(boolean focus) {
            if (focus && mBehavior == ListBehavior.FOCUS)
                mSelected.setVisibility(View.VISIBLE);
            else
                mSelected.setVisibility(View.INVISIBLE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
