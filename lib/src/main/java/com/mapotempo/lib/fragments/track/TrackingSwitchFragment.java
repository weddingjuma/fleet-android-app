package com.mapotempo.lib.fragments.track;

import android.Manifest;
import android.content.Context;
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
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.fleet.api.model.UserPreferenceInterface;
import com.mapotempo.fleet.api.model.submodel.LocationDetailsInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;

import java.util.Date;

public class TrackingSwitchFragment extends Fragment implements LocationListener {

    static final int UPDATE_TIME = 5 * 60000;
    static final int UPDATE_DISTANCE = 150;

    private SwitchCompat mSwitchTracking;

    private MapotempoApplicationInterface mMapotempoApplicationInterface;

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSwitchTracking = new SwitchCompat(getContext());
        mSwitchTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (mSwitchTracking.isChecked()) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
                            explain();
                        } else {
                            askForPermission();
                        }
                    }
                } else {
                    hookLocationListener(mSwitchTracking.isChecked());
                }
                UserPreferenceInterface up = mMapotempoApplicationInterface.getManager().getUserPreference();
                up.setBoolPreference(UserPreferenceInterface.Preference.TRACKING_ENABLE, mSwitchTracking.isChecked());
                up.save();
            }
        });
        return mSwitchTracking;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 666) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hookLocationListener(true);
            } else if (shouldShowRequestPermissionRationale(permissions[0]) == false) {
                displayOptions();
            } else {
                mSwitchTracking.setChecked(false);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    private void hookLocationListener(boolean hook_status) {
        LocationManager locMngr = (LocationManager) getActivity().getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        boolean test = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (hook_status && test) {
            Log.d(getClass().getName(), "hooked the location listener");
            if (locMngr.isProviderEnabled(LocationManager.GPS_PROVIDER))
                locMngr.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_TIME, UPDATE_DISTANCE, this);
            if (locMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                locMngr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_TIME, UPDATE_DISTANCE, this);
        } else {
            Log.d(getClass().getName(), "unhooked the location listener");
            locMngr.removeUpdates(this);
        }
    }

    private void displayOptions() {
        Snackbar.make(getView(), R.string.disabled_permission, Snackbar.LENGTH_LONG).setAction(R.string.settings, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                final Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }).show();
    }

    private void explain() {
        askForPermission();

        Snackbar.make(getView(), R.string.explaination_location_permission, Snackbar.LENGTH_LONG).setAction("Activer", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPermission();
            }
        }).show();
    }

    private void askForPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 666);
    }
}
