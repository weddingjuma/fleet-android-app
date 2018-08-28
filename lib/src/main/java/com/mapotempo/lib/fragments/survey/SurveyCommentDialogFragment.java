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

import com.mapotempo.lib.R;

public class SurveyCommentDialogFragment extends SurveyBaseDialogFragment
{
    public static SurveyCommentDialogFragment newInstance(String comment)
    {
        SurveyCommentDialogFragment f = new SurveyCommentDialogFragment();
        f.mComment = comment;
        return f;
    }

    public interface SurveyCommentListener
    {
        boolean onCommentSave(String comment);

        boolean onCommentClear();
    }

    private String mComment;

    private TextInputEditText mCommentTIET;

    private SurveyCommentListener mSurveyCommentListener = new SurveyCommentListener()
    {
        @Override
        public boolean onCommentSave(String comment)
        {
            return true;
        }

        @Override
        public boolean onCommentClear()
        {
            return true;
        }
    };

    public void setSurveyCommentListener(SurveyCommentListener surveyCommentListener)
    {
        mSurveyCommentListener = surveyCommentListener;
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.survey_fragment_comment, container, false);
        mCommentTIET = view.findViewById(R.id.comment);
        mCommentTIET.setText(mComment);
        return view;
    }

    // ==========================
    // ==  SurveyBaseDialogFragment  ==
    // ==========================

    @Override
    protected boolean onSave()
    {
        return mSurveyCommentListener.onCommentSave(mCommentTIET.getText().toString());
    }

    @Override
    protected boolean onClear()
    {
        return mSurveyCommentListener.onCommentClear();
    }
}
