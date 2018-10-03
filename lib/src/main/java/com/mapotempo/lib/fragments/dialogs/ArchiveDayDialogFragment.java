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

package com.mapotempo.lib.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.mapotempo.fleet.dao.model.Route;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * ArchiveDayDialogFragment.
 * This dialog fragment is a support for automatic route archiving.
 */
public class ArchiveDayDialogFragment extends MapotempoBaseDialogFragment
{
    private List<Route> mRoutesTag = new ArrayList<>();

    public boolean setRoutes(List<Route> routes)
    {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.MILLISECOND, 0);
        for (Route route : routes)
        {
            Calendar routeCalendar = Calendar.getInstance();
            routeCalendar.setTime(route.getDate());
            if (today.getTime().compareTo(routeCalendar.getTime()) > 0)
            {
                mRoutesTag.add(route);
            }
        }
        return !mRoutesTag.isEmpty();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.archive_outdated)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    for (Route route : mRoutesTag)
                    {
                        route.archived();
                    }
                }
            })
            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                }
            });
        return builder.create();
    }
}
