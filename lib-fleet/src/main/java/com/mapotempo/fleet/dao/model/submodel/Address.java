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

package com.mapotempo.fleet.dao.model.submodel;

import com.couchbase.lite.Dictionary;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.SubModelBase;

public class Address extends SubModelBase {

    private static final String STREET = "street";
    private static final String POSTALCODE = "postalcode";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String COUNTRY = "country";
    private static final String DETAIL = "detail";

    public Address(IDatabaseHandler iDatabaseHandler, String street, String postalCode, String city, String state, String country, String detail) throws FleetException {
        super(iDatabaseHandler, null);
        mDictionary.setString(STREET, street);
        mDictionary.setString(POSTALCODE, postalCode);
        mDictionary.setString(CITY, city);
        mDictionary.setString(STATE, state);
        mDictionary.setString(COUNTRY, country);
        mDictionary.setString(DETAIL, detail);
    }

    public Address(IDatabaseHandler iDatabaseHandler, Dictionary dictionary) throws FleetException {
        super(iDatabaseHandler, dictionary);
    }

    public String getStreet() {
        String res = mDictionary.getString(STREET);
        return res != null ? res : "";
    }

    public String getPostalcode() {
        String res = mDictionary.getString(POSTALCODE);
        return res != null ? res : "";
    }

    public String getCity() {
        String res = mDictionary.getString(CITY);
        return res != null ? res : "";
    }

    public String getState() {
        String res = mDictionary.getString(STATE);
        return res != null ? res : "";
    }

    public String getCountry() {
        String res = mDictionary.getString(COUNTRY);
        return res != null ? res : "";
    }

    public String getDetail() {
        String res = mDictionary.getString(DETAIL);
        return res != null ? res : "";
    }

    public boolean isValid() {
        if (getStreet() != null || getStreet().trim().isEmpty())
            return false;
        if (getPostalcode() != null || getPostalcode().trim().isEmpty())
            return false;
        if (getCity() != null || getCity().trim().isEmpty())
            return false;
        if (getState() != null || getState().trim().isEmpty())
            return false;
        if (getCountry() != null || getCountry().trim().isEmpty())
            return false;
        return true;
    }

    public boolean equals(Object obj) {
        if (super.equals(obj))
            return true;

        if (!(obj instanceof Address))
            return false;

        Address cmp = (Address) obj;
        return (getStreet().equals(cmp.getStreet()) &&
                getPostalcode().equals(cmp.getPostalcode()) &&
                getCity().equals(cmp.getCity()) &&
                getCountry().equals(cmp.getCountry()) &&
                getState().equals(cmp.getState()) &&
                getDetail().equals(cmp.getDetail()) &&
                super.equals(cmp));
    }
}
