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

package com.mapotempo.fleet.manager;

/**
 * Connection status :
 * 0 : verify
 * 2XX : document missing
 */
public enum LOGIN_STATUS {
    VERIFY(200),
    LOGIN_ERROR(401),
    USER_MISSING(404),
    USER_CURRENT_LOCATION_MISSING(404),
    USER_SETTING_MISSING(404),
    META_INFO_MISSING(404),
    COMPANY_MISSING(404),
    UNKNOW_ERROR(520);

    private int mCode;

    private String mPayload;

    LOGIN_STATUS(int code) {
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }

    public void setPayload(String payload) {
        mPayload = payload;
    }

    public String getPayload() {
        return mPayload;
    }
}
