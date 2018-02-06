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

package com.mapotempo.lib.view.action;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapotempo.lib.R;

public class MissionActionPanel extends LinearLayout {

    private View rootView;
    private ImageView mActionImageView;
    private TextView mActionTextView;
    private boolean mShowLabel = true;

    public MissionActionPanel(Context context) {
        super(context);
        init(context);
    }

    public MissionActionPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MissionActionPanel,
                0, 0);

        try {
            mShowLabel = a.getBoolean(R.styleable.MissionActionPanel_showLabel, true);
        } finally {
            a.recycle();
        }

        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        setOrientation(VERTICAL);
        rootView = inflate(context, R.layout.view_mission_action_panel, this);

        mActionImageView = rootView.findViewById(R.id.icon_view);
        mActionTextView = rootView.findViewById(R.id.description_view);

        if (mShowLabel)
            mActionTextView.setVisibility(VISIBLE);
        else
            mActionTextView.setVisibility(GONE);
    }

    public final void setText(CharSequence text) {
        mActionTextView.setText(text);
    }

    public void setImageDrawable(Drawable drawable) {
        mActionImageView.setImageDrawable(drawable);
    }
}

