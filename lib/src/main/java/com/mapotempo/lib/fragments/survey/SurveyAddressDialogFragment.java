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

package com.mapotempo.lib.fragments.survey;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.fleet.dao.model.submodel.Address;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.lib.MapotempoApplication;
import com.mapotempo.lib.R;

public class SurveyAddressDialogFragment extends SurveyBaseDialogFragment
{
    public static SurveyAddressDialogFragment newInstance(@NonNull Address address)
    {
        if (address == null)
            throw new RuntimeException("address can't be null");
        SurveyAddressDialogFragment f = new SurveyAddressDialogFragment();
        f.mStreet = address.getStreet();
        f.mPostalCode = address.getPostalcode();
        f.mCity = address.getCity();
        f.mState = address.getState();
        f.mCountry = address.getCountry();
        f.mDetail = address.getDetail();
        return f;
    }

    public interface SurveyAddressListener
    {
        boolean onAddressSave(Address address);

        boolean onAddressClear();
    }

    private String mStreet;
    private String mPostalCode;
    private String mCity;
    private String mState;
    private String mCountry;
    private String mDetail;

    private TextInputEditText mStreetTIET;
    private TextInputEditText mPostalCodeTIET;
    private TextInputEditText mCityTIET;
    private TextInputEditText mStateTIET;
    private TextInputEditText mCountryTIET;
    private TextInputEditText mDetailTIET;

    private SurveyAddressListener mSurveyAddressListener = new SurveyAddressListener()
    {
        @Override
        public boolean onAddressSave(Address address)
        {
            return true;
        }

        @Override
        public boolean onAddressClear()
        {
            return true;
        }
    };

    public void setSurveyAddressListener(SurveyAddressListener surveyAddressListener)
    {
        mSurveyAddressListener = surveyAddressListener;
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.survey_fragment_address, container, false);
        mStreetTIET = view.findViewById(R.id.street);
        mPostalCodeTIET = view.findViewById(R.id.postal_code);
        mCityTIET = view.findViewById(R.id.city);
        mStateTIET = view.findViewById(R.id.state);
        mCountryTIET = view.findViewById(R.id.country);
        mDetailTIET = view.findViewById(R.id.detail);

        mStreetTIET.setText(mStreet);
        mPostalCodeTIET.setText(mPostalCode);
        mCityTIET.setText(mCity);
        mStateTIET.setText(mState);
        mCountryTIET.setText(mCountry);
        mDetailTIET.setText(mDetail);
        return view;
    }

    // ==========================
    // ==  SurveyBaseDialogFragment  ==
    // ==========================

    @Override
    protected boolean onSave()
    {
        MapotempoFleetManager mapotempoFleetManager = ((MapotempoApplication) getActivity().getApplication()).getManager();
        Address surveyAddress = new Address(mapotempoFleetManager, mStreetTIET.getText().toString(),
            mPostalCodeTIET.getText().toString(),
            mCityTIET.getText().toString(),
            mStateTIET.getText().toString(),
            mCountryTIET.getText().toString(),
            mDetailTIET.getText().toString()
        );
        return mSurveyAddressListener.onAddressSave(surveyAddress);
    }

    @Override
    protected boolean onClear()
    {
        return mSurveyAddressListener.onAddressClear();
    }
}
