package mapotempo.com.mapotempo_fleet_android;

import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.DrawerLayout;
import android.content.res.Configuration;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mapotempo.fleet.core.model.Mission;

import mapotempo.com.mapotempo_fleet_android.utils.DrawerListAdapter;
import mapotempo.com.mapotempo_fleet_android.utils.ListItemCustom;

public class MainActivity extends AppCompatActivity implements MissionsFragment.OnMissionsInteractionListener, MissionFragment.OnFragmentInteractionListener, MissionContainerFragment.ContainerFragmentMission {

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
        ListItemCustom[] drawerElements = new ListItemCustom[] {
                new ListItemCustom(R.drawable.ic_login_icon_24dp, "Connection", getResources().getColor(R.color.colorDeepBlue)),
                new ListItemCustom(R.drawable.ic_about_black_24dp, "About", getResources().getColor(R.color.colorOrange)),
        };

        mDrawerList.setAdapter(new DrawerListAdapter(this, R.layout.drawer_layout_item_row, drawerElements));
        mDrawerList.setOnItemClickListener(new DrawerOnClickListener());
        mDrawerList.addHeaderView(getLayoutInflater().inflate(R.layout.drawer_layout_header, null));
    }

    @Override
    public View.OnClickListener onListMissionsInteraction(final int position) {
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    MissionContainerFragment fragment = (MissionContainerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);
                    fragment.setCurrentItem(position);
                } else {
                    Intent intent = new Intent(v.getContext(), SingleMissionView.class);
                    intent.putExtra("mission_id", position);

                    v.getContext().startActivity(intent);
                }
            }
        };

        return onClick;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onSingleMissionInteraction(Mission mission) {
        MissionsFragment missionsFragment = (MissionsFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
        MissionContainerFragment fragment = (MissionContainerFragment) getSupportFragmentManager().findFragmentById(R.id.base_fragment);

        if (missionsFragment != null)
            missionsFragment.recyclerView.getAdapter().notifyDataSetChanged();

        if (fragment != null)
            fragment.notifyDataChange();
    }

    @Override
    public int wichViewIsTheCurrent(int position) {
        MissionsFragment missionsFragment = (MissionsFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);

        if (missionsFragment != null)
            missionsFragment.setCurrentMission(position);

        return position;
    }
}


