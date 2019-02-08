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
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.fleet.dao.model.submodel.SopacLOG;
import com.mapotempo.lib.R;

import java.util.List;

public class SurveySopacNFCTemperatureDialogFragment extends SurveyBaseDialogFragment
{
    public static SurveySopacNFCTemperatureDialogFragment newInstance(List<SopacLOG> sopacLOGs, Long sopacIdFocus)
    {
        SurveySopacNFCTemperatureDialogFragment f = new SurveySopacNFCTemperatureDialogFragment();
        f.mSopacLOGList = sopacLOGs;
        f.mSopacIdFocus = new Long(sopacIdFocus);
        return f;
    }

    public interface SurveySopacNFCTemperatureListener
    {
        boolean onSopacNFCSave(List<SopacLOG> sopacLOGs);

        boolean onSopacNFCClearOne(List<SopacLOG> sopacLOGs);

        boolean onSopacNFCClear();
    }

    private SurveySopacNFCTemperatureListener mSurveySopacNFCTemperatureListener = new SurveySopacNFCTemperatureListener()
    {
        @Override
        public boolean onSopacNFCSave(List<SopacLOG> sopacLOGs)
        {
            return true;
        }

        @Override
        public boolean onSopacNFCClear()
        {
            return true;
        }

        @Override
        public boolean onSopacNFCClearOne(List<SopacLOG> sopacLOGs)
        {
            return true;
        }
    };

    public void setSurveySopacNFCTemperatureListener(SurveySopacNFCTemperatureListener surveySopacNFCTemperatureListener)
    {
        mSurveySopacNFCTemperatureListener = surveySopacNFCTemperatureListener;
    }

    Long mSopacIdFocus;

    List<SopacLOG> mSopacLOGList;

    ViewPager mViewPager;

    SurveySopacNFCTemperatureDialogAdapter mPagerAdapter;

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.survey_fragment_sopac_temperature_pager, container, true);
        mPagerAdapter = new SurveySopacNFCTemperatureDialogAdapter(getChildFragmentManager(), mSopacLOGList);
        mViewPager = view.findViewById(R.id.sopac_viewpager);
        mViewPager.setAdapter(mPagerAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setPositiveText(R.string.close);
        for (SopacLOG sopacLOG : mSopacLOGList)
        {
            if (mSopacIdFocus.equals(sopacLOG.getTagID()))
                mViewPager.setCurrentItem(mSopacLOGList.indexOf(sopacLOG));
        }
    }

    // ================================
    // ==  SurveyBaseDialogFragment  ==
    // ================================

    @Override
    protected boolean onPositive()
    {
        return true;
    }

    @Override
    protected boolean onClear()
    {
        if (mSopacLOGList.isEmpty())
        {
            return true;
        }
        mSopacLOGList.remove(mViewPager.getCurrentItem());
        mPagerAdapter.refreshMissions(mSopacLOGList);
        mSurveySopacNFCTemperatureListener.onSopacNFCClearOne(mSopacLOGList);
        return mSopacLOGList.isEmpty();
    }
}
