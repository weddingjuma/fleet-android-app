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

package com.mapotempo.lib.fragments.track;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.fleet.api.model.UserPreferenceInterface;
import com.mapotempo.fleet.api.model.submodel.LocationDetailsInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;

import java.util.Date;

public class TrackingSwitchFragment extends MapotempoBaseFragment implements LocationListener {

    static final int UPDATE_TIME = 5 * 60000;
    static final int UPDATE_DISTANCE = 150;

    private SwitchCompat mSwitchTracking;

    private MapotempoApplicationInterface mMapotempoApplicationInterface;

    private LocationManager mLocMngr;

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSwitchTracking = new SwitchCompat(getContext());
        mSwitchTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hooked = hookLocationListener(mSwitchTracking.isChecked());
                mSwitchTracking.setChecked(hooked);
                UserPreferenceInterface up = mMapotempoApplicationInterface.getManager().getUserPreference();
                up.setBoolPreference(UserPreferenceInterface.Preference.TRACKING_ENABLE, mSwitchTracking.isChecked());
                up.save();
            }
        });
        return mSwitchTracking;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLocationManager();

        mMapotempoApplicationInterface = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        boolean check = (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && mMapotempoApplicationInterface.getManager().getUserPreference().getBoolPreference(UserPreferenceInterface.Preference.TRACKING_ENABLE);
        mSwitchTracking.setChecked(check);
        hookLocationListener(check);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hookLocationListener(false);
    }

    // ==================================
    // ==  LocationListener Interface  ==
    // ==================================

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("onLocationChanged", "" + location.getLongitude() + location.getLatitude());

        if (mMapotempoApplicationInterface != null && mMapotempoApplicationInterface.getManager() != null) {
            Integer cid = -1;
            Integer lac = -1;
            Integer mcc = -1;
            Integer mnc = -1;

//            TelephonyManager telephonyManager = (TelephonyManager) (getActivity().getSystemService((Context.TELEPHONY_SERVICE)));
//             GsmCellLocation gl = (GsmCellLocation) telephonyManager.getCellLocation();
//            String networkOperator = telephonyManager.getNetworkOperator();

//            if (gl != null) {
//                cid = gl.getCid();
//                lac = gl.getLac();
//                //Log.i("loc info", "psc : " + cl.getPsc());
//            }
//            if (networkOperator != null) {
//                System.out.println(">>>>>>>>>>>" + networkOperator);
//                //mcc = Integer.parseInt(networkOperator.substring(0, 3));
//                //mnc = Integer.parseInt(networkOperator.substring(3));
//            }

            LocationDetailsInterface ld = mMapotempoApplicationInterface.getManager().getSubmodelFactory().CreateNewLocationDetails(
                    location.getLatitude(),
                    location.getLongitude(),
                    new Date(),
                    location.getAccuracy(),
                    location.getSpeed(),
                    location.getBearing()
                    , location.getAltitude(),
                    0,
                    cid,
                    lac,
                    mcc,
                    mnc);

            mMapotempoApplicationInterface.getManager().setCurrentLocationDetails(ld);

            Log.d(getClass().getName(), "---------------------UP LOCATION---------------");
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i("onStatusChanged", s);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i("onProviderEnabled", s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i("onProviderDisabled", s);
    }


    // ===============
    // ==  Private  ==
    // ===============
    private void initLocationManager() {
        mLocMngr = (LocationManager) getActivity().getBaseContext().getSystemService(Context.LOCATION_SERVICE);
    }

    private boolean hookLocationListener(boolean hook_status) {
        boolean access_fine_location = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (hook_status && access_fine_location) {
            Log.d(getClass().getName(), "hooked the location listener");

            if (mLocMngr.isProviderEnabled(LocationManager.GPS_PROVIDER))
                mLocMngr.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_TIME, UPDATE_DISTANCE, this);
            if (mLocMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                mLocMngr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_TIME, UPDATE_DISTANCE, this);
            return true;
        } else {
            Log.d(getClass().getName(), "unhooked the location listener");
            mLocMngr.removeUpdates(this);
            return false;
        }

    }
}
