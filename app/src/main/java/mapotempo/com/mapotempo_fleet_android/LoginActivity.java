package mapotempo.com.mapotempo_fleet_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.lib.login.LoginFragment;
import com.mapotempo.lib.utils.AlertMessageHelper;

import java.util.Date;
import java.util.TimerTask;

import mapotempo.com.mapotempo_fleet_android.utils.ConnectionManager;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentImplementation, ConnectionManager.OnConnectionSetup {

    private static final String sharedBaseName = "Mapotempo";

    private static final String userLoginKey = "UserLogin";

    private static final String userPasswordKey = "UserPassword";

    private static final String userDateKey = "UserDate";

    // ===================================
    // ==  Android Activity Life cycle  ==
    // ===================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // =====================================================
    // ==  ConnectionManager.OnConnectionSetup Interface  ==
    // =====================================================

    @Override
    public void onSelectedDataProviderWifi(ConnectionManager.ConnectionType connectionType, Context context) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSelectedDataProviderBoth(ConnectionManager.ConnectionType connectionType, Context context) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // =============================================================
    // ==  LoginFragment.OnLoginFragmentImplementation Interface  ==
    // =============================================================

    /**
     * An interface which is trigger when the connection has been processed.
     *
     * @param status The current status of connection
     * @param task   A task which is in charge to stop the timer if no connection has been established.
     */
    @Override
    public void onLoginFragmentImplementation(MapotempoFleetManagerInterface.OnServerConnexionVerify.Status status, TimerTask task, String[] logs, MapotempoFleetManagerInterface manager) {
        task.cancel();
        switch (status) {
            case VERIFY:
                if (logs != null)
                    keepTraceOfConnectionLogsData(logs);

                MapotempoApplication mapotempoApplication = (MapotempoApplication) getApplicationContext();
                mapotempoApplication.setManager(manager);
                ConnectionManager.getInstance().askUserPreference(this, R.layout.user_data_pref);
                break;
            case LOGIN_ERROR:
                LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.hook_login_fragment);
                loginFragment.toogleLogginView(false);
                AlertMessageHelper.errorAlert(this, null, R.string.login_error_title, R.string.login_error_short_text, R.string.login_error_details);
                break;
            default:
                break;
        }
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void keepTraceOfConnectionLogsData(String[] logs) {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedBaseName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userLoginKey, logs[0]);
        editor.putString(userPasswordKey, logs[1]);
        editor.putString(userDateKey, (new Date()).toString());
        editor.apply(); // Apply is async, while commit isn't.
    }
}

