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
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.SubModelBase;

public class Address extends SubModelBase
{

    private static final String STREET = "street";
    private static final String POSTALCODE = "postalcode";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String COUNTRY = "country";
    private static final String DETAIL = "detail";

    public Address(IDatabaseHandler iDatabaseHandler, String street, String postalCode, String city, String state, String country, String detail)
    {
        super(iDatabaseHandler, null);
        mDictionary.setString(STREET, street);
        mDictionary.setString(POSTALCODE, postalCode);
        mDictionary.setString(CITY, city);
        mDictionary.setString(STATE, state);
        mDictionary.setString(COUNTRY, country);
        mDictionary.setString(DETAIL, detail);
    }

    public Address(IDatabaseHandler iDatabaseHandler, Dictionary dictionary)
    {
        super(iDatabaseHandler, dictionary);
    }

    public String getStreet()
    {
        String res = mDictionary.getString(STREET);
        return res != null ? res : "";
    }

    public void setStreet(String street)
    {
        mDictionary.setString(STREET, street);
    }

    public String getPostalcode()
    {
        String res = mDictionary.getString(POSTALCODE);
        return res != null ? res : "";
    }

    public void setPostalcode(String postalcode)
    {
        mDictionary.setString(POSTALCODE, postalcode);
    }

    public String getCity()
    {
        String res = mDictionary.getString(CITY);
        return res != null ? res : "";
    }

    public void setCity(String city)
    {
        mDictionary.setString(CITY, city);
    }

    public String getState()
    {
        String res = mDictionary.getString(STATE);
        return res != null ? res : "";
    }

    public void setState(String state)
    {
        mDictionary.setString(STATE, state);
    }

    public String getCountry()
    {
        String res = mDictionary.getString(COUNTRY);
        return res != null ? res : "";
    }

    public void setCountry(String country)
    {
        mDictionary.setString(COUNTRY, country);
    }

    public String getDetail()
    {
        String res = mDictionary.getString(DETAIL);
        return res != null ? res : "";
    }

    public void setDetail(String detail)
    {
        mDictionary.setString(DETAIL, detail);
    }

    public boolean isValid()
    {
        if (getStreet() != null && !getStreet().trim().isEmpty())
            return true;
        if (getPostalcode() != null && !getPostalcode().trim().isEmpty())
            return true;
        if (getCity() != null && !getCity().trim().isEmpty())
            return true;
        if (getState() != null && !getState().trim().isEmpty())
            return true;
        if (getCountry() != null && !getCountry().trim().isEmpty())
            return true;
        return false;
    }

    public boolean equals(Object obj)
    {
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
