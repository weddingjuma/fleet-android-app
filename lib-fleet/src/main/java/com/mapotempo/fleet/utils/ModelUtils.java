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

package com.mapotempo.fleet.utils;

import com.couchbase.lite.Array;
import com.couchbase.lite.Dictionary;
import com.mapotempo.fleet.core.model.SubModelBase;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ModelUtils
{

    public static <T extends SubModelBase> List<T> arrayToSubmodelList(Array array, Class<T> submodelClazz)
    {
        List<T> res = new ArrayList<>();
        if (array == null)
            return res;
        for (int i = 0; i < array.count(); i++)
        {
            Dictionary dico = array.getDictionary(i);
            try
            {
                res.add(submodelClazz.getConstructor(Dictionary.class).newInstance(dico));
            } catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            } catch (InstantiationException e)
            {
                e.printStackTrace();
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static List<String> arrayToStringList(Array array)
    {
        List<String> res = new ArrayList<>();
        if (array == null)
            return res;
        for (int i = 0; i < array.count(); i++)
        {
            String str = array.getString(i);
            res.add(new String());
        }
        return res;
    }
}
