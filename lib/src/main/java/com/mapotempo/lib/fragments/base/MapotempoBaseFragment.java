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

package com.mapotempo.lib.fragments.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;

import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.exception.MapotempoBaseFragmentRuntimeException;
import com.mapotempo.lib.exception.MapotempoManagerMissingException;

/**
 * Mapotempo database fragment to handle MapotempoFleetManagerInterface missing.
 * This fragment ensure that during onAttach the MapotempoApplicationInterface can return a MapotempoFleetManagerInterface.
 * If MapotempoFleetManagerInterface is null throw a MapotempoManagerMissingException.
 * Activity can try catch on inflate this exception.
 */
public abstract class MapotempoBaseFragment extends Fragment {

    public static boolean locationHasBeenAsked = false;

    // ====================================
    // ==  Android Fragments Life cycle  ==
    // ====================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init location if Gps is off
        if (!locationHasBeenAsked) initLocation();
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context.getApplicationContext() instanceof MapotempoApplicationInterface) {
            if (((MapotempoApplicationInterface) context.getApplicationContext()).getManager() == null) {
                throw new MapotempoManagerMissingException(context.toString() + "getManager() of MapotempoApplicationInterface return null value");
            }
        } else {
            throw new MapotempoBaseFragmentRuntimeException(context.toString() + "Application must implement MapotempoApplicationInterface");
        }
    }

    // =============================
    // ==  Private Init Location  ==
    // =============================

    private void initLocation() {
        LocationManager locMngr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !locMngr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.enable_location_services);
            dialog.setMessage(R.string.location_is_disabled_long_text);
            dialog.setPositiveButton(R.string.connection_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            });
            dialog.show();
            locationHasBeenAsked = true;
        }
    }
}
