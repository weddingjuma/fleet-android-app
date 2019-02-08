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

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;
import com.mapotempo.fleet.dao.model.submodel.SopacLOG;
import com.mapotempo.fleet.dao.model.submodel.Temperature;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SurveySopacNFCTemperatureDialogAdapter extends FragmentStatePagerAdapter
{
    protected List<SopacLOG> mSopacLOGList;

    public SurveySopacNFCTemperatureDialogAdapter(FragmentManager fm, List<SopacLOG> sopacLOGList)
    {
        super(fm);
        mSopacLOGList = new ArrayList<>(sopacLOGList);
    }

    @Override
    public int getCount()
    {
        return mSopacLOGList.size();
    }

    @Override
    public Fragment getItem(int position)
    {
        SurveySopacNFCTemperatureFragment fragment = SurveySopacNFCTemperatureFragment.create(position, mSopacLOGList.get(position));
        return fragment;
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    public void refreshMissions(List<SopacLOG> sopacLOGList)
    {
        mSopacLOGList = sopacLOGList;
        notifyDataSetChanged();
    }


    public static class SurveySopacNFCTemperatureFragment extends MapotempoBaseFragment
    {
        public static SurveySopacNFCTemperatureFragment create(int pageNumber, SopacLOG sopacLOG)
        {
            SurveySopacNFCTemperatureFragment fragment = new SurveySopacNFCTemperatureFragment();
            fragment.setSopacLog(sopacLOG);
            return fragment;
        }

        private Series mSeries;
        private Series mMaxSeries;
        private Series mMinSeries;
        private long mId;

        public void setSopacLog(SopacLOG sopacLOG)
        {
            List<DataPoint> dataPointsList = new LinkedList<>();
            for (Temperature t : sopacLOG.getTemperatures())
            {
                dataPointsList.add(new DataPoint(t.getDate(), t.getValue()));
            }
            DataPoint[] dataPointsArray = dataPointsList.toArray(new DataPoint[dataPointsList.size()]);
            // Trick to prevent unorder data
            Arrays.sort(dataPointsArray, new Comparator<DataPoint>()
            {
                @Override
                public int compare(DataPoint o1, DataPoint o2)
                {
                    return (int) (o1.getX() - o2.getX());
                }
            });
            mSeries = new LineGraphSeries<>(dataPointsArray);


            DataPoint[] maxPoint = {
                new DataPoint(mSeries.getLowestValueX(), sopacLOG.getTemperatureMax()),
                new DataPoint(mSeries.getHighestValueX(), sopacLOG.getTemperatureMax())};
            DataPoint[] minPoint = {
                new DataPoint(mSeries.getLowestValueX(), sopacLOG.getTemperatureMin()),
                new DataPoint(mSeries.getHighestValueX(), sopacLOG.getTemperatureMin())};
            mMaxSeries = new LineGraphSeries<>(maxPoint);
            ((LineGraphSeries) mMaxSeries).setColor(Color.RED);
            mMinSeries = new LineGraphSeries<>(minPoint);
            ((LineGraphSeries) mMinSeries).setColor(Color.RED);

            mId = sopacLOG.getTagID();
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {
            super.onCreateView(inflater, container, savedInstanceState);
            View view = inflater.inflate(R.layout.survey_fragment_sopac_temperature, container, false);
            GraphView graphView = view.findViewById(R.id.graph_view);
            graphView.addSeries(mSeries);
            graphView.addSeries(mMaxSeries);
            graphView.addSeries(mMinSeries);

            graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
            graphView.getGridLabelRenderer().setHorizontalLabelsAngle(70);
            // graphView.getGridLabelRenderer().setNumHorizontalLabels(2); // only 4 because of the space

            TextView textView = view.findViewById(R.id.id_view);
            textView.setText("Sopac Tag ID : " + Long.toString(mId));
            return view;
        }
    }
}
