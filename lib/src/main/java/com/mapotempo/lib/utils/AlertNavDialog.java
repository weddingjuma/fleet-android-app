package com.mapotempo.lib.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.mapotempo.lib.R;

import java.util.HashMap;
import java.util.List;
import java.util.MissingResourceException;

public final class AlertNavDialog implements View.OnClickListener {

    private Builder builder;
    private AlertDialog.Builder builderDialog;
    private AlertDialog dialog;

    public final String MAPS_PACKAGE = "com.google.android.apps.maps";
    public final String WAZE_PACKAGE = "com.waze";

    private HashMap<Integer, String> ids = new HashMap<>();

    private AlertNavDialog(Builder builder) {
        this.builder = builder;
        builderDialog = new AlertDialog.Builder(builder.getContext());
        builderDialog.setView(builder.getView());

        LinearLayout layout = builder.getView().findViewById(R.id.navs_list_layout);

        for (ResolveInfo info : builder.getActivities()) {
            ImageButton img = LayoutInflater.from(builder.getContext())
                                            .inflate(R.layout.nav_image_button, null)
                                            .findViewWithTag("nav_button");
            img.setImageDrawable(info.loadIcon(builder.getPackageManager()));

            int id = View.generateViewId();
            ids.put(id, info.activityInfo.packageName);

            img.setId(id);
            img.setOnClickListener(this);
            layout.addView(img);
        }
    }

    @Override
    public void onClick(View v) {
        String packageName = ids.get(v.getId());
        Uri uri;
        switch (packageName) {
            case MAPS_PACKAGE:
                uri = Uri.parse("geo:" + builder.lat + "," + builder.lng + "?q=" + builder.lat + "," + builder.lng);
                break;
            case WAZE_PACKAGE:
                uri = Uri.parse("https://waze.com/ul?ll=" + builder.lat + "," + builder.lng + "&navigate=yes");
                break;
            default:
                uri = Uri.parse("geo:" + builder.lat + "," + builder.lng);
                break;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage(packageName);

        builder.callback.onSelected(intent);
        dialog.dismiss();
    }

    public void show() {
        dialog = builderDialog.show();
    }

    public static final class Builder {

        private View view;
        private Context context;
        private List<ResolveInfo> activities;
        private PackageManager packageManager;
        private OnMapsAppSelected callback;
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }

        public OnMapsAppSelected getOnClick() {
            return callback;
        }

        public View getView() {
            return view;
        }

        public Context getContext() {
            return context;
        }

        public List<ResolveInfo> getActivities() {
            return activities;
        }

        public PackageManager getPackageManager() {
            return packageManager;
        }

        public Builder setOnClick(OnMapsAppSelected onClick) {
            callback = onClick;
            return this;
        }

        public Builder setActivities(List<ResolveInfo> activities) {
            this.activities = activities;
            return this;
        }

        public Builder setPackageManager(PackageManager packageManager) {
            this.packageManager = packageManager;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setView(int id) {
            view = LayoutInflater.from(getContext()).inflate(id, null);
            return this;
        }

        public Builder setLat(double lat) {
            this.lat = lat;
            return this;
        }

        public Builder setLng(double lng) {
            this.lng = lng;
            return this;
        }

        public Builder(Context context) {
            setContext(context);
        }

        public AlertNavDialog build() throws MissingResourceException {
            if (view == null || context == null || activities == null || packageManager == null) {
                throw new MissingResourceException("can't build object",
                                                   AlertNavDialog.class.getName(), "Builder");
            }
            return new AlertNavDialog(this);
        }
    }

    public interface OnMapsAppSelected {
        void onSelected(Intent mapIntent);
    }
}
