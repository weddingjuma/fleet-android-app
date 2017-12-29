package com.mapotempo.lib.map;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.UserPreferenceInterface;
import com.mapotempo.fleet.api.model.accessor.AccessInterface;
import com.mapotempo.fleet.api.model.submodel.LocationDetailsInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;

import java.util.ArrayList;
import java.util.List;

public class MapMissionsFragment extends Fragment {

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
                    setMissions(missions, false);
                }
            });
        }
    };

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.mapView);
        mMapView.setStyleUrl("https://maps.tilehosting.com/styles/basic/style.json?key=LLRxrAW8qh4LHSzTw6qo");
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

        boolean display_zoom = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager().getUserPreference().getBoolPreference(UserPreferenceInterface.Preference.MAP_DISPLAY_ZOOM_BUTTON);
        if (!display_zoom) {
            mZoomIn.setVisibility(View.GONE);
            mZoomOut.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        MapotempoFleetManagerInterface mapotempoFleetManagerInterface = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager();
        if (mapotempoFleetManagerInterface != null) {
            mapotempoFleetManagerInterface.getMissionAccess().addChangeListener(missionChangeListener);
            setMissions(mapotempoFleetManagerInterface.getMissionAccess().getAll(), true);
        }

        boolean display_position = ((MapotempoApplicationInterface) getContext().getApplicationContext()).getManager().getUserPreference().getBoolPreference(UserPreferenceInterface.Preference.MAP_CURRENT_POSITION);
        if (display_position) {
            setCurrentPosition(mapotempoFleetManagerInterface.getCurrentLocationDetails());
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

    private void setMissions(final List<MissionInterface> missions, final boolean updateLocation) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.clear();
                final String mission_id = getActivity().getIntent().getStringExtra("mission_id");
                MissionInterface missionFocus = null;

                // Draw marker
                IconFactory mIconFactory = IconFactory.getInstance(getActivity());
                Icon icon = mIconFactory.fromResource(R.drawable.ic_map_marker);
                for (MissionInterface mission : missions) {
                    mapboxMap.addMarker(new MarkerViewOptions()
                            .icon(icon)
                            .position(new LatLng(mission.getLocation().getLat(), mission.getLocation().getLon()))
                    );
                    if (mission.getId().equals(mission_id)) {
                        missionFocus = mission;
                    }
                }

                // Draw path
                List<LatLng> polygonPath = new ArrayList<>();
                for (MissionInterface mission : missions) {
                    polygonPath.add(new LatLng(mission.getLocation().getLat(), mission.getLocation().getLon()));
                }
                mapboxMap.addPolyline(new PolylineOptions()
                        .width(0.5F)
                        .addAll(polygonPath)
                        .color(Color.BLACK));

                // Set zoom and position
                if (updateLocation) {
                    final MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
                    LocationDetailsInterface locationDetailsInterface = mapotempoApplication.getManager().getCurrentLocationDetails();
                    int zoom = missionFocus != null ? 14 : 5;
                    LatLng latLng = (missionFocus != null ?
                            new LatLng(missionFocus.getLocation().getLat(), missionFocus.getLocation().getLon()) :
                            new LatLng(locationDetailsInterface.getLat(), locationDetailsInterface.getLon()));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)
                            .zoom(zoom)
                            .build();

                    mapboxMap.setCameraPosition(cameraPosition);
                }
            }
        });
    }

    private void setCurrentPosition(final LocationDetailsInterface locationDetailsInterface) {
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
