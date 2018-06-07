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

package com.mapotempo.fleet.dao.model;

import com.couchbase.lite.Document;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.ModelBase;
import com.mapotempo.fleet.core.model.ModelType;
import com.mapotempo.fleet.dao.access.MissionActionTypeAccess;

import java.util.ArrayList;
import java.util.List;

@ModelType(type = "mission_status_type")
public class MissionStatusType extends ModelBase
{

    // MAPOTEMPO KEY
    public static final String LABEL = "label";
    public static final String COLOR = "color";
    public static final String SVG_PATH = "svg_path";
    public static final String COMMANDS = "commands";

    // Default value
    // TODO Change it
    private static final String DEFAULT_SVG = "M604.1,440.2h-19.1V333.2c0,-12.5 -3.8,-24.8 -10.8,-35.2l-47.9,-71c-11.7,-17.3 -31.2,-27.7 -52.2,-27.7h-74.3c-8.7,0 -15.7,7.1 -15.7,15.7v225H262.5c11.6,10 19.5,23.9 21.8,39.7H412.5c4.6,-31.2 31.5,-55.4 64,-55.4c32.5,0 59.3,24.2 63.9,55.4h63.7c4.3,0 7.9,-3.5 7.9,-7.9V448C612,443.7 608.5,440.2 604.1,440.2zM525.8,312.2h-98c-4.3,0 -7.9,-3.5 -7.9,-7.9v-54.4c0,-4.3 3.5,-7.9 7.9,-7.9h59.7c2.6,0 5,1.3 6.5,3.3l38.3,54.5C535.8,305.1 532.1,312.2 525.8,312.2zM476.5,440.2c-27.1,0 -48.9,22 -48.9,49c0,27 21.9,48.9 48.9,48.9c27,0 48.9,-22 48.9,-48.9C525.4,462.1 503.5,440.2 476.5,440.2zM476.5,513.7c-13.5,0 -24.5,-11 -24.5,-24.5c0,-13.5 10.9,-24.5 24.5,-24.5c13.5,0 24.5,10.9 24.5,24.5C501,502.6 490,513.7 476.5,513.7zM68.4,440.2c-4.3,0 -7.9,3.5 -7.9,7.9v23.9c0,4.3 3.5,7.9 7.9,7.9h88c2.3,-15.7 10.2,-29.7 21.7,-39.7H68.4V440.2zM220.3,440.2c-27,0 -48.9,22 -48.9,49c0,27 22,48.9 48.9,48.9c27.1,0 48.9,-22 48.9,-48.9C269.2,462.1 247.4,440.2 220.3,440.2zM220.3,513.7c-13.5,0 -24.5,-11 -24.5,-24.5c0,-13.5 10.9,-24.5 24.5,-24.5c13.5,0 24.5,10.9 24.5,24.5C244.8,502.6 233.8,513.7 220.3,513.7zM338,150.6h-91.2c4.5,13.3 6.8,27.5 6.8,42.3c0,74.3 -60.4,134.7 -134.7,134.7c-13.5,0 -26.7,-2 -39,-5.7v86.9c0,4.3 3.5,7.9 7.9,7.9h266c4.3,0 7.9,-3.5 7.9,-7.9V174.2C361.6,161.1 351.1,150.6 338,150.6zM119,73.9C53.3,73.9 0,127.1 0,192.8s53.3,119 119,119s119,-53.3 119,-119S184.7,73.9 119,73.9zM119,284.7c-50.8,0 -91.9,-41.1 -91.9,-91.9c0,-50.8 41.1,-91.9 91.9,-91.9c50.8,0 91.9,41.1 91.9,91.9C210.9,243.6 169.7,284.7 119,284.7zM154.1,212.2c-1,0 -2.1,-0.1 -3.1,-0.4L112.6,201.5c-5.1,-1.4 -8.7,-6.1 -8.7,-11.4v-59c0,-6.5 5.3,-11.8 11.8,-11.8c6.5,0 11.8,5.3 11.8,11.8v50l29.6,8c6.3,1.7 10,8.2 8.3,14.5C164,208.8 159.3,212.2 154.1,212.2z";
    private static final String DEFAULT_COLOR = "#FF0000";

    public MissionStatusType(IDatabaseHandler databaseHandler, Document document) throws FleetException
    {
        super(databaseHandler, document);
    }

    public String getLabel()
    {
        String res = mDocument.getString(LABEL);
        return res != null ? res : "";
    }

    public String getColor()
    {
        String res = mDocument.getString(COLOR);
        return res != null ? res : DEFAULT_COLOR;
    }

    public String getSVGPath()
    {
        String res = mDocument.getString(SVG_PATH);
        return res != null ? res : DEFAULT_SVG;
    }

    public List<MissionActionType> getNextActionType()
    {
        MissionActionTypeAccess missionActionTypeAccess;
        try
        {
            missionActionTypeAccess = new MissionActionTypeAccess(mDatabaseHandler);
        } catch (FleetException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return missionActionTypeAccess.byPreviousStatusType(this);
    }
}
