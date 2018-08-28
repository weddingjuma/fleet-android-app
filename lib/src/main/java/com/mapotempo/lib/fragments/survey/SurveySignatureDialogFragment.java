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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.mapotempo.lib.R;

public class SurveySignatureDialogFragment extends SurveyBaseDialogFragment
{
    public static SurveySignatureDialogFragment newInstance(@Nullable Bitmap bitmapInit, @Nullable String signatoryNameInit)
    {
        SurveySignatureDialogFragment f = new SurveySignatureDialogFragment();
        f.bitmapInit = bitmapInit;
        f.mSignatoryNameInit = signatoryNameInit;
        return f;
    }

    public interface SurveySignatureListener
    {
        boolean onSignatureSave(Bitmap signatureBitmap, String signatory_name);

        boolean onSignatureClear();
    }

    public void setSurveySignatureListener(SurveySignatureListener surveySignatureListener)
    {
        mSurveySignatureListener = surveySignatureListener;
    }

    private Bitmap bitmapInit = null;

    private String mSignatoryNameInit = "";

    private SignaturePad mSignaturePad;

    private TextView mSignatureMessage;

    private TextInputEditText mSignatoryName;

    private SurveySignatureListener mSurveySignatureListener = new SurveySignatureListener()
    {
        @Override
        public boolean onSignatureSave(Bitmap signatureBitmap, String signatory_name)
        {
            return true;
        }

        @Override
        public boolean onSignatureClear()
        {
            return true;
        }
    };

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.survey_fragment_signature, container, false);
        mSignaturePad = v.findViewById(R.id.signature_pad);
        mSignatureMessage = v.findViewById(R.id.signature_message);
        mSignatoryName = v.findViewById(R.id.signatory_name);

        if (bitmapInit != null)
        {
            mSignaturePad.setSignatureBitmap(bitmapInit);
            mSignatureMessage.setVisibility(View.GONE);
        }

        if (mSignatoryNameInit != null)
            mSignatoryName.setText(mSignatoryNameInit);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener()
        {
            @Override
            public void onStartSigning()
            {
                mSignatureMessage.setVisibility(View.GONE);
            }

            @Override
            public void onSigned()
            {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear()
            {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });
        return v;
    }

    // ==========================
    // ==  SurveyBaseDialogFragment  ==
    // ==========================

    @Override
    protected boolean onSave()
    {
        Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
        String signatoryName = mSignatoryName.getText()
            .toString()
            .trim();
        if (mSurveySignatureListener.onSignatureSave(signatureBitmap, signatoryName))
            return true;
        return false;
    }

    @Override
    protected boolean onClear()
    {
        if (!mSurveySignatureListener.onSignatureClear())
            return false;
        mSignaturePad.clear();
        mSignatureMessage.setVisibility(View.VISIBLE);
        return true;

    }
}
