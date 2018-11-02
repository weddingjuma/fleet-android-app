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

package com.mapotempo.lib.fragments.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.mapotempo.fleet.api.FleetError;
import com.mapotempo.fleet.api.ManagerFactory;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.lib.R;

/**
 * Responsible for establish a connection with the library that allow you to get access to Mapotempo models. The fragment launch a connection attempt that give you, in some cases, a manager. The manager allow you to get access to the following entities :
 * <ul>
 * <li>Missions</li>
 * <li>User</li>
 * <li>Mission Status</li>
 * <li>Company</li>
 * </ul>
 * <b>Integration</b>
 * <p>
 * <p>As a Fragment you must implement it in your XML file through the following line of code :
 * <code>
 * {@literal <fragment class="mapotempo.com.mapotempo_fleet_android.login.LoginFragment" />}
 * </code>
 * </p>
 * This fragment require the implementation of {@link OnLoginFragmentImplementation} directly in the Activity that hold the Login Fragment.
 * You will have to implement the {@link OnLoginFragmentImplementation#onLogin(FleetError, MapotempoFleetManager)} which will be called by an async task. If the library doesn't respond then, a timeout will stop the attempt and give the user back to the login page.
 * <p>
 * As you'ill need to use the manager during the whole life cycle of the application, we highly recommend to keep a reference to it in a descendant of Application.
 * </p>
 * </b>Example:</b>
 * <pre>
 * public void onLogin(MapotempoFleetManagerInterface.OnServerConnexionVerify.Status status, TimerTask task, String[] logs) {
 *      task.cancel();
 *
 *      switch (status) {
 *         case VERIFY:
 *              if (logs != null)
 *                  keepTraceOfConnectionLogsData(logs);
 *              MapotempoApplication mapotempoApplication = (MapotempoApplication) getApplicationContext();
 *              mapotempoApplication.setManager(manager);
 *              onBackPressed();
 *              finish();
 *              break;
 *          case LOGIN_ERROR:
 *              LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.hook_login_fragment);
 *              loginFragment.toogleLogginView(false);
 *              AlertMessageHelper.errorAlert(this, null, R.string.login_error_title, R.string.login_error_short_text, R.string.login_error_details);
 *              break;
 *      }
 * }
 * </pre>
 */
public class LoginFragment extends Fragment
{
    private OnLoginFragmentImplementation mListener;

    private MapotempoFleetManager iFleetManager = null;

    private EditText mLoginView;

    private EditText mPasswordView;

    private LoginPrefManager mLoginPrefManager;

    public LoginFragment()
    {
        // Required empty public constructor
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentImplementation)
        {
            mListener = (OnLoginFragmentImplementation) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme);
        if (false)
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme_Night);

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_login, container, true);

        mLoginPrefManager = new LoginPrefManager(getActivity());

        mLoginView = view.findViewById(R.id.login);
        mLoginView.setText(mLoginPrefManager.getLoginPref());

        mPasswordView = view.findViewById(R.id.password);

        if (mLoginPrefManager.getAutoLoginPref())
            mPasswordView.setText(mLoginPrefManager.getPasswordPref());

        final Button connexionButton = view.findViewById(R.id.login_sign_in_button);
        connexionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                attemptLogin();
            }
        });

        final Button signUpButton = view.findViewById(R.id.login_sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Uri uriUrl = Uri.parse(getString(R.string.mapotempo_url));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        // Auto Login configuration
        final CheckBox checkbox = view.findViewById(R.id.remember_logs);
        checkbox.setChecked(mLoginPrefManager.getAutoLoginPref());
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                mLoginPrefManager.setAutoLoginPref(isChecked);
            }
        });

        // Advanced connexion option
        ImageButton advancedConnexion = view.findViewById(R.id.advanced_connexion);
        advancedConnexion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null)
                {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                AdvancedLoginFragment newFragment = AdvancedLoginFragment.newInstance();
                newFragment.show(ft, "t");
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (mLoginPrefManager.getAutoLoginPref())
        {
            attemptLogin();
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    // ==============
    // ==  Public  ==
    // ==============

    /**
     * Toggle the login form view.
     *
     * @param active True: start spinner and kill form.
     */
    public void toogleLogginView(boolean active)
    {
        final LinearLayout progressLayout = getView().findViewById(R.id.login_progress_layout);
        final LinearLayout formLayout = getView().findViewById(R.id.login_form_layout);
        if (active)
        {
            formLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            formLayout.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
        }
    }

    public interface OnLoginFragmentImplementation
    {
        void onLogin(FleetError status,
                     MapotempoFleetManager manager);
    }

    public class InvalidLoginException extends Exception
    {
        public InvalidLoginException(String message)
        {
            super(message);
        }
    }

    // ===============
    // ==  Private  ==
    // ===============

    /**
     * Try to get connection with existing database through the library.
     * If none as been found in 5 seconds, the TimerTask restart the view.
     */
    private void attemptLogin()
    {
        final String login = mLoginView.getText().toString();
        final String password = mPasswordView.getText().toString();

        try
        {
            loginValid(login, password);
        } catch (InvalidLoginException e)
        {
            e.getStackTrace();
            return;
        }

        ManagerFactory.OnManagerReadyListener onUserAvailable = new ManagerFactory.OnManagerReadyListener()
        {
            @Override
            public void onManagerReady(final MapotempoFleetManager manager, final FleetError errorStatus)
            {
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mListener.onLogin(errorStatus, manager);
                    }
                });
            }
        };

        toogleLogginView(true);

        String fullUrl = mLoginPrefManager.getFullURL();

        ManagerFactory.getManagerAsync(getContext(), login, password, onUserAvailable, fullUrl);
        hideCurrentKeyboard();

        mLoginPrefManager.setLoginPasswordPref(login, password);
    }

    /**
     * Private method called when the virtual android keyboard needs to be hidden.
     */
    private void hideCurrentKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null)
        {
            view = new View(getContext());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loginValid(String login, String password) throws InvalidLoginException
    {
        if ((login == null || password == null) || (login.isEmpty() || password.isEmpty()))
        {
            throw new InvalidLoginException("Connection login invalid");
        }
    }
}

