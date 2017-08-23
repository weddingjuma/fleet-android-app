package mapotempo.com.mapotempo_fleet_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.core.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import mapotempo.com.mapotempo_fleet_android.utils.AlertMessageHelper;

import static android.Manifest.permission.READ_CONTACTS;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentImplementation {

    private static final String sharedBaseName = "Mapotempo";
    private static final String userLoginKey = "UserLogin";
    private static final String userPasswordKey = "UserPassword";
    private static final String userDateKey = "UserDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    /**
     * An interface which is trigger when the connection has been processed.
     * @param status The current status of connection
     * @param task A task which is in charge to stop the timer if no connection has been established.
     */
    @Override
    public void onLoginFragmentImplementation(MapotempoFleetManagerInterface.OnServerConnexionVerify.Status status, TimerTask task, String[] logs) {
        task.cancel();

        switch (status) {
            case VERIFY:
                if (logs != null)
                    keepTraceOfConnectionLogsData(logs);

                onBackPressed();
                break;
            case USER_ERROR:
            case PASSWORD_ERROR:
                LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.hook_login_fragment);
                loginFragment.toogleLogginView(false);
                break;
            case TIMEOUT:
                // NOT SUPPORTED YET
                Log.w("NOT SUPPORTED", "TIMEOUT from callback onLogin is not yet implemented");
                break;
            default:
                onBackPressed();
                break;
        }
    }

    private void keepTraceOfConnectionLogsData(String[] logs) {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedBaseName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(userLoginKey, logs[0]);
        editor.putString(userPasswordKey, logs[1]);
        editor.putString(userDateKey, (new Date()).toString());

        editor.apply(); // Apply is async, while commit isn't.
    }
}

