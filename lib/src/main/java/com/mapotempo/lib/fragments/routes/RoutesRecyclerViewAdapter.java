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

package com.mapotempo.lib.fragments.routes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.mapotempo.fleet.dao.model.Route;
import com.mapotempo.lib.R;
import com.mapotempo.lib.utils.DateHelpers;

import java.util.List;

class RoutesRecyclerViewAdapter extends RecyclerView.Adapter<RoutesRecyclerViewAdapter.ViewHolder>
{
    private int routesCount = 0;

    private List<Route> mRoutes;

    private int mRouteFocus = 0;

    private boolean mArchiveBehavior = false;

    private OnRouteSelectedListener mOnRouteSelectedListener;

    private String ARCHIVED_BEHAVIOR_TAG = "ARCHIVED_BEHAVIOR";

    // ===================
    // ==  Constructor  ==
    // ===================

    public RoutesRecyclerViewAdapter(Context context, List<Route> routes, OnRouteSelectedListener onRouteSelectedListener, @Nullable Bundle savedInstanceState)
    {
        mRoutes = routes;
        routesCount = routes.size();
        mOnRouteSelectedListener = onRouteSelectedListener;
        mArchiveBehavior = savedInstanceState != null ? savedInstanceState.getBoolean(ARCHIVED_BEHAVIOR_TAG, false) : false;
    }

    // ======================================
    // ==  RecyclerView.Adapter Interface  ==
    // ======================================

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_routes_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final Route route = mRoutes.get(position);
        holder.setRoute(route);
    }

    @Override
    public int getItemCount()
    {
        return routesCount;
    }

    // ==============
    // ==  Public  ==
    // ==============
    public void saveInstanceState(Bundle outState)
    {
        outState.putBoolean(ARCHIVED_BEHAVIOR_TAG, mArchiveBehavior);
    }

    public void setRoutes(List<Route> routes, boolean archiveBehavior)
    {
        mRoutes = routes;
        routesCount = routes.size();
        mArchiveBehavior = archiveBehavior;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        Route mRoute;
        final SwipeLayout mView;
        final TextView mName;
        final TextView mDate;
        final TextView mHour;
        boolean isSwipe = false;
        ImageView mArchiveAction;

        public ViewHolder(View view)
        {
            super(view);
            mView = (SwipeLayout) view;
            mName = view.findViewById(R.id.name);
            mDate = view.findViewById(R.id.date);
            mHour = view.findViewById(R.id.hour);
            mArchiveAction = view.findViewById(R.id.archive_action);
            mView.addSwipeListener(mSwipeListener);
            mArchiveAction.setOnClickListener(mArchiveActionListener);
        }

        void setRoute(Route route)
        {
            String missionDate = DateHelpers.parse(route.getDate(), DateHelpers.DateStyle.SHORTDATE);
            String missionHour = DateHelpers.parse(route.getDate(), DateHelpers.DateStyle.HOURMINUTES);
            mName.setText(route.getName());
            mDate.setText(missionDate);
            mHour.setText(missionHour);
            mRoute = route;
            if (!mArchiveBehavior)
            {
                mArchiveAction.setImageResource(R.drawable.ic_archive_black_24dp);
            }
            else
            {
                mArchiveAction.setImageResource(R.drawable.ic_unarchive_black_24dp);
            }
            mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!isSwipe)
                        mOnRouteSelectedListener.onRouteSelected(mRoute);
                }
            });
        }

        View.OnClickListener mArchiveActionListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isSwipe = false;
                if (!mArchiveBehavior)
                {
                    mRoute.archived();
                }
                else
                {
                    mRoute.unArchived();
                }
            }
        };

        SwipeLayout.SwipeListener mSwipeListener = new SwipeLayout.SwipeListener()
        {
            @Override
            public void onStartOpen(SwipeLayout layout)
            {
                isSwipe = true;
            }

            @Override
            public void onOpen(SwipeLayout layout)
            {

            }

            @Override
            public void onStartClose(SwipeLayout layout)
            {

            }

            @Override
            public void onClose(SwipeLayout layout)
            {
                isSwipe = false;
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset)
            {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel)
            {

            }
        };

        @Override
        public String toString()
        {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
