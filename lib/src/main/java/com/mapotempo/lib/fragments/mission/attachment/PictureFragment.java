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
import android.widget.Button;
import android.widget.ImageView;

import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseDialogFragment;

public class PictureFragment extends MapotempoBaseDialogFragment
{
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static PictureFragment newInstance(@Nullable Bitmap init)
    {
        PictureFragment f = new PictureFragment();
        f.mBitmap = init;
        return f;
    }

    public interface PictureListener
    {
        boolean onPictureSave(Bitmap signatureBitmap);

        boolean onPictureClear();
    }


    private Bitmap mBitmap = null;

    private ImageView mPicturePad;

    private Button mClearButton;

    private Button mSaveButton;

    private PictureListener mPictureListener = new PictureListener()
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

    public void setPictureListener(PictureListener pictureListener)
    {
        mPictureListener = pictureListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_picture, container, false);
        mPicturePad = v.findViewById(R.id.picture_pad);

        mClearButton = v.findViewById(R.id.clear_button);
        mSaveButton = v.findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setBitmap(null);
                if (mPictureListener.onPictureClear())
                {
                    dismiss();
                }
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mPictureListener.onPictureSave(mBitmap))
                {
                    dismiss();
                }
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            setBitmap(imageBitmap);
            mSaveButton.setEnabled(true);
        }
    }
}
