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

package com.mapotempo.fleet.core.model;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.DocumentChangeListener;
import com.couchbase.lite.MutableDocument;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.Base;
import com.mapotempo.fleet.core.IDatabaseHandler;

/**
 * ModelBase.
 */
public abstract class ModelBase extends Base {

    private static String TAG = ModelBase.class.getName();

    public MutableDocument mDocument;

    public ModelBase(IDatabaseHandler databaseHandler) throws FleetException {
        super(databaseHandler);
        mDocument = new MutableDocument();
        mDatabaseHandler.getDatabase().addDocumentChangeListener(mDocument.getId(), new DocumentChangeListener() {
            @Override
            public void changed(DocumentChange change) {
                change.getDocumentID();

            }
        });
    }

    public ModelBase(IDatabaseHandler databaseHandler, Document document) throws FleetException {
        super(databaseHandler);
        mDocument = document.toMutable();
        mDatabaseHandler.getDatabase().addDocumentChangeListener(mDocument.getId(), new DocumentChangeListener() {
            @Override
            public void changed(DocumentChange change) {
                change.getDocumentID();

            }
        });
    }

    public String getId() {
        return mDocument.getId();
    }

    public boolean save() {
        try {
            mDatabaseHandler.getDatabase().save(mDocument);
            return true;
        } catch (CouchbaseLiteException e) {
            return false;
        }
    }
}
