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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;

public class MapLocationPickerFragment extends MapotempoBaseFragment
{

    private MapView mMapView;

    private static final String ZOOM_IN = "zoom_in";

    private static final String ZOOM_OUT = "zoom_out";

    private static LatLng mScrollLocation;

    private Mission mMission;

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final String mission_id = getActivity().getIntent().getStringExtra("mission_id");
        MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
        mMission = mapotempoFleetManagerInterface.getMissionAccess().get(mission_id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
        container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.mapView);
        mMapView.setStyleUrl(getString(R.string.tilehosting_base_url) + "/styles/basic/style.json?key=" + getString(R.string.tilehosting_access_token));
        mMapView.onCreate(savedInstanceState);

        ImageView crossPicker = view.findViewById(R.id.cross_picker);
        crossPicker.setVisibility(View.VISIBLE);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                mMapView.getMapAsync(new OnMapReadyCallback()
                {
                    @Override
                    public void onMapReady(final MapboxMap mapboxMap)
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

        FloatingActionButton mZoomIn = view.findViewById(R.id.zoom_in);
        FloatingActionButton mZoomOut = view.findViewById(R.id.zoom_out);
        mZoomIn.setTag(ZOOM_IN);
        mZoomOut.setTag(ZOOM_OUT);
        mZoomIn.setOnClickListener(onClickListener);
        mZoomOut.setOnClickListener(onClickListener);

        displayLocationMarkers();
        initializeMapLocation();

        return view;
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
        mMapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mMapView.onPause();
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

    // ==============
    // ==  Public  ==
    // ==============

    public void savePickedLocation()
    {
        if (mScrollLocation != null)
        {
            MapotempoFleetManager mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getActivity().getApplicationContext()).getManager();
            com.mapotempo.fleet.dao.model.nested.Location location = new com.mapotempo.fleet.dao.model.nested.Location(mapotempoFleetManagerInterface, mScrollLocation.getLatitude(), mScrollLocation.getLongitude());
            mMission.setSurveyLocation(location);
            mMission.save();
        }
    }

    public void deletePickedLocation()
    {
        mMission.clearSurveyLocation();
        mMission.save();
        displayLocationMarkers();
    }

    // ===============
    // ==  Private  ==
    // ===============

    private Location getNativeLocation()
    {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationManager mLocMngr = (LocationManager) getActivity().getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            if (mLocMngr.isProviderEnabled(LocationManager.GPS_PROVIDER))
                return mLocMngr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            else if (mLocMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                return mLocMngr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return null;
    }

    private void initializeMapLocation()
    {
        mMapView.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(final MapboxMap mapboxMap)
            {
                com.mapotempo.fleet.dao.model.nested.Location locationInterface;
                LatLng latLng = new LatLng(0, 0);

                if (getNativeLocation() != null)
                {
                    latLng.setLatitude(getNativeLocation().getLatitude());
                    latLng.setLongitude(getNativeLocation().getLongitude());
                }
                else if (mMission != null)
                {
                    locationInterface = (mMission.getSurveyLocation().isValid()) ? mMission.getSurveyLocation() :
                        mMission.getLocation();
                    if (locationInterface.isValid())
                    {
                        latLng.setLatitude(locationInterface.getLat());
                        latLng.setLongitude(locationInterface.getLon());
                    }
                }

                CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(14)
                    .build();

                mapboxMap.setCameraPosition(cameraPosition);

                mScrollLocation = latLng;

                mapboxMap.setOnScrollListener(new MapboxMap.OnScrollListener()
                {
                    @Override
                    public void onScroll()
                    {
                        mScrollLocation = mapboxMap
                            .getProjection()
                            .fromScreenLocation(new PointF(mMapView.getWidth() / 2,
                                mMapView.getHeight() / 2));
                    }
                });
            }
        });
    }

    private void displayLocationMarkers()
    {
        if (mMission != null)
        {
            mMapView.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(final MapboxMap mapboxMap)
                {
                    mapboxMap.clear();
                    IconFactory mIconFactory = IconFactory.getInstance(getActivity());
                    int iconId = R.drawable.ic_map_green_marker;
                    LatLng position = null;

                    if (mMission.getLocation().isValid())
                        position = new LatLng(mMission.getLocation().getLat(), mMission.getLocation().getLon());

                    if (mMission.getSurveyLocation().isValid())
                    {
                        position = new LatLng(mMission.getSurveyLocation().getLat(), mMission.getSurveyLocation().getLon());
                        iconId = R.drawable.ic_map_blue_marker;
                    }

                    if (position != null)
                    {
                        mapboxMap.addMarker(new MarkerViewOptions()
                            .icon(mIconFactory.fromResource(iconId))
                            .position(position)
                        );
                    }
                }
            });
        }
    }
}
