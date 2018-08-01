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

package com.mapotempo.fleet.core.accessor;

import android.support.annotation.Nullable;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Document;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.DocumentChangeListener;
import com.couchbase.lite.Expression;
import com.couchbase.lite.IndexBuilder;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.QueryChange;
import com.couchbase.lite.QueryChangeListener;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;
import com.couchbase.lite.ValueIndexItem;
import com.mapotempo.fleet.api.FleetException;
import com.mapotempo.fleet.core.Base;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.ModelBase;
import com.mapotempo.fleet.core.model.ModelType;
import com.mapotempo.fleet.utils.DateUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * AccessBase.
 */
public abstract class AccessBase<T extends ModelBase> extends Base
{

    private final String TAG = AccessBase.class.getName();

    private Constructor<T> mConstructorFromDocument;

    private Class<T> mModelClazz;

    private ModelType mDocumentAnnotation;

    private final Expression mBaseExpression;


    final HashMap<ModelAccessToken, ModelAccessChangeListener> mModelAccessMap;
    private final HashMap<LiveAccessToken, LiveAccessChangeListener> mLiveAccessMap;

    private String mDefaultSortField = "date";

    public AccessBase(IDatabaseHandler databaseHandler, Class<T> clazz, @Nullable final String sortField) throws FleetException
    {
        super(databaseHandler);
        mDefaultSortField = sortField != null ? sortField : mDefaultSortField;

        mLiveAccessMap = new HashMap();
        mModelAccessMap = new HashMap();
        mModelClazz = clazz;
        mDocumentAnnotation = mModelClazz.getAnnotation(ModelType.class);

        try
        {
            mConstructorFromDocument = mModelClazz.getConstructor(IDatabaseHandler.class, Document.class);
        } catch (NoSuchMethodException e)
        {
            throw new FleetException("Error : wrong definition of fleet model " + mModelClazz + " constructor in : ");
        }

        if (mDocumentAnnotation == null)
            throw new FleetException("Error : The fleet model " + mModelClazz + " need to be annotate with " + ModelType.class);

        try
        {
            mDatabaseHandler.getDatabase().createIndex(mDocumentAnnotation.type(), IndexBuilder.valueIndex(ValueIndexItem.property("type")));
        } catch (CouchbaseLiteException e)
        {
            throw new FleetException("Error during index creation", e);
        }
        mBaseExpression = Expression.property(mDocumentAnnotation.type_field()).equalTo(Expression.string(mDocumentAnnotation.type()));
    }

    public T get(String id)
    {
        Document document = mDatabaseHandler.getDatabase().getDocument(id);
        if (document == null)
            return null;
        if (!document.contains(mDocumentAnnotation.type_field()))
            return null;
        String type = document.getString(mDocumentAnnotation.type_field());
        if (!mDocumentAnnotation.type().equals(type))
            return null;
        return getInstance(document);
    }

    public ModelAccessToken addListener(final T item, final ModelAccessChangeListener<T> listener)
    {
        final String id = item.getId();
        ListenerToken tk = mDatabaseHandler.getDatabase().addDocumentChangeListener(id, new DocumentChangeListener()
        {
            @Override
            public void changed(DocumentChange change)
            {
                Document document = mDatabaseHandler.getDatabase().getDocument(id);
                if (document != null)
                    listener.changed(getInstance(document));
            }
        });
        ModelAccessToken res = new ModelAccessToken<T>(item, tk);
        mModelAccessMap.put(res, listener);
        return res;
    }

    public void removeListener(ModelAccessToken modelAccessToken)
    {
        mLiveAccessMap.remove(modelAccessToken);
        mDatabaseHandler.getDatabase().removeChangeListener(modelAccessToken.mListenerToken);
    }

    // =============================================================================================
    // == Query Definition
    // =============================================================================================

    public T getNew()
    {
        MutableDocument document = new MutableDocument();
        document.setString(mDocumentAnnotation.type_field(), mDocumentAnnotation.type());
        return getInstance(new MutableDocument());
    }

    /**
     * all.
     * Type view filter
     *
     * @return all data <T> in a list
     */
    public List<T> all()
    {
        return runQuery(null, mDefaultSortField);
    }

    public List<T> byDateGreaterThan(Date date)
    {
        return runQuery(Expression.property(getDefaultSortdField()).greaterThan(Expression.string(DateUtils.toStringISO8601(date))), mDefaultSortField);
    }

    public List<T> byDateLessThan(Date date)
    {
        return runQuery(Expression.property(getDefaultSortdField()).lessThan(Expression.string(DateUtils.toStringISO8601(date))), mDefaultSortField);
    }

    public List<T> byDateBetween(String dateField, Date date1, Date date2)
    {
        return runQuery(Expression.property(getDefaultSortdField()).between(Expression.string(DateUtils.toStringISO8601(date1)), Expression.string(DateUtils
            .toStringISO8601(date2))), mDefaultSortField);
    }

    // =============================================================================================
    // == Live Query Definition
    // =============================================================================================

    public LiveAccessToken all_AddListener(final LiveAccessChangeListener<T> changeListener)
    {
        return createLiveQuery(changeListener, null, mDefaultSortField);
    }

    public LiveAccessToken byDateGreaterThan_AddListener(final LiveAccessChangeListener<T> changeListener, Date date)
    {
        return createLiveQuery(changeListener, Expression.property(getDefaultSortdField()).greaterThan(Expression.string(DateUtils.toStringISO8601(date))), mDefaultSortField);
    }

    public LiveAccessToken byDateLessThanAdd_AddListener(final LiveAccessChangeListener<T> changeListener, Date date)
    {
        return createLiveQuery(changeListener, Expression.property(getDefaultSortdField()).lessThan(Expression.string(DateUtils.toStringISO8601(date))), mDefaultSortField);
    }

    public LiveAccessToken byDateBetween_AddListener(final LiveAccessChangeListener<T> changeListener, Date date1, Date date2)
    {
        return createLiveQuery(changeListener, Expression.property(getDefaultSortdField()).between(Expression.string(DateUtils.toStringISO8601(date1)), Expression
            .string(DateUtils.toStringISO8601(date2))), mDefaultSortField);
    }

    public void removeListener(LiveAccessToken liveAccessToken)
    {
        mLiveAccessMap.remove(liveAccessToken);
        liveAccessToken.mLiveQuery.removeChangeListener(liveAccessToken.mListenerToken);
    }

    // =============================================================================================
    // == Protected method
    // =============================================================================================

    protected String getDefaultSortdField()
    {
        return mDefaultSortField;
    }

    protected List<T> runQuery(@Nullable Expression expression, @Nullable String orderField)
    {
        Query query = getQuery(expression, orderField);
        List<T> res = new ArrayList<>();
        try
        {
            Log.d(TAG, query.explain());
            ResultSet rs = query.execute();
            for (Result result : rs)
            {
                Log.d(TAG, result.toMap().toString());
                String id = result.getString("id");
                Document doc = mDatabaseHandler.getDatabase().getDocument(id);
                res.add(getInstance(doc));
            }
        } catch (CouchbaseLiteException e)
        {
            Log.e("Sample", e.getLocalizedMessage());
        }
        return res;
    }

    protected LiveAccessToken createLiveQuery(final LiveAccessChangeListener<T> changeListener, @Nullable Expression expression, @Nullable String sortField)
    {
        Query query = getQuery(expression, sortField);
        ListenerToken listenerToken = query.addChangeListener(new QueryChangeListener()
        {
            @Override
            public void changed(QueryChange change)
            {
                List<T> res = new ArrayList<>();
                ResultSet rs = change.getResults();
                for (Result result : rs)
                {
                    String id = result.getString("id");
                    Document doc = mDatabaseHandler.getDatabase().getDocument(id);
                    res.add(getInstance(doc));
                }
                changeListener.changed(res);
            }
        });
        LiveAccessToken res = new LiveAccessToken(query, listenerToken);
        mLiveAccessMap.put(res, changeListener);
        return res;
    }

    // =============================================================================================
    // == Private method
    // =============================================================================================

    private Query getQuery(@Nullable Expression expression, @Nullable String sortField)
    {
        Expression e = expression != null ? expression.and(mBaseExpression) : mBaseExpression;
        return QueryBuilder
            .select(SelectResult.expression(Meta.id), SelectResult.all())
            .from(DataSource.database(mDatabaseHandler.getDatabase()))
            .where(e)
            .orderBy(Ordering.property(sortField != null ? sortField : mDefaultSortField));
    }

    private T getInstance(Document document) throws UnknownError
    {
        T res = null;
        try
        {
            res = mConstructorFromDocument.newInstance(mDatabaseHandler, document);
        } catch (InstantiationException e)
        {
            e.printStackTrace();
            throw new UnknownError(e.getMessage());
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
            throw new UnknownError(e.getMessage());
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
            throw new UnknownError(e.getMessage());
        }
        return res;
    }

}
