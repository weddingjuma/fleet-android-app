package com.mapotempo.app.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapotempo.app.R;

public class DrawerListAdapter extends ArrayAdapter<ListItemCustom> {
    private final int layoutResourceId;
    private final Context context;
    private final ListItemCustom[] data;

    public DrawerListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ListItemCustom[] data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ListItemCustomHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ListItemCustomHolder();
            holder.imageView = row.findViewById(R.id.imgIcon);
            holder.textView = row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        } else {
            holder = (ListItemCustomHolder)row.getTag();
        }

        ListItemCustom item = data[position];

        holder.imageView.setImageResource(item.icon);
        holder.imageView.setColorFilter(item.color);
        holder.textView.setText(item.title);

        return row;
    }

    private static class ListItemCustomHolder {
        ImageView imageView;
        TextView textView;
    }
}
