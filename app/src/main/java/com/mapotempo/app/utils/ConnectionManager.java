package com.mapotempo.app.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.mapotempo.app.R;

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

        setButtonEvents(view, alert);
        alert.setView(view);
        alert.show();
    }

    public void setButtonEvents(View buttonEventsView, final AlertDialog alertDialog) {
        Button wifi = buttonEventsView.findViewById(R.id.only_wifi);
        Button both = buttonEventsView.findViewById(R.id.both_wifi_and_mobile);

        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSelectedDataProviderWifi(connectionType, mContext);
                alertDialog.dismiss();
            }
        });

        both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSelectedDataProviderBoth(connectionType, mContext);
                alertDialog.dismiss();
            }
        });
    }

    private void setUserPreference(boolean value) {
        allowUsingMobileData = value;
    }

    /**
     * Singleton class that hold user preferences about data pull method.
     * call {@link ConnectionManager#setUserPreference(boolean)} to set them up.
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
