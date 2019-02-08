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

import android.nfc.Tag;
import android.os.AsyncTask;

import com.mapotempo.fleet.dao.model.submodel.SopacLOG;
import com.mapotempo.fleet.dao.model.submodel.Temperature;
import com.mapotempo.fleet.manager.MapotempoFleetManager;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import eu.blulog.blulib.exceptions.BluException;
import eu.blulog.blulib.exceptions.NoBlutagDedectedException;
import eu.blulog.blulib.tdl2.BlutagContent;
import eu.blulog.blulib.tdl2.BlutagHandler;
import eu.blulog.blulib.tdl2.Recording;

public class SopacBackgroundTagProcessor extends AsyncTask<Tag, Integer, SopacLOG>
{
    private MapotempoFleetManager mMapotempoFleetManager;

    private TagOperation tagOperation = TagOperation.StartNewRecording;

    public enum TagOperation
    {
        StartNewRecording, Other
    }

    public SopacBackgroundTagProcessor(MapotempoFleetManager mapotempoFleetManager)
    {
        mMapotempoFleetManager = mapotempoFleetManager;
    }

    @Override
    protected SopacLOG doInBackground(Tag... tags)
    {
        try
        {


            return processTag(tags[0], mMapotempoFleetManager);
        } catch (NoBlutagDedectedException e)
        {
            return null;
        } catch (BluException e)
        {
            if (tagOperation == TagOperation.StartNewRecording && e.getCodeError() == 0xF1)
                return null;
            e.printStackTrace();
            return null;
        }
    }

    public static SopacLOG processTag(Tag tag, MapotempoFleetManager mapotempoFleetManager) throws BluException
    {
        BlutagHandler.get().readBlutag(tag);
        if (BlutagContent.get().getRecordings().size() > 0
            && BlutagContent.get().getRecordings().get(0).getRegistrationStartDate().getTime() < Recording.START_BY_BUTTON)
        {
            Recording recording = BlutagContent.get().getRecordings().get(0);
            recording.computeStatistics();
            //            Toast.makeText(this, BlutagContent.get()., Toast.LENGTH_SHORT).show();
            List<Temperature> temperatures = new LinkedList<>();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(recording.getRegistrationStartDate());
            recording.computeStatistics();
            for (short temperature : recording.getTemperatures())
            {
                temperatures.add(new Temperature(mapotempoFleetManager, (short) (temperature / 10.), calendar.getTime()));
                calendar.add(Calendar.SECOND, recording.getMeasurementCycleInSeconds());
            }
            return new SopacLOG(mapotempoFleetManager,
                BlutagContent.get().getBlueTagId(),
                new Date(),
                temperatures,
                (float) recording.getMaxTemp(),
                (float) recording.getMinTemp(),
                "C°",
                recording.getStatistics().getBreachesCount());
        }
        return null;
    }
}
