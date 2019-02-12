package com.mapotempo.fleet.dao.model.nested;

import com.couchbase.lite.Dictionary;
import com.mapotempo.fleet.core.IDatabaseHandler;
import com.mapotempo.fleet.core.model.NestedModelBase;

public class Quantity extends NestedModelBase
{

    public static final String DELIVERABLE_UNIT_ID = "deliverable_unit_id";
    public static final String QUANTITY = "quantity";
    public static final String SURVEY_QUANTITY = "survey_quantity";
    public static final String LABEL = "label";
    public static final String UNIT_ICON = "unit_icon";
    public static final String QUANTITY_FORMATTED = "quantity_formatted";

    /**
     * @param iDatabaseHandler
     * @param dictionary
     */
    public Quantity(IDatabaseHandler iDatabaseHandler, Dictionary dictionary)
    {
        super(iDatabaseHandler, dictionary);
    }

    /**
     * getDeliverableUnitId
     * get the deliverable unit id quantity
     *
     * @return the
     */
    public int getDeliverableUnitId()
    {
        return mDictionary.getInt(DELIVERABLE_UNIT_ID);
    }

    /**
     * getPreferredQuantity
     * get the survey quantity if it's available, else quantity
     *
     * @return Quantity
     */
    public float getPreferedQuantity()
    {
        if (mDictionary.contains(SURVEY_QUANTITY))
            return mDictionary.getFloat(SURVEY_QUANTITY);
        return mDictionary.getFloat(QUANTITY);
    }

    /**
     * setSurveyQuantity
     * set the syrvey quantity
     *
     * @param quantity the survey quantity to set
     */
    public void setSurveyQuantity(float quantity)
    {
        mDictionary.setFloat(SURVEY_QUANTITY, quantity);
    }

    /**
     * removeSurveyQuantity
     * remove the survey quantity
     */
    public void removeSurveyQuantity()
    {
        mDictionary.remove(SURVEY_QUANTITY);
    }

    /**
     * getLabel
     * get the quanity label
     *
     * @return label
     */
    public String getLabel()
    {
        String label = mDictionary.getString(LABEL);
        if (label == null || "".equals(label))
        {
            label = "N/A";
        }
        return label;
    }

    /**
     * getUnitIcon
     * get the unit icon
     *
     * @return unit
     */
    public String getUnitIcon()
    {
        return mDictionary.getString(UNIT_ICON);
    }

    /**
     * getQuantityFormatted
     * get the formated quantity string
     *
     * @return formated quantity
     */
    public String getQuantityFormatted()
    {
        return mDictionary.getString(QUANTITY_FORMATTED);
    }

    @Override
    public boolean isValid()
    {
        return getDeliverableUnitId() > 0 && getUnitIcon() != null && !getUnitIcon().equals("");
    }
}
