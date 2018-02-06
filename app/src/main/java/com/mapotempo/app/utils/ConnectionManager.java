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

package com.mapotempo.app.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

public class ConnectionManager {

    private static ConnectionManager instance = null;
    private ConnectionType connectionType = ConnectionType.NONE;
    private boolean allowUsingMobileData;
    private OnConnectionSetup mListener;
    private Context mContext;

    public enum ConnectionType {
        NONE,
        WIFI,
        BOTH
    }

    private ConnectionManager() {

    }
//
//    public boolean isActiveWifi(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        final NetworkInfo networkTypeMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//
//        return networkTypeMobile.isConnectedOrConnecting();
//    }
//
//    public boolean isActiveMobile(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        final NetworkInfo networkTypeWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//        return networkTypeWifi.isConnectedOrConnecting();
//    }
//
//    /**
//     * Call this method only if user already took a decision concerning data (Return NONE otherwise)
//     * Call {@link ConnectionManager#askUserPreference(Context, int)} to set data type
//     * @return void
//     */
//    public ConnectionType getConnectionType() {
//        return connectionType;
//    }

    /**
     * Call this method to ask user if he wants to use mobile data or only wifi.
     *
     * @param context
     * @param viewToInflate the View.xml to inflate must contain ([Button] id: only_wifi, [Button] id: both_wifi_and_mobile
     */
    public void askUserPreference(Context context, int viewToInflate) throws NullPointerException {
        if (connectionType != ConnectionType.NONE)
            return;

        if (context instanceof OnConnectionSetup) {
            mListener = (OnConnectionSetup) context;
        } else {
            throw new NullPointerException("OnConnectionSetup must be implemented");
        }

        mContext = context;

        Activity currentActivity = (Activity) context;
        View view = currentActivity.getLayoutInflater().inflate(viewToInflate, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alert = builder.create();

        alert.setView(view);
        alert.show();
    }

    private void setUserPreference(boolean value) {
        allowUsingMobileData = value;
    }

    /**
     * Singleton class that hold user preferences about data pull method.
     * call {@link ConnectionManager#setUserPreference(boolean)} to set them up.
     *
     * @return
     */
    public static ConnectionManager getInstance() {
        if (instance == null)
            instance = new ConnectionManager();
        return instance;
    }

    public interface OnConnectionSetup {
        void onSelectedDataProviderWifi(ConnectionManager.ConnectionType connectionType, Context context);

        void onSelectedDataProviderBoth(ConnectionManager.ConnectionType connectionType, Context context);
    }
}
