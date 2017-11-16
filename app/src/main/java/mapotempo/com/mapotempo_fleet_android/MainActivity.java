package mapotempo.com.mapotempo_fleet_android;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.mapotempo.fleet.api.model.submodel.LocationDetailsInterface;
import com.mapotempo.lib.mission.MissionsListFragment;
import com.mapotempo.lib.mission.MissionsPagerFragment;

import java.util.ArrayList;
import java.util.Date;

import mapotempo.com.mapotempo_fleet_android.other.DrawerOnClickListener;
import mapotempo.com.mapotempo_fleet_android.utils.DrawerListAdapter;
import mapotempo.com.mapotempo_fleet_android.utils.ListItemCustom;

public class MainActivity extends AppCompatActivity implements MissionsListFragment.OnMissionSelectedListener,
        MissionsPagerFragment.OnMissionFocusListener,
        LocationListener {

    private DrawerLayout mDrawerLayout;

    // ===================================
    // ==  Android Activity Life cycle  ==
    // ===================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        addDrawableHandler(toolbar);
    }

    @Override
    @SuppressWarnings("MissingPermission")
    protected void onStart() {
        super.onStart();
        LocationManager locMngr = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        //LocationManager locMngr = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        //if (locMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        if (locMngr.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Log.i("GPS_PROVIDER", "OKKKKKKKKKK");
        else
            Log.i("GPS_PROVIDER", "FAIIIIIIIIIL");
        locMngr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
        refreshMissionListFragment();
        refreshMissionsPagerFragment();
    }

    // ========================================
    // ==  OnMissionFocusListener Interface  ==
    // ========================================

    @Override
    public void onMissionFocus(int position) {
        MissionsListFragment missionsListFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
        if (missionsListFragment != null)
            missionsListFragment.setCurrentMission(position);
    }

    // ===========================================
    // ==  OnMissionSelectedListener Interface  ==
    // ===========================================

    @Override
    public void onMissionSelected(final int position) {
        MissionsPagerFragment missionsPagerFragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
        if (missionsPagerFragment != null && missionsPagerFragment.isVisible()) {
            MissionsPagerFragment fragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
            fragment.setCurrentItem(position);
        } else {
            Intent intent = new Intent(this, MissionActivity.class);
            intent.putExtra("mission_id", position);
            startActivity(intent);
        }
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void refreshMissionListFragment() {
        MissionsListFragment missionsListFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
        if (missionsListFragment != null && missionsListFragment.isVisible())
            missionsListFragment.mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void refreshMissionsPagerFragment() {
        MissionsPagerFragment missionsPagerFragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
        if (missionsPagerFragment != null && missionsPagerFragment.isVisible())
            missionsPagerFragment.notifyDataChange();
    }

    private void addDrawableHandler(Toolbar toolbar) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {

                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                    //drawerOpened = false;
                }

                public void onDrawerOpened(View drawerView) {
                    supportInvalidateOptionsMenu();
                    //drawerOpened = true;
                }
            };

            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();

            customerDrawerList();
        }
    }

    private void customerDrawerList() {
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        ListItemCustom[] drawerElements = new ListItemCustom[]{
                new ListItemCustom(R.drawable.ic_login_black_24dp, "Connection", getResources().getColor(R.color.colorDeepBlue)),
                new ListItemCustom(R.drawable.ic_about_black_24dp, "About", getResources().getColor(R.color.colorOrange)),
        };

        mDrawerList.setAdapter(new DrawerListAdapter(this, R.layout.drawer_layout_item_row, drawerElements));
        mDrawerList.setOnItemClickListener(new DrawerOnClickListener());
        mDrawerList.addHeaderView(getLayoutInflater().inflate(R.layout.drawer_layout_header, null));
    }

    // ###################################
    // ##                               ##
    // ##    LOCATION PROVIDER TEST     ##
    // ##                               ##
    // ###################################
    ArrayList<LocationDetailsInterface> mLocationPool = new ArrayList<LocationDetailsInterface>();

    @Override
    @SuppressWarnings("MissingPermission")
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "" + location.getLongitude() + location.getLatitude());
        MapotempoApplication app = (MapotempoApplication) getApplicationContext();
        if (app.getManager() != null) {
            Integer cid = -1;
            Integer lac = -1;
            Integer mcc = -1;
            Integer mnc = -1;

            Log.d("onLocationChanged", "up mapotempo");
            TelephonyManager telephonyManager = (TelephonyManager) (getSystemService((Context.TELEPHONY_SERVICE)));
            GsmCellLocation gl = (GsmCellLocation) telephonyManager.getCellLocation();
            String networkOperator = telephonyManager.getNetworkOperator();

            if (gl != null) {
                cid = gl.getCid();
                lac = gl.getLac();
                //Log.i("loc info", "psc : " + cl.getPsc());
            }
            if (networkOperator != null) {
                System.out.println(">>>>>>>>>>>" + networkOperator);
                //mcc = Integer.parseInt(networkOperator.substring(0, 3));
                //mnc = Integer.parseInt(networkOperator.substring(3));
            }

            LocationDetailsInterface ld = app.getManager().getSubmodelFactory().CreateNewLocationDetails(
                    location.getLatitude(),
                    location.getLongitude(),
                    new Date(),
                    location.getAccuracy(),
                    location.getSpeed(),
                    location.getBearing()
                    , location.getAltitude(),
                    0,
                    cid,
                    lac,
                    mcc,
                    mnc);
            app.getManager().setCurrentLocationDetails(ld);

            System.out.println("---------------------UP LOCATION---------------");
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i("onStatusChanged", s);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i("onProviderEnabled", s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i("onProviderDisabled", s);
    }
}


