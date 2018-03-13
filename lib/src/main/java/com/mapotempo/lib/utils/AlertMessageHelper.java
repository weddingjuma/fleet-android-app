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

package com.mapotempo.lib.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mapotempo.lib.R;

public class AlertMessageHelper {

    private AlertMessageHelper() {
    }

    /**
     * Public helper which will display an alert box with text/xmlID given
     *
     * @param context    The context where the alert must be displayed
     * @param customView the view to be Inflate.
     * @param title      Custom title
     * @param message    Custom message
     * @param details    Custom Details
     */
    public static void errorAlert(Context context, Integer customView, String title, String message, String details) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alert = builder.create();
        LayoutInflater inflater = LayoutInflater.from(context);
        View box;

        if (customView == null) {
            box = inflater.inflate(R.layout.alert_dialog, null);
        } else {
            box = inflater.inflate(customView, null);
        }

        TextView titleView = box.findViewById(R.id.dialog_title);
        TextView errorView = box.findViewById(R.id.dialog_message);
        TextView detailsView = box.findViewById(R.id.dialog_details);

        titleView.setText(title);
        errorView.setText(message);
        String message_code = details;
        detailsView.setText(message_code);

        alert.setView(box);
        alert.show();
    }
}
