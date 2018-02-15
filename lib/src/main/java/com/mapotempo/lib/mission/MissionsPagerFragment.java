package com.mapotempo.lib.mission;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;

import java.util.ArrayList;
import java.util.List;

import mapotempo.com.lib.R;


/**
 * This fragment act as a manger for the {@link MissionDetailsFragment}.
 * It is used to :
 * <ul>
 * <li>Detect if a ViewPager must be used</li>
 * <li>Return the current view displayed</li>
 * <li>Provide the {@link MissionInterface} object to {@link MissionDetailsFragment}</li>
 * </ul>
 * <p>
 * <h3>Integration</h3>
 * First and foremost, it is needed to implement the fragment through XML using the following class :
 * {@literal <fragment class="mapotempo.com.mapotempo_fleet_android.mission.MissionsPagerFragment"} <br>
 * {@literal android:id="@+id/base_fragment"} <br>
 * {@literal app:ViewStyle="SCROLLVIEW"} <br>
 * {@literal android:layout_width="match_parent"} <br>
 * {@literal android:layout_height="match_parent" />} <br>
 * <p>
 * It is Highly recommended to set up the enum "ViewStyle" to "SCROLLVIEW" in order to benefit of full features.
 * <p>
 * This fragment require the implementation of {@link OnMissionFocusListener} directly in the Activity that hold the List Fragment.
 * Then Override the {@link OnMissionFocusListener#onMissionFocus(int)} is required to use this fragment.
 * </p>
 * <p>
 * <b>Here is an example of usability: </b>
 * <pre>
 * @Override
 * public int onMissionFocus(int position) {
 *      MissionsListFragment missionsFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
 *
 *      if (missionsFragment != null)
 *      missionsFragment.setMissionFocus(position);
 *
 *      return position;
 *  }
 * </pre>
 */
public class MissionsPagerFragment extends Fragment {
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private OnMissionFocusListener mListener;

    public MissionsPagerFragment() {
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnMissionFocusListener) context;
        if (mListener == null)
            throw new RuntimeException("You must implement OnMissionFocusListener Interface");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        List<MissionInterface> missions = new ArrayList<>();
        if (mapotempoApplication.getManager() != null)
            missions = mapotempoApplication.getManager().getMissionAccess().getAll();

        View view = inflater.inflate(R.layout.fragment_pager, container, false);
        LinearLayout content = view.findViewById(R.id.mission_view_content);

        mPagerAdapter = new MissionPagerAdapter(getFragmentManager(), missions.size(), missions);
        ViewPager viewPager = (ViewPager) getActivity().getLayoutInflater().inflate(R.layout.view_pager, null);
        mViewPager = viewPager.findViewById(R.id.mission_viewpager);

        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        setPagerChangeListener();
        content.addView(viewPager);
        mViewPager.setAdapter(mPagerAdapter);
        setListenerForButtons(view);
        setNextAndPreviousVisibility(view);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void notifyDataChange() {
        mPagerAdapter.notifyDataSetChanged();
    }

    public void refreshPagerData(List<MissionInterface> missions) {
        MissionPagerAdapter missionPagerAdapter = (MissionPagerAdapter) mPagerAdapter;

        if (missionPagerAdapter != null)
            missionPagerAdapter.updateMissions(missions);
    }

    public void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position, true);
    }

    public interface OnMissionFocusListener {
        void onMissionFocus(int page);
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void setPagerChangeListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mListener.onMissionFocus(position);
                setNextAndPreviousVisibility(getView());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        final int position = getActivity().getIntent().getIntExtra("mission_id", 0);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(position);
            }
        });
    }

    private void setNextAndPreviousVisibility(View view) {
        int currentItem = mViewPager.getCurrentItem();
        final Button next = view.findViewById(R.id.next_nav);
        final Button prev = view.findViewById(R.id.previous_nav);

        next.setVisibility(View.VISIBLE);
        prev.setVisibility(View.VISIBLE);

        if (currentItem == mPagerAdapter.getCount() - 1) {
            next.setVisibility(View.INVISIBLE);
        } else if (currentItem == 0) {
            prev.setVisibility(View.INVISIBLE);
        }
    }

    private void setListenerForButtons(View view) {
        final Button prev = view.findViewById(R.id.previous_nav);
        final Button next = view.findViewById(R.id.next_nav);

        if (prev != null && next != null) {
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentItem = mViewPager.getCurrentItem();
                    mViewPager.setCurrentItem(currentItem - 1);
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentItem = mViewPager.getCurrentItem();
                    mViewPager.setCurrentItem(currentItem + 1);
                }
            });
        } else {
            throw new RuntimeException("Button aren't settled in view");
        }
    }
}
