package mapotempo.com.mapotempo_fleet_android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.mapotempo.fleet.api.model.submodel.LocationDetailsInterface;

import java.util.ArrayList;

import mapotempo.com.mapotempo_fleet_android.mission.MissionsListFragment;
import mapotempo.com.mapotempo_fleet_android.mission.MissionsPagerFragment;
import mapotempo.com.mapotempo_fleet_android.other.DrawerOnClickListener;
import mapotempo.com.mapotempo_fleet_android.utils.DrawerListAdapter;
import mapotempo.com.mapotempo_fleet_android.utils.ListItemCustom;

public class MainActivity extends AppCompatActivity implements MissionsListFragment.OnMissionSelectedListener,
        MissionsPagerFragment.OnMissionFocusListener,
        LocationListener {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        addDrawableHandler(toolbar);
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
                new ListItemCustom(R.drawable.ic_login_icon_24dp, "Connection", getResources().getColor(R.color.colorDeepBlue)),
                new ListItemCustom(R.drawable.ic_about_black_24dp, "About", getResources().getColor(R.color.colorOrange)),
        };

        mDrawerList.setAdapter(new DrawerListAdapter(this, R.layout.drawer_layout_item_row, drawerElements));
        mDrawerList.setOnItemClickListener(new DrawerOnClickListener());
        mDrawerList.addHeaderView(getLayoutInflater().inflate(R.layout.drawer_layout_header, null));
    }

    @Override
    @SuppressWarnings("MissingPermission")
    protected void onStart() {
        super.onStart();
        LocationManager locMngr = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
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

    @Override
    public void onMissionFocus(int position) {
        MissionsListFragment missionsListFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
        if (missionsListFragment != null)
            missionsListFragment.setCurrentMission(position);
    }

    @Override
    public void onMissionSelected(final int position) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MissionsPagerFragment fragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
            fragment.setCurrentItem(position);
        } else {
            Intent intent = new Intent(this, MissionActivity.class);
            intent.putExtra("mission_id", position);
            startActivity(intent);
        }
    }

    private void refreshMissionListFragment() {
        MissionsListFragment missionsListFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
        if (missionsListFragment != null && missionsListFragment.isVisible())
            missionsListFragment.recyclerView.getAdapter().notifyDataSetChanged();
    }

    private void refreshMissionsPagerFragment() {
        MissionsPagerFragment missionsPagerFragment = (MissionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
        if (missionsPagerFragment != null && missionsPagerFragment.isVisible())
            missionsPagerFragment.notifyDataChange();
    }

    // ###################################
    // ##                               ##
    // ##    LOCATION PROVIDER TEST     ##
    // ##                               ##
    // ###################################
    ArrayList<LocationDetailsInterface> mLocationPool = new ArrayList<LocationDetailsInterface>();

    @Override
    public void onLocationChanged(Location location) {/*
        Log.i("onLocationChanged", "" + location.getLongitude() + location.getLatitude());
        MapotempoApplication app = (MapotempoApplication) getApplicationContext();
        CurrentLocationInterface cl = app.getManager().getCurrentLocation();
        if(cl != null) {
            Log.i("onLocationChanged", "up mapotempo");
            TelephonyManager telephonyManager = (TelephonyManager)(getSystemService((Context.TELEPHONY_SERVICE)));
            GsmCellLocation gl =  (GsmCellLocation) telephonyManager.getCellLocation();
            String networkOperator =  telephonyManager.getNetworkOperator();

            String cid = "cid";//Integer.toString(gl.getCid());
            String lac = "lac";//Integer.toString(gl.getLac());
            //Log.i("loc info", "psc : " + cl.getPsc());
            String mcc = "mcc";//networkOperator.substring(0, 3);
            String mnc = "mnc";//networkOperator.substring(3);
            LocationDetailsInterface ld = app.getManager().getSubmodelFactory().CreateNewLocationDetails(location.getLatitude(), location.getLongitude(), new Date(),
                location.getAccuracy(), location.getSpeed(), location.getBearing(), location.getAltitude(), 0, cid, lac, mcc, mnc);
            cl.setLocation(ld);
            cl.save();
        }
*/
        /*
        Log.i("onLocationChanged", "" + location.getLongitude() + location.getLatitude());
        MapotempoApplication app = (MapotempoApplication) getApplicationContext();
        TelephonyManager telephonyManager = (TelephonyManager)(getSystemService((Context.TELEPHONY_SERVICE)));
        GsmCellLocation cl =  (GsmCellLocation) telephonyManager.getCellLocation();
        String networkOperator =  telephonyManager.getNetworkOperator();

        String cid = Integer.toString(cl.getCid());
        String lac = Integer.toString(cl.getLac());
        //Log.i("loc info", "psc : " + cl.getPsc());
        String mcc = networkOperator.substring(0, 3);
        String mnc = networkOperator.substring(3);
        LocationDetailsInterface ld = app.getManager().getSubmodelFactory().CreateNewLocationDetails(location.getLatitude(), location.getLongitude(), new Date(),
            location.getAccuracy(), location.getSpeed(), location.getBearing(), location.getAltitude(), 0, cid, lac, mcc, mnc);
        mLocationPool.add(ld);

        Log.i("onLocationChanged", "" +mLocationPool.size());

        if(mLocationPool.size() >= 20) {
            Log.i("onLocationChanged", "emit");
            TrackAccessInterface ta = app.getManager().getTrackAccess();
            TrackInterface ti = ta.getNew();
            ti.setLocations(mLocationPool);
            ti.setCompanyId("company_mapotempo");
            ti.setOwnerId("static");
            ti.setDate(new Date());
            ti.save();
            mLocationPool = new ArrayList<LocationDetailsInterface>();
        }*/
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


