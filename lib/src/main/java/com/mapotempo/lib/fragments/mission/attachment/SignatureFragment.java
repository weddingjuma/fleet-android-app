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

package com.mapotempo.lib.fragments.mission.attachment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseDialogFragment;

public class SignatureFragment extends MapotempoBaseDialogFragment
{

    public static SignatureFragment newInstance(@Nullable Bitmap bitmapInit, @Nullable String signatoryNameInit)
    {
        SignatureFragment f = new SignatureFragment();
        f.bitmapInit = bitmapInit;
        f.mSignatoryNameInit = signatoryNameInit;
        return f;
    }

    public interface SignatureListener
    {
        boolean onSignatureSave(Bitmap signatureBitmap, String signatory_name);

        boolean onSignatureClear();
    }

    public void setSignatureListener(SignatureListener signatureListener)
    {
        mSignatureListener = signatureListener;
    }

    private Bitmap bitmapInit = null;

    private String mSignatoryNameInit = "";

    private SignaturePad mSignaturePad;

    private Button mClearButton;

    private Button mSaveButton;

    private TextView mSignatureMessage;

    private TextInputEditText mSignatoryName;

    private SignatureListener mSignatureListener = new SignatureListener()
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
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_signature, container, false);
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

        mClearButton = v.findViewById(R.id.clear_button);
        mSaveButton = v.findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mSignatureListener.onSignatureClear())
                {
                    mSignaturePad.clear();
                    mSignatureMessage.setVisibility(View.VISIBLE);
                    dismiss();
                }
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                String signatoryName = mSignatoryName.getText()
                    .toString()
                    .trim();
                if (mSignatureListener.onSignatureSave(signatureBitmap, signatoryName))
                    dismiss();
            }
        });
        return v;
    }
}
