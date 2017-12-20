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
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;

import java.util.ArrayList;
import java.util.List;

public class MapMissionsFragment extends Fragment {

    private MapView mMapView;

    private FloatingActionButton mZoomIn;

    private FloatingActionButton mZoomOut;

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
                        double offset = 0.1;
                        if (v.getTag().equals("in"))
                            offset = -offset;
                        CameraPosition lastCameraPosition = mapboxMap.getCameraPosition();
                        CameraPosition cameraPosition = new CameraPosition.Builder(lastCameraPosition)
                                .zoom(lastCameraPosition.zoom + offset)
                                .build();
                        mapboxMap.setCameraPosition(cameraPosition);
                    }
                });
            }
        };

        mZoomIn = view.findViewById(R.id.zoom_in);
        mZoomIn.setTag("in");
        mZoomIn.setOnClickListener(onClickListener);
        mZoomOut = view.findViewById(R.id.zoom_out);
        mZoomOut.setTag("out");
        mZoomOut.setOnClickListener(onClickListener);

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
        final MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                final String mission_id = getActivity().getIntent().getStringExtra("mission_id");
                double latAverage = 0, lonAverage = 0;
                MissionInterface missionFocus = null;

                // Draw marker
                IconFactory mIconFactory = IconFactory.getInstance(getActivity());
                Icon icon = mIconFactory.fromResource(R.drawable.ic_map_marker);
                List<MissionInterface> missions = mapotempoApplication.getManager().getMissionAccess().getAll();
                for (MissionInterface mission : missions) {
                    latAverage += mission.getLocation().getLat();
                    lonAverage += mission.getLocation().getLon();
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

                // Set position
                int zoom = 12;
                LatLng latLng;
                if (missionFocus != null) {
                    zoom = 17;
                    latLng = new LatLng(missionFocus.getLocation().getLat(), missionFocus.getLocation().getLon());
                } else if (latAverage != 0 && lonAverage != 0) {
                    latAverage = latAverage / missions.size();
                    lonAverage = lonAverage / missions.size();
                    latLng = new LatLng(latAverage, lonAverage);
                } else {
                    zoom = 5;
                    latLng = new LatLng(43, 1);
                }

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(zoom)
                        .build();
                mapboxMap.setCameraPosition(cameraPosition);
            }
        });
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
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
}
