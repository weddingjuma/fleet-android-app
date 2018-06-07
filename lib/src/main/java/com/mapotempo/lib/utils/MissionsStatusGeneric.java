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

public class MissionsStatusGeneric<V, T>
{

    private V view;
    private T type;

    /**
     * A Generic class that hold a view and a type, mostly used for the missions status
     *
     * @param view Any object descendant of View.
     * @param type Type of a status, can be anything from the database.
     * @throws NullPointerException
     */
    public MissionsStatusGeneric(V view, T type) throws NullPointerException
    {
        this.view = view;
        this.type = type;

        if (view == null)
            throw new NullPointerException("View can't be null");

        if (type == null)
            throw new NullPointerException("type can't be null");
    }

    public V getView()
    {
        return view;
    }

    public T getType()
    {
        return type;
    }
}
