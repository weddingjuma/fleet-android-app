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

package com.mapotempo.lib.view.autogone;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * TextViewAG
 * This TextViewAG automatically gone after setText calling, if string is empty.
 */
public class TextViewAG extends AppCompatTextView
{

    TextViewAG(Context context)
    {
        super(context);
    }

    public TextViewAG(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextViewAG(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type)
    {
        super.setText(text, type);
        if (isEmptyTextView())
            setVisibility(GONE);
        else
            setVisibility(VISIBLE);
    }

    private boolean isEmptyTextView()
    {
        return !(getText().toString().trim().length() > 0);
    }
}
