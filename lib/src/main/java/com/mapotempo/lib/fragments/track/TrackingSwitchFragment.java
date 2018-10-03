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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.dao.model.UserCurrentLocation;
import com.mapotempo.fleet.dao.model.UserSettings;
import com.mapotempo.fleet.dao.model.submodel.LocationDetails;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;
import com.mapotempo.lib.utils.Haversine;

import java.util.Date;

public class TrackingSwitchFragment extends MapotempoBaseFragment implements LocationListener
{

    static final int UPDATE_TIME = 5 * 60000; // 5 min
    static final int UPDATE_DISTANCE = 150;   // 150 meter

    private SwitchCompat mSwitchTracking;

    private MapotempoApplicationInterface mMapotempoApplicationInterface;

    private LocationManager mLocMngr;

    private Location mLastLocation;

    final static int REQUEST_ACCESS_FINE_LOCATION = 666;

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mSwitchTracking = new SwitchCompat(getContext());
        mSwitchTracking.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    if (mSwitchTracking.isChecked())
                    {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true)
                            explain();
                        else
                            askForPermission();
                    }
                }
                else
                {
                    boolean hooked = hookLocationListener(mSwitchTracking.isChecked());
                    mSwitchTracking.setChecked(hooked);
                    UserSettings up = mMapotempoApplicationInterface.getManager().getUserPreference();
                    up.setBoolPreference(UserSettings.Preference.TRACKING_ENABLE, mSwitchTracking.isChecked());
                    up.save();
                }
            }
        });
        return mSwitchTracking;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mMapotempoApplicationInterface = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        initLocationManager();

        boolean check = mMapotempoApplicationInterface.getManager().getUserPreference().getBoolPreference(UserSettings.Preference.TRACKING_ENABLE);
        mSwitchTracking.setChecked(check);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                explain();
            else
                askForPermission();
        }
        else
        {
            hookLocationListener(check);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ACCESS_FINE_LOCATION)
        {
            for (int i = 0; i < permissions.length; i++)
            {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                {
                    hookLocationListener(mSwitchTracking.isChecked());
                }
                else if (shouldShowRequestPermissionRationale(permissions[i]) == false)
                {
                    displayOptions();
                }
                else
                {
                    mSwitchTracking.setChecked(false);
                }
            }
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        hookLocationListener(false);
    }

    // ==================================
    // ==  LocationListener Interface  ==
    // ==================================

    @Override
    public void onLocationChanged(@NonNull Location location)
    {
        Log.d("onLocationChanged", "" + location.getLongitude() + location.getLatitude());

        if (mMapotempoApplicationInterface != null && mMapotempoApplicationInterface.getManager() != null)
        {
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

            try
            {
                if (mLastLocation == null || (mLastLocation != null && accuracyTester(location, mLastLocation)))
                {
                    mLastLocation = location;
                    LocationDetails locationDetails = new LocationDetails(mMapotempoApplicationInterface.getManager(),
                        location.getLatitude(),
                        location.getLongitude(),
                        new Date(),
                        (double) location.getAccuracy(),
                        (double) location.getSpeed(),
                        (double) location.getBearing()
                        , location.getAltitude(),
                        0,
                        cid,
                        lac,
                        mcc,
                        mnc);
                    UserCurrentLocation userCurrentLocation = mMapotempoApplicationInterface.getManager().getCurrentLocation();
                    userCurrentLocation.setLocation(locationDetails);
                    userCurrentLocation.save();
                }
            } catch (FleetException e)
            {
                // TODO CL2.0
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle)
    {
        Log.i("onStatusChanged", s);
    }

    @Override
    public void onProviderEnabled(String s)
    {
        Log.i("onProviderEnabled", s);
    }

    @Override
    public void onProviderDisabled(String s)
    {
        Log.i("onProviderDisabled", s);
    }


    // ===============
    // ==  Private  ==
    // ===============
    private void initLocationManager()
    {
        mLocMngr = (LocationManager) getActivity().getBaseContext().getSystemService(Context.LOCATION_SERVICE);

        if (!mLocMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !mLocMngr.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.enable_location_services);
            dialog.setMessage(R.string.location_is_disabled_long_text);
            dialog.setPositiveButton(R.string.connection_settings, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(R.string.close, new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                }
            });
            dialog.show();
        }
    }

    // NewLoc accuracy test
    private boolean accuracyTester(Location newLoc, Location oldLoc)
    {
        Double dist = Haversine.distance(newLoc.getLatitude(), newLoc.getLongitude(), oldLoc.getLatitude(), oldLoc.getLongitude());
        if (dist + oldLoc.getAccuracy() > newLoc.getAccuracy())
            return true;
        return false;
    }

    private boolean hookLocationListener(boolean hook_status)
    {
        boolean access_fine_location = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (hook_status && access_fine_location)
        {
            Log.d(getClass().getName(), "hooked the location listener");

            if (mLocMngr.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
                mLocMngr.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, UPDATE_TIME, UPDATE_DISTANCE, this);
            if (mLocMngr.isProviderEnabled(LocationManager.GPS_PROVIDER))
                mLocMngr.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_TIME, UPDATE_DISTANCE, this);
            if (mLocMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                mLocMngr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_TIME, UPDATE_DISTANCE, this);
            return true;
        }
        else
        {
            Log.d(getClass().getName(), "unhooked the location listener");
            mLocMngr.removeUpdates(this);
            return false;
        }
    }

    private void displayOptions()
    {
        Snackbar.make(getView(), com.mapotempo.lib.R.string.disabled_permission, Snackbar.LENGTH_LONG).setAction(com.mapotempo.lib.R.string.settings, new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                final Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }).show();
    }

    private void explain()
    {
        askForPermission();

        Snackbar.make(getView(), com.mapotempo.lib.R.string.explaination_location_permission, Snackbar.LENGTH_LONG).setAction(com.mapotempo.lib.R.string.Activate,
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    askForPermission();
                }
            }).show();
    }

    private void askForPermission()
    {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
    }
}
