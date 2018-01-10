package com.mapotempo.lib.login;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.mapotempo.lib.R;

public class AdvancedLoginFragment extends DialogFragment {

    Button mDefaultButton;

    Button mValidateButton;

    EditText mEditPort;

    EditText mEditUrl;

    public static AdvancedLoginFragment newInstance() {
        AdvancedLoginFragment f = new AdvancedLoginFragment();
        return f;
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginPref.SHARED_BASE_NAME, 0);

        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme);
        if (false) // TODO
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme_Night);

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View v = localInflater.inflate(R.layout.fragment_advanced_login, container, false);

        String dataBaseUrl = sharedPreferences.getString(LoginPref.URL_CONFIGURATION, null) != null ? sharedPreferences.getString(LoginPref.URL_CONFIGURATION, null) : getResources().getString(R.string.default_syncgateway_url);
        mEditUrl = v.findViewById(R.id.edit_url);
        mEditUrl.setText(dataBaseUrl);

        String dataBasePort = sharedPreferences.getString(LoginPref.PORT_CONFIGURATION, null) != null ? sharedPreferences.getString(LoginPref.PORT_CONFIGURATION, null) : getResources().getString(R.string.default_syncgateway_port);
        mEditPort = v.findViewById(R.id.edit_port);
        mEditPort.setText(dataBasePort);

        mDefaultButton = v.findViewById(R.id.default_button);
        mDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment.serverLoginConfiguration(getActivity(), null, null, null);
                getDialog().dismiss();
            }
        });

        mValidateButton = v.findViewById(R.id.validate_button);
        mValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment.serverLoginConfiguration(getActivity(), mEditUrl.getText().toString(), mEditPort.getText().toString(), "db");
                getDialog().dismiss();
            }
        });

        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
