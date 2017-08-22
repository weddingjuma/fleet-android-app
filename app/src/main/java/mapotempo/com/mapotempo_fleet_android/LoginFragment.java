package mapotempo.com.mapotempo_fleet_android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.couchbase.lite.android.AndroidContext;
import com.mapotempo.fleet.MapotempoFleetManager;
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;

public class LoginFragment extends Fragment {
    private OnLoginFragmentImplementation mListener;
    private String dataBaseUrl = "http://192.168.1.108:4984/db";

    private String mLogin = null;
    private String mPassword = null;
    private MapotempoFleetManagerInterface iFleetManager = null;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        addButtonSubmit(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentImplementation) {
            mListener = (OnLoginFragmentImplementation) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void addButtonSubmit(View view) {
        final EditText mPasswordView = (EditText) view.findViewById(R.id.password);
        final EditText mLoginView = (EditText) view.findViewById(R.id.login);

        (view.findViewById(R.id.login_sign_in_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLogin = mLoginView.getText().toString().toLowerCase();
                mPassword = mPasswordView.getText().toString().toLowerCase();

                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        try {
            loginValid(mLogin, mPassword);
        } catch (InvalidLoginException e) {
            e.getStackTrace();
            return;
        }

        iFleetManager = MapotempoFleetManager.getManager(new AndroidContext(getContext().getApplicationContext()),  mLogin, mPassword, dataBaseUrl);
        mListener.onLoginFragmentImplementation(iFleetManager);
    }

    private void loginValid(String login, String password) throws InvalidLoginException {
        if ((login == null || password == null) || (login.isEmpty() || password.isEmpty())) {
            throw new InvalidLoginException("Connection login invalid");
        }
    }

    public interface OnLoginFragmentImplementation {
        void onLoginFragmentImplementation(MapotempoFleetManagerInterface manager);
    }

    public class InvalidLoginException extends Exception {
        public InvalidLoginException(String message) {
            super(message);
        }
    }
}
