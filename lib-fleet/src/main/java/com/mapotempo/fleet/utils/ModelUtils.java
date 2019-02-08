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
import com.couchbase.lite.MutableArray;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.SubModelBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ModelUtils
{
    public static <T extends SubModelBase> Array submodelListToArray(IDatabaseHandler databaseHandler,
                                                                     List<T> list,
                                                                     Class<T> submodelClazz)
    {
        MutableArray res = new MutableArray();
        for (SubModelBase quantity : list)
        {
            if (quantity.isValid())
                res.addDictionary(quantity.getDictionary());
            else
                throw new RuntimeException("Submodel must be valid");
        }
        return res;
    }

    public static <T extends SubModelBase> List<T> arrayToSubmodelList(IDatabaseHandler databaseHandler,
                                                                       Array array,
                                                                       Class<T> submodelClazz)
    {
        List<T> res = new ArrayList<>();
        if (array == null)
            return res;

        try
        {
            Constructor<T> submodelConstructor = submodelClazz.getConstructor(IDatabaseHandler.class, Dictionary.class);
            for (int i = 0; i < array.count(); i++)
            {
                Dictionary dico = array.getDictionary(i);
                res.add(submodelConstructor.newInstance(databaseHandler, dico));
            }
        } catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        } catch (InstantiationException e)
        {
            throw new RuntimeException();
        } catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
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
