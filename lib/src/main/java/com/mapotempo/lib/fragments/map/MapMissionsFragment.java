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
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.accessor.AccessInterface;
import com.mapotempo.fleet.api.model.submodel.LocationDetailsInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;

import java.util.ArrayList;
import java.util.List;

public class MapMissionsFragment extends MapotempoBaseFragment {

    private MapView mMapView;

    private static final String ZOOM_IN = "zoom_in";

    private static final String ZOOM_OUT = "zoom_out";

    private FloatingActionButton mZoomIn;

    private FloatingActionButton mZoomOut;

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

    private boolean mIsInitCameraPosition = false;

    private static String CAMERA_POSITION = "CAMERA_POSITION";

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsInitCameraPosition = savedInstanceState.getBoolean(CAMERA_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.mapView);
        mMapView.setStyleUrl(getString(R.string.tilehosting_base_url) + "/styles/basic/style.json?key=" + getString(R.string.tilehosting_access_token));
        mMapView.onCreate(savedInstanceState);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(MapboxMap mapboxMap) {
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        final String mission_id = getActivity().getIntent().getStringExtra("mission_id");
        MapotempoFleetManagerInterface mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
        if (mapotempoFleetManagerInterface != null) {
            mapotempoFleetManagerInterface.getMissionAccess().addChangeListener(missionChangeListener);
            setCurrentPosition(mapotempoFleetManagerInterface.getCurrentLocationDetails());
            setMissions(mapotempoFleetManagerInterface.getMissionAccess().getAll());
            if (!mIsInitCameraPosition) {
                setCameraPosition(mapotempoFleetManagerInterface.getMissionAccess().getAll(), mission_id);
                mIsInitCameraPosition = true;
            }
        } else {
            setCameraPosition(new ArrayList<MissionInterface>(), mission_id);
        }

        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        MapotempoFleetManagerInterface mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
        if (mapotempoFleetManagerInterface != null) {
            mapotempoFleetManagerInterface.getMissionAccess().removeChangeListener(missionChangeListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CAMERA_POSITION, mIsInitCameraPosition);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    // ===============
    // ==  Private  ==
    // ===============

    private void setMissions(final List<MissionInterface> missions) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.clear();

                // Draw marker
                IconFactory mIconFactory = IconFactory.getInstance(getActivity());
                Icon icon = mIconFactory.fromResource(R.drawable.ic_map_green_marker);
                for (MissionInterface mission : missions) {
                    if (mission.getLocation().isValide()) {
                        mapboxMap.addMarker(new MarkerViewOptions()
                                .icon(icon)
                                .position(new LatLng(mission.getLocation().getLat(), mission.getLocation().getLon()))
                        );
                    }
                }

                // Draw path
                List<LatLng> polygonPath = new ArrayList<>();
                for (MissionInterface mission : missions) {
                    if (mission.getLocation().isValide()) {
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

    private void setCurrentPosition(final LocationDetailsInterface locationDetailsInterface) {
        if (locationDetailsInterface.isValide()) {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    IconFactory mIconFactory = IconFactory.getInstance(getActivity());
                    Icon icon = mIconFactory.fromResource(R.drawable.ic_current_location);
                    mapboxMap.addMarker(new MarkerViewOptions()
                            .icon(icon)
                            .position(new LatLng(locationDetailsInterface.getLat(), locationDetailsInterface.getLon())));
                }
            });
        }
    }

    private void setCameraPosition(final List<MissionInterface> missions, @Nullable String mission_id) {
        // Set zoom and position
        final MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        LocationDetailsInterface locationDetailsInterface = mapotempoApplication.getManager().getCurrentLocationDetails();
        final _InternalLocationWithBnounds internalLocationWithBnounds = new _InternalLocationWithBnounds(locationDetailsInterface, missions, mission_id);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                if (internalLocationWithBnounds.isBounded)
                    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(internalLocationWithBnounds.getBoundedLocation(), 30));
                else
                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                                                  .target(internalLocationWithBnounds.getLocation())
                                                                  .zoom(7)
                                                                  .build());
            }
        });
    }

    private class _InternalLocationWithBnounds {

        private LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
        private LatLng mLocation = new LatLng();
        private int validMissionSize = 0;
        public boolean isBounded = true;

        public _InternalLocationWithBnounds(LocationDetailsInterface userLocation, List<MissionInterface> missions, String missionId) {
            for (MissionInterface mission : missions) {
                if (mission.getLocation().isValide()) {
                    double lat = mission.getLocation().getLat();
                    double lon = mission.getLocation().getLon();
                    validMissionSize++;

                    latLngBounds.include(new LatLng(lat, lon));

                    if (mission.getId().equals(missionId)) {
                        isBounded = false;
                        mLocation.setLatitude(lat);
                        mLocation.setLongitude(lon);
                        return;
                    }
                }
            }

            if (userLocation.isValide() && validMissionSize < 2) {
                isBounded = false;
                mLocation.setLongitude(userLocation.getLon());
                mLocation.setLatitude(userLocation.getLat());
                return;
            }

            isBounded = (validMissionSize > 2);
        }

        public LatLngBounds getBoundedLocation() {
            return latLngBounds.build();
        }

        public LatLng getLocation() {
            return mLocation;
        }
    }
}
