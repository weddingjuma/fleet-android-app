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

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapotempo.fleet.dao.model.submodel.Quantity;
import com.mapotempo.lib.R;

import java.util.List;

public class SurveyQuantityDialogFragment extends SurveyBaseDialogFragment
{
    public static SurveyQuantityDialogFragment newInstance(@NonNull List<Quantity> editQuantities)
    {
        if (editQuantities == null)
            throw new RuntimeException("editQuantities can't be null");

        SurveyQuantityDialogFragment f = new SurveyQuantityDialogFragment();
        f.mEditQuantities = editQuantities;
        return f;
    }

    public interface SurveyQuantityListener
    {
        boolean onQuantitySave(List<Quantity> editQuantities);

        boolean onQuantityClear();
    }

    public void setListener(SurveyQuantityListener surveyQuantityListener)
    {
        mSurveyQuantityListener = surveyQuantityListener;
    }

    private List<Quantity> mEditQuantities;

    private LinearLayout mLayoutQuantitiesContainer;

    private SurveyQuantityListener mSurveyQuantityListener = new SurveyQuantityListener()
    {
        @Override
        public boolean onQuantitySave(List<Quantity> editQuantities)
        {
            return false;
        }

        @Override
        public boolean onQuantityClear()
        {
            return false;
        }
    };

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.survey_fragment_quantities, container, false);
        mLayoutQuantitiesContainer = v.findViewById(R.id.quantities_container);
        refreshView();
        return v;
    }

    // ==========================
    // ==  SurveyBaseDialogFragment  ==
    // ==========================

    @Override
    protected boolean onSave()
    {
        return mSurveyQuantityListener.onQuantitySave(mEditQuantities);
    }

    @Override
    protected boolean onClear()
    {
        return mSurveyQuantityListener.onQuantityClear();
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void refreshView()
    {
        mLayoutQuantitiesContainer.removeAllViews();

        for (final Quantity quantity : mEditQuantities)
        {
            if (!quantity.isValid()) { continue; }

            View quantityLayout = getLayoutInflater().inflate(R.layout.quantity_edit_layout, null);

            ImageButton remove = quantityLayout.findViewById(R.id.remove_unit);
            remove.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    quantity.setQuantity(quantity.getQuantity() - 1);
                    refreshView();
                }
            });

            ImageButton add = quantityLayout.findViewById(R.id.add_unit);
            add.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    quantity.setQuantity(quantity.getQuantity() + 1);
                    refreshView();
                }
            });

            TextView label = quantityLayout.findViewById(R.id.quantity_label);
            label.setText(String.valueOf(quantity.getQuantity()) + " " + quantity.getLabel());

            String iconName = quantity.getUnitIcon().replace("-", "_icon_");
            String fontIcon;
            try
            {
                fontIcon = getString(getResources().getIdentifier(iconName, "string", getActivity().getPackageName()));
            } catch (Resources.NotFoundException e)
            {
                Log.e("Ressource Not Found", e.getMessage());
                fontIcon = getString(R.string.fa_icon_archive);
            }
            TextView icon = quantityLayout.findViewById(R.id.quantity_icon);
            icon.setText(fontIcon);
            icon.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome-webfont.ttf"));


            mLayoutQuantitiesContainer.addView(quantityLayout);
        }
    }
}
