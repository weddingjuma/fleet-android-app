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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapotempo.fleet.dao.model.Route;
import com.mapotempo.lib.R;
import com.mapotempo.lib.utils.DateHelpers;

import java.util.List;

class RoutesRecyclerViewAdapter extends RecyclerView.Adapter<RoutesRecyclerViewAdapter.ViewHolder>
{
    private int routesCount = 0;

    private List<Route> mRoutes;

    private int mRouteFocus = 0;

    private OnRouteSelectedListener mOnRouteSelectedListener;

    // ===================
    // ==  Constructor  ==
    // ===================

    public RoutesRecyclerViewAdapter(Context context, List<Route> routes, OnRouteSelectedListener onRouteSelectedListener)
    {
        mRoutes = routes;
        routesCount = routes.size();
        mOnRouteSelectedListener = onRouteSelectedListener;
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

    public void setRoutes(List<Route> routes)
    {
        mRoutes = routes;
        routesCount = routes.size();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        Route mRoute;
        final View mView;
        final TextView mName;
        final TextView mDate;
        final TextView mHour;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.name);
            mDate = view.findViewById(R.id.date);
            mHour = view.findViewById(R.id.hour);
        }

        void setRoute(Route route)
        {
            String missionDate = DateHelpers.parse(route.getDate(), DateHelpers.DateStyle.SHORTDATE);
            String missionHour = DateHelpers.parse(route.getDate(), DateHelpers.DateStyle.HOURMINUTES);
            mName.setText(route.getName());
            mDate.setText(missionDate);
            mHour.setText(missionHour);
            mRoute = route;
            mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnRouteSelectedListener.onRouteSelected(mRoute);
                }
            });
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
