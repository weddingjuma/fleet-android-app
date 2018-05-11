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

package com.mapotempo.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.app.LoginActivity;
import com.mapotempo.lib.exception.MapotempoManagerMissingException;

public abstract class MapotempoBaseActivity extends AppCompatActivity {

    final MapotempoBaseActivity INSTANCE = this;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        try {
            super.onCreate(savedInstanceState, persistentState);
        } catch (InflateException e) {
            // Catch only MapotempoManagerMissingException exception
            if (e.getCause() instanceof MapotempoManagerMissingException) {
                e.printStackTrace();
                intentLoginActivity();
            } else {
                throw e;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (InflateException e) {
            // Catch only MapotempoManagerMissingException exception
            if (e.getCause() instanceof MapotempoManagerMissingException) {
                e.printStackTrace();
                intentLoginActivity();
            } else {
                throw e;
            }
        }
    }


    @Override
    public void setContentView(View view) {
        try {
            super.setContentView(view);
        } catch (InflateException e) {
            // Catch only MapotempoManagerMissingException exception
            if (e.getCause() instanceof MapotempoManagerMissingException) {
                e.printStackTrace();
                intentLoginActivity();
            } else {
                throw e;
            }
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        try {
            super.setContentView(view, params);
        } catch (InflateException e) {
            // Catch only MapotempoManagerMissingException exception
            if (e.getCause() instanceof MapotempoManagerMissingException) {
                e.printStackTrace();
                intentLoginActivity();
            } else {
                throw e;
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        try {
            super.setContentView(layoutResID);
        } catch (InflateException e) {
            // Catch only MapotempoManagerMissingException exception
            if (e.getCause() instanceof MapotempoManagerMissingException) {
                e.printStackTrace();
                intentLoginActivity();
            } else {
                throw e;
            }
        }
    }

    private void intentLoginActivity() {
        Intent intent = new Intent(INSTANCE, LoginActivity.class);
        startActivity(intent);
        finish();
        System.exit(2);
    }
}
