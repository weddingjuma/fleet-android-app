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

package com.mapotempo.lib.fragments.map;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapotempo.fleet.core.accessor.LiveAccessChangeListener;
import com.mapotempo.fleet.core.accessor.LiveAccessToken;
import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.fleet.dao.model.Route;
import com.mapotempo.fleet.dao.model.nested.LocationDetails;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MapMissionsFragment extends MapotempoBaseFragment
{

    private MapView mMapView;

    private static final String ZOOM_IN = "zoom_in";

    private static final String ZOOM_OUT = "zoom_out";

    private FloatingActionButton mZoomIn;

    private FloatingActionButton mZoomOut;

    private LiveAccessToken mLiveAccessToken;

    private boolean mIsInitCameraPosition = false;

    private static String CAMERA_POSITION = "CAMERA_POSITION";

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            mIsInitCameraPosition = savedInstanceState.getBoolean(CAMERA_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.mapView);
        mMapView.setStyleUrl(getString(R.string.tilehosting_base_url) + "/styles/basic/style.json?key=" + getString(R.string.tilehosting_access_token));
        mMapView.onCreate(savedInstanceState);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                mMapView.getMapAsync(new OnMapReadyCallback()
                {
                    @Override
                    public void onMapReady(MapboxMap mapboxMap)
                    {
                        double offset = 1;
                        if (v.getTag().equals(ZOOM_IN))
                            offset = -offset;
                        CameraPosition lastCameraPosition = mapboxMap.getCameraPosition();
                        CameraPosition cameraPosition = new CameraPosition.Builder(lastCameraPosition)
                            .zoom(lastCameraPosition.zoom + offset)
                            .build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        mapboxMap.animateCamera(cameraUpdate);
                    }
                });
            }
        };

        mZoomIn = view.findViewById(R.id.zoom_in);
        mZoomOut = view.findViewById(R.id.zoom_out);
        mZoomIn.setTag(ZOOM_IN);
        mZoomOut.setTag(ZOOM_OUT);
        mZoomIn.setOnClickListener(onClickListener);
        mZoomOut.setOnClickListener(onClickListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(calendar.HOUR, -12);
        final String route_id = getActivity().getIntent().getStringExtra("route_id");
        final String mission_id = getActivity().getIntent().getStringExtra("mission_id");

        MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();

        if (mapotempoFleetManagerInterface != null)
        {
            Route route = null;
            List<Mission> missions = new LinkedList<>();
            if (route_id != null)
            {
                route = mapotempoFleetManagerInterface.getRouteAccess().get(route_id);
            }

            if (route == null)
            {
                mLiveAccessToken = mapotempoFleetManagerInterface.getMissionAccess().byDateGreaterThan_AddListener(new LiveAccessChangeListener<Mission>()
                                                                                                                   {
                                                                                                                       @Override
                                                                                                                       public void changed(final List<Mission> missions)
                                                                                                                       {
                                                                                                                           getActivity().runOnUiThread(new Runnable()
                                                                                                                           {
                                                                                                                               @Override
                                                                                                                               public void run()
                                                                                                                               {
                                                                                                                                   setMissions(missions);
                                                                                                                               }
                                                                                                                           });
                                                                                                                       }
                                                                                                                   }, calendar.getTime()
                );
                setCurrentPosition(mapotempoFleetManagerInterface.getCurrentLocation().getLocation());
                missions = mapotempoFleetManagerInterface.getMissionAccess().byDateGreaterThan(calendar.getTime());
            }
            else
            {
                mLiveAccessToken = mapotempoFleetManagerInterface.getMissionAccess().byRoute_AddListener(new LiveAccessChangeListener<Mission>()
                                                                                                         {
                                                                                                             @Override
                                                                                                             public void changed(final List<Mission> missions)
                                                                                                             {
                                                                                                                 getActivity().runOnUiThread(new Runnable()
                                                                                                                 {
                                                                                                                     @Override
                                                                                                                     public void run()
                                                                                                                     {
                                                                                                                         setMissions(missions);
                                                                                                                     }
                                                                                                                 });
                                                                                                             }
                                                                                                         }, route
                );
                setCurrentPosition(mapotempoFleetManagerInterface.getCurrentLocation().getLocation());
                missions = mapotempoFleetManagerInterface.getMissionAccess().byRoute(route);
            }
            setMissions(missions);
            if (!mIsInitCameraPosition)
            {
                setCameraPosition(missions, mission_id);
                mIsInitCameraPosition = true;
            }
        }
        else
        {
            setCameraPosition(new ArrayList<Mission>(), mission_id);
        }

        mMapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mMapView.onPause();
        MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
        if (mapotempoFleetManagerInterface != null)
        {
            mapotempoFleetManagerInterface.getMissionAccess().removeListener(mLiveAccessToken);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CAMERA_POSITION, mIsInitCameraPosition);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mMapView.onDestroy();
    }
    // ===============
    // ==  Private  ==
    // ===============

    private void setMissions(final List<Mission> missions)
    {
        mMapView.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(MapboxMap mapboxMap)
            {
                mapboxMap.clear();

                // Draw markers
                IconFactory mIconFactory = IconFactory.getInstance(getActivity());
                Icon icon;
                LatLng location;

                for (Mission mission : missions)
                {
                    if (mission.getSurveyLocation().isValid())
                    {
                        icon = mIconFactory.fromResource(R.drawable.ic_map_blue_marker);
                        location = new LatLng(
                            mission.getSurveyLocation().getLat(),
                            mission.getSurveyLocation().getLon()
                        );
                    }
                    else if (mission.getLocation().isValid())
                    {
                        icon = mIconFactory.fromResource(R.drawable.ic_map_green_marker);
                        location = new LatLng(
                            mission.getLocation().getLat(),
                            mission.getLocation().getLon()
                        );
                    }
                    else
                    {
                        continue;
                    }

                    mapboxMap.addMarker(new MarkerViewOptions()
                        .position(location)
                        .icon(icon));
                }

                // Draw path
                List<LatLng> polygonPath = new ArrayList<>();
                for (Mission mission : missions)
                {
                    if (mission.getLocation().isValid())
                    {
                        polygonPath.add(new LatLng(mission.getLocation().getLat(), mission.getLocation().getLon()));
                    }
                }
                mapboxMap.addPolyline(new PolylineOptions()
                    .width(0.5F)
                    .addAll(polygonPath)
                    .color(Color.BLACK));
            }
        });
    }

    private void setCurrentPosition(final LocationDetails locationDetails)
    {
        if (locationDetails.isValid())
        {
            mMapView.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(MapboxMap mapboxMap)
                {
                    IconFactory mIconFactory = IconFactory.getInstance(getActivity());
                    Icon icon = mIconFactory.fromResource(R.drawable.ic_current_location);
                    mapboxMap.addMarker(new MarkerViewOptions()
                        .icon(icon)
                        .position(new LatLng(locationDetails.getLat(), locationDetails.getLon())));
                }
            });
        }
    }

    private void setCameraPosition(final List<Mission> missions, @Nullable String mission_id)
    {
        // Set zoom and position
        final MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        LocationDetails locationDetailsInterface = mapotempoApplication.getManager().getCurrentLocation().getLocation();
        final _InternalLocationWithBnounds internalLocationWithBnounds = new _InternalLocationWithBnounds(locationDetailsInterface, missions, mission_id);

        mMapView.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(MapboxMap mapboxMap)
            {
                if (internalLocationWithBnounds.isBounded)
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(internalLocationWithBnounds.getBoundedLocation(), 30));
                else
                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(internalLocationWithBnounds.getLocation())
                        .zoom(17)
                        .build());
            }
        });
    }

    private class _InternalLocationWithBnounds
    {

        private LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
        private LatLng mLocation = new LatLng();
        private int validMissionSize = 0;
        public boolean isBounded = true;

        public _InternalLocationWithBnounds(LocationDetails userLocation, List<Mission> missions, String missionId)
        {
            if (userLocation.isValid())
            {
                mLocation.setLongitude(userLocation.getLon());
                mLocation.setLatitude(userLocation.getLat());
            }

            for (Mission mission : missions)
            {
                if (mission.getLocation().isValid())
                {
                    double lat = mission.getLocation().getLat();
                    double lon = mission.getLocation().getLon();
                    validMissionSize++;

                    latLngBounds.include(new LatLng(lat, lon));

                    // Check if mission matches
                    if (!mission.getId().equals(missionId))
                        continue;

                    if (mission.getSurveyLocation().isValid())
                    {
                        double surveyLat = mission.getSurveyLocation().getLat();
                        double surveyLon = mission.getSurveyLocation().getLon();

                        mLocation.setLatitude(surveyLat);
                        mLocation.setLongitude(surveyLon);
                    }
                    else
                    {
                        mLocation.setLatitude(lat);
                        mLocation.setLongitude(lon);
                    }

                    isBounded = false;
                    return;
                }
            }

            isBounded = (validMissionSize > 2);
        }

        public LatLngBounds getBoundedLocation()
        {
            return latLngBounds.build();
        }

        public LatLng getLocation()
        {
            return mLocation;
        }
    }
}
