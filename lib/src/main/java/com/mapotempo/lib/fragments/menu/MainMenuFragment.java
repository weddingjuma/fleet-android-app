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

package com.mapotempo.lib.fragments.menu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;
import com.mapotempo.lib.fragments.login.LoginPrefManager;

public class MainMenuFragment extends MapotempoBaseFragment
{

    private NavigationView mNavigationView;

    private MainMenuFragment.OnMenuInteractionListener mListener;

    private TextView mUserName;

    private TextView mUserEmail;

    private Menu mMenu;

    private MenuItem mLogoutItem;

    private SwitchCompat mSwitchTracking;

    public MainMenuFragment()
    {
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mListener = (OnMenuInteractionListener) context;
        if (mListener == null)
            throw new RuntimeException("You must implement OnMenuInteractionListener Interface");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        mNavigationView = (NavigationView) inflater.inflate(R.layout.fragment_menu, container, false);

        View header = mNavigationView.getHeaderView(0);
        mUserName = header.findViewById(R.id.user_name_header);
        mUserEmail = header.findViewById(R.id.user_email_header);
        mUserName.setText(mapotempoApplication.getManager().getUser().getName());
        mUserEmail.setText(mapotempoApplication.getManager().getUser().getEmail());

        mMenu = mNavigationView.getMenu();
        mLogoutItem = mMenu.findItem(R.id.sign_out);
        mLogoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                LoginPrefManager loginPrefManager = new LoginPrefManager(getActivity());
                loginPrefManager.setAutoLoginPref(false);
                mListener.onLogout();
                return false;
            }
        });


        mMenu.findItem(R.id.main).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                mListener.onMain();
                return true;
            }
        });

        mMenu.findItem(R.id.archived).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                mListener.onArchived();
                return true;
            }
        });

        mMenu.findItem(R.id.map).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                mListener.onMap();
                return true;
            }
        });

        mMenu.findItem(R.id.setting).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                mNavigationView.setCheckedItem(R.id.setting);
                mListener.onSettings();
                return true;
            }
        });

        mMenu.findItem(R.id.help).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Uri uriUrl = Uri.parse(getString(R.string.mapotempo_help_url));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
                return true;
            }
        });

        mNavigationView.setCheckedItem(R.id.setting);
        return mNavigationView;
    }

    // ==============
    // ==  Public  ==
    // ==============

    public interface OnMenuInteractionListener
    {
        void onMain();

        void onArchived();

        void onMap();

        void onSettings();

        boolean onTracking(boolean tracking_status);

        void onLogout();

    }
}

