package com.mapotempo.lib;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;

public interface MapotempoApplicationInterface {
    MapotempoFleetManagerInterface getManager();

    void setManager(MapotempoFleetManagerInterface manager);
}
