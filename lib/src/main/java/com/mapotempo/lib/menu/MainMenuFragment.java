package com.mapotempo.lib.menu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.login.LoginPrefManager;

public class MainMenuFragment extends Fragment {

    private MainMenuFragment.OnMenuInteractionListener mListener;

    TextView mUserName;

    TextView mUserEmail;

    Menu mMenu;

    MenuItem mLogoutItem;

    SwitchCompat mSwitchTracking;

    public MainMenuFragment() {
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnMenuInteractionListener) context;
        if (mListener == null)
            throw new RuntimeException("You must implement OnMenuInteractionListener Interface");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        NavigationView view = (NavigationView) inflater.inflate(R.layout.fragment_menu, container, false);

        View header = view.getHeaderView(0);
        mUserName = header.findViewById(R.id.user_name_header);
        mUserEmail = header.findViewById(R.id.user_email_header);
        mUserName.setText(mapotempoApplication.getManager().getUser().getUserName());
        mUserEmail.setText(mapotempoApplication.getManager().getUser().getUserEmail());

        mMenu = view.getMenu();
        mLogoutItem = mMenu.findItem(R.id.sign_out);
        mLogoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                LoginPrefManager loginPrefManager = new LoginPrefManager(getActivity());
                loginPrefManager.setAutoLoginPref(false);
                mListener.onLogout();
                return false;
            }
        });

        mMenu.findItem(R.id.map).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mListener.onMap();
                return true;
            }
        });

        mMenu.findItem(R.id.setting).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mListener.onSettings();
                return true;
            }
        });

        mMenu.findItem(R.id.help).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Uri uriUrl = Uri.parse(getString(R.string.mapotempo_help_url));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
                return true;
            }
        });
        return view;
    }

    // ==============
    // ==  Public  ==
    // ==============

    public interface OnMenuInteractionListener {
        void onMap();

        void onSettings();

        boolean onTracking(boolean tracking_status);

        void onLogout();

    }
}

