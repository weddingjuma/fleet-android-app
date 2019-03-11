/*
 * Copyright © Mapotempo, 2019
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

package com.mapotempo.lib.view.mission;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mapotempo.lib.R;

public class MissionDetailSectionLayout extends LinearLayout
{
    private String TAG = MissionDetailSectionLayout.class.getName();

    private ImageView mIcon;


    public MissionDetailSectionLayout(Context context)
    {
        super(context);
        init(null);
    }

    public MissionDetailSectionLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public MissionDetailSectionLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs)
    {

        inflate(getContext(), R.layout.view_mission_detail_section, this);
        mIcon = findViewById(R.id.icon);

        // Programmatically set params
        int padding = getResources().getDimensionPixelSize(R.dimen.fragment_mission_body_element_padding);
        setPadding(0, padding, padding, padding);
        setOrientation(HORIZONTAL);
        setBackground(getResources().getDrawable(isClickable() ? R.drawable.bg_action_detail_item : R.drawable.bg_mission_detail_item));

        // Programmatically set icon drawable
        if (attrs != null)
        {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MissionDetailSectionLayout);
            Drawable drawable = a.getDrawable(R.styleable.MissionDetailSectionLayout_detail_icon);
            if (drawable != null)
                mIcon.setImageDrawable(drawable);
            a.recycle();
        }
    }
}
