package com.mapotempo.lib.menu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;

public class MainMenuFragment extends Fragment {

    private MainMenuFragment.OnMenuInteractionListener mListener;

    public MainMenuFragment() {
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnMenuInteractionListener) context;
        if (mListener == null)
            throw new RuntimeException("You must implement OnMenuInteractionListener Interface");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        NavigationView view = (NavigationView) inflater.inflate(R.layout.fragment_menu, container, false);

        Menu menu = view.getMenu();
        menu.findItem(R.id.sign_out).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mListener.onLogout();
                return false;
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

        void onHelp();

        void onLogout();
    }
}
