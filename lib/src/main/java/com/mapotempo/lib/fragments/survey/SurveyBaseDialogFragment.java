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
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseDialogFragment;

abstract public class SurveyBaseDialogFragment extends MapotempoBaseDialogFragment
{
    protected Button mNegativeButton;

    protected Button mPositiveButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // FIXME Close dialog fragment on restored activity
        if (savedInstanceState != null)
        {
            dismiss();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mNegativeButton = view.findViewById(R.id.negative_button);
        mPositiveButton = view.findViewById(R.id.positive_button);

        if (mNegativeButton == null || mPositiveButton == null)
            throw new RuntimeException("save_button id or clear_button layout missing in " + this.getClass().getName() + " view");

        mNegativeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (onClear())
                    dismiss();
            }
        });

        mPositiveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (onPositive())
                    dismiss();
            }
        });
    }

    public void setPositiveText(int resid)
    {
        mPositiveButton.setText(resid);
    }

    public void setNegativeText(int resid)
    {
        mNegativeButton.setText(resid);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    // ================================
    // ==  SurveyBaseDialogFragment  ==
    // ================================

    abstract protected boolean onPositive();

    abstract protected boolean onClear();
}
