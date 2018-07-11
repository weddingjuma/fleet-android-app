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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.fleet.core.accessor.LiveAccessToken;
import com.mapotempo.fleet.dao.model.Route;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;

import java.util.LinkedList;
import java.util.List;

public class RoutesListFragment extends MapotempoBaseFragment
{
    private RecyclerView mRecyclerView;

    private RoutesRecyclerViewAdapter mRecyclerAdapter;

    private LiveAccessToken mMissionChangeListenerToken;

    private OnRouteSelectedListener mListener;

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnRouteSelectedListener)
        {
            mListener = (OnRouteSelectedListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement " + OnRouteSelectedListener.class.getName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme);
        if (false) // FIXME when theme switch will be implemented
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme_Night);

        View view = inflater.cloneInContext(contextThemeWrapper).inflate(R.layout.fragment_routes_list, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerAdapter = new RoutesRecyclerViewAdapter(getContext(), new LinkedList<Route>(), mListener);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        return view;
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void setRoutes(@NonNull List<Route> routes)
    {
        mRecyclerAdapter.setRoutes(routes);
    }
}
