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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.settings.SettingsHelper;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OfflineMapManager {

    // FOR OFFLINE MAPBOX MANAGER

    private static final String JSON_CHARSET = "UTF-8";

    private static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

    private static final String TAG = "OFFLINE_MANAGER";

    private static final int MEGABYTE_FACTOR = 1_048_576;

    // FOR LOCAL STORAGE ON DEVICE

    private static final String SHARED_FILE_NAME = "mapbox_cached_data";

    private static final String IS_CAHED = "has_cached_data";

    private static final String CACHED_DATE = "cached_date";

    // FOR DATA INNER CLASS

    private Context mContext;

    private OfflineManager mOfflineManager;

    private Toast updateInfoToast;

    private IMapLoading mListener;

    public OfflineMapManager(Context context) {
        setContext(context);
    }

    //=============================
    //   CREATE OFFLINE REGIONS
    //=============================
    private OfflineManager createOfflineRegion(LatLngBounds zone, String styleUrl) {
        mOfflineManager = OfflineManager.getInstance(mContext);
        updateInfoToast = Toast.makeText(mContext.getApplicationContext(), "", Toast.LENGTH_LONG);

        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                styleUrl,
                zone,
                0,
                5,
                mContext.getResources().getDisplayMetrics().density);

        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, "Cached Region");
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }

        mOfflineManager.createOfflineRegion(
            definition,
            metadata,
            new OfflineManager.CreateOfflineRegionCallback() {
                @Override
                public void onCreate(OfflineRegion offlineRegion) {
                    offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                    // Monitor the download progress using setObserver
                    offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                        @Override
                        public void onStatusChanged(OfflineRegionStatus status) {

                            if (status.isComplete()) {
                                updateInfoToast.setText(mContext.getString(R.string.loading_completed));
                                updateInfoToast.setDuration(Toast.LENGTH_LONG);

                                if (!updateInfoToast.getView().isShown())
                                    updateInfoToast.show();

                                mContext.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_PRIVATE)
                                        .edit()
                                        .putBoolean(IS_CAHED, true)
                                        .apply();

                                mContext.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_PRIVATE)
                                        .edit()
                                        .putLong(CACHED_DATE, (new Date()).getTime())
                                        .apply();

                                // CallBack
                                if (mListener != null) { mListener.OnDownloadingCompleted(); }

                            } else if (status.isRequiredResourceCountPrecise()) {
                                String completedInfo = status.getCompletedResourceCount() + " / " + status.getRequiredResourceCount();
                                updateInfoToast.setText("Chargement: " + completedInfo);

                                if (!updateInfoToast.getView().isShown())
                                    updateInfoToast.show();

//                                status.getRequiredResourceCount();
                            }
                        }

                        @Override
                        public void onError(OfflineRegionError error) {
                            // If an error occurs, print to logcat
                            Log.e(TAG, "onError reason: " + error.getReason());
                            Log.e(TAG, "onError message: " + error.getMessage());
                            if (mListener != null) mListener.OnDownloadingFailed(error.getMessage());
                        }

                        @Override
                        public void mapboxTileCountLimitExceeded(long limit) {
                            // Notify if offline region exceeds maximum tile count
                            Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error: " + error);
                }
            });
            return mOfflineManager;
    }


    private String getStyle() {
        return  mContext.getString(R.string.tilehosting_base_url) +
                mContext.getString(R.string.tilehosting_style_base) +
                mContext.getString(R.string.tilehosting_access_token);
    }

    //============================
    //       Init world map
    //============================
    public void initWorldMapCache(IMapLoading listener) {
        if (listener != null) {
            mListener = listener;
        }
        createOfflineRegion(LatLngBounds.world(), getStyle());
    }

    //============================
    // Init with Custom location
    //============================
    public void initCachForZone(@Nonnull LatLngBounds zone, @Nullable IMapLoading listener) {
        if (listener != null) {
            mListener = listener;
        }
        createOfflineRegion(zone, getStyle());
    }

    //=============================
    //  DELETE ALL REGIONS CACHED
    //=============================
    // When no Errors has been triggered, it should stay no caches
    public void deleteOfflineCacheRegions() {
        OfflineManager.getInstance(mContext).listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(OfflineRegion[] offlineRegions) {
                if (offlineRegions.length <= 0) {
                    Toast.makeText(mContext, mContext.getString(R.string.no_offline_maps), Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                for (final OfflineRegion region : offlineRegions) {
                    region.delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                        @Override
                        public void onDelete() {
                            Log.e("CACHE SUCCESS", ">>>>>>> Region deleted: " + region.getID());
                            Toast.makeText(mContext, mContext.getString(R.string.map_deleted), Toast.LENGTH_LONG)
                                 .show();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("CACHE ERROR", error);
                            Toast.makeText(mContext, mContext.getString(R.string.delete_offline_map_error), Toast.LENGTH_LONG)
                                 .show();
                        }
                    });
                }

                // RUN IN MEMORY SHARED
                mContext.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(IS_CAHED, false)
                        .apply();
            }

            @Override
            public void onError(String error) {
                Log.e("tag", error);
            }
        });
    }

    //==========================
    //    Re-new the context
    //==========================
    public void setContext(Context context) {
        mContext = context;
    }

    //=======================================
    // True if cached data, false otherwise
    //=======================================
    public boolean asCachedData() {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(IS_CAHED, false);
    }

    public void alertBuilderForMapDownloading(@Nonnull Context context,
                                              @Nonnull AlertDialog.OnClickListener onOk,
                                              @Nonnull AlertDialog.OnClickListener onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.mapbox_AlertDialogStyle);

        boolean allowPreload = SettingsHelper.allowPreloadUse(mContext);
        boolean connectionAvailable = SettingsHelper.connectionAvailableForDownload(mContext);

        if (allowPreload && connectionAvailable) {
            builder.setTitle(R.string.preload_modal_title)
                   .setMessage(R.string.preload_modal_desc)
                   .setPositiveButton(R.string.preload_modal_ok, onOk)
                   .setNegativeButton(R.string.preload_modal_cancel, onCancel);
        } else {
            builder.setTitle(R.string.preload_modal_title_no_connection)
                   .setMessage(R.string.preload_modal_desc_no_connection)
                   .setNegativeButton(R.string.preload_modal_ok, onCancel);
        }
        builder.create()
               .show();
    }

    // Return the size of the current Offline db size. (doesn't ensure all tiles has been downloaded yet)
    public long getOfflineDbSize() {
        String path = mContext.getFilesDir().toString() + File.separator + "mbgl-offline.db";
        File file = new File(path);
        if (file.exists()) {
            long size = file.length() / MEGABYTE_FACTOR;
            return size;
        }
        return 0L;
    }

    public Date getLastCachedDate() {
        long milliseconds = mContext.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_PRIVATE)
                .getLong(CACHED_DATE, 0L);

        if (milliseconds == 0L) {
            return null;
        }

        return new Date(milliseconds);
    }

    public interface IMapLoading {
        void OnDownloadingCompleted();
        void OnDownloadingFailed(String message);
    }
}
