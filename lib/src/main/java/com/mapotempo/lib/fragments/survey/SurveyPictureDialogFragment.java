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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mapotempo.lib.R;

public class
SurveyPictureDialogFragment extends SurveyBaseDialogFragment
{
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static SurveyPictureDialogFragment newInstance(@Nullable Bitmap init)
    {
        SurveyPictureDialogFragment f = new SurveyPictureDialogFragment();
        f.mBitmap = init;
        return f;
    }

    public interface SurveyPictureListener
    {
        boolean onPictureSave(Bitmap signatureBitmap);

        boolean onPictureClear();
    }


    private Bitmap mBitmap = null;

    private ImageView mPicturePad;

    private SurveyPictureListener mSurveyPictureListener = new SurveyPictureListener()
    {
        @Override
        public boolean onPictureSave(Bitmap signatureBitmap)
        {
            return true;
        }

        @Override
        public boolean onPictureClear()
        {
            return true;
        }
    };

    public void setSurveyPictureListener(SurveyPictureListener surveyPictureListener)
    {
        mSurveyPictureListener = surveyPictureListener;
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.survey_fragment_picture, container, false);
        mPicturePad = v.findViewById(R.id.picture_pad);
        refreshView();
        return v;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (mBitmap == null)
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null)
            {
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            setBitmap(imageBitmap);
            mPositiveButton.setEnabled(true);
        }
    }

    // ================================
    // ==  SurveyBaseDialogFragment  ==
    // ================================

    @Override
    protected boolean onPositive()
    {
        return mSurveyPictureListener.onPictureSave(mBitmap);
    }

    @Override
    protected boolean onClear()
    {
        setBitmap(null);
        return mSurveyPictureListener.onPictureClear();
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void setBitmap(Bitmap bitmap)
    {
        mBitmap = bitmap;
        refreshView();
    }

    private void refreshView()
    {
        if (mBitmap != null)
        {
            mPicturePad.setImageBitmap(mBitmap);
        }
        else
        {
            mPicturePad.setImageDrawable(null);
        }
    }
}
