package mapotempo.com.mapotempo_fleet_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.core.model.User;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentImplementation {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view =  super.onCreateView(name, context, attrs);

        return view;
    }

    @Override
    public void onLoginFragmentImplementation(final MapotempoFleetManagerInterface manager) {
        final MapotempoApplication app = (MapotempoApplication) getApplicationContext();
        final ProgressBar spinner = (ProgressBar) findViewById(R.id.login_progress);

        if(manager.getUser() != null) {
            app.setManager(manager);
            app.setConnectionTo(true);
            manager.clearOnUserAvailable();
            onBackPressed();
        } else {
            spinner.setVisibility(View.VISIBLE);

            MapotempoFleetManagerInterface.OnUserAvailable onUserAvailable = new MapotempoFleetManagerInterface.OnUserAvailable() {
                @Override
                public void userAvailable(User user) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            app.setManager(manager);
                            app.setConnectionTo(true);
                            spinner.setBackgroundColor(View.GONE);
                            manager.clearOnUserAvailable();
                            onBackPressed();
                        }
                    });
                }
            };
            manager.setOnUserAvailable(onUserAvailable);
        }

    }
}

