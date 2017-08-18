package mapotempo.com.mapotempo_fleet_android;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.accessor.MissionAccessInterface;
import com.mapotempo.fleet.core.model.Mission;

import java.util.List;


/**
 * create an instance of this fragment.
 */
public class MissionContainerFragment extends Fragment {
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private MissionFragment fMission;
    private List<Mission> mMissions;
    private MapotempoFleetManagerInterface mManager;
    private int mCount;
    private ContainerFragmentMission mListener;

    public enum ViewStyle {
        VIEWSCROLL(0),
        SIMPLEVIEW(1);

        private int mValue;

        ViewStyle(int value) {
            mValue = value;
        }

        public static ViewStyle fromInt(int value) {
            switch (value) {
                case 0:
                    return VIEWSCROLL;
                case 1:
                default:
                    return SIMPLEVIEW;
            }
        }
    }

    private ViewStyle mViewStyle = ViewStyle.SIMPLEVIEW;

    public MissionContainerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getManagerAndMissions();
        mCount = mMissions.size();
    }

    private void getManagerAndMissions() {
        MapotempoApplication mapotempoApplication = (MapotempoApplication) getActivity().getApplicationContext();
        mManager = mapotempoApplication.getManager();

        MissionAccessInterface missionAccessInterface = mManager.getMissionAccess();
        mMissions = missionAccessInterface.getAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        LinearLayout content = view.findViewById(R.id.mission_view_content);

        if (mViewStyle == ViewStyle.VIEWSCROLL) {
            mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager(), mCount, mMissions);
            ViewPager viewPager = (ViewPager) getActivity().getLayoutInflater().inflate(R.layout.view_pager, null);
            mPager = viewPager.findViewById(R.id.mission_viewpager);

            mPager.setPageTransformer(true, new DepthPageTransformer());
            setPagerChangeListener();
            content.addView(viewPager);
            mPager.setAdapter(mPagerAdapter);
            setListenerForButtons(view);
            setNextAndPreviousVisibility(view);
        } else {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();

            fMission = new MissionFragment();
            fragmentTransaction.add(R.id.mission_view_content, fMission);
            fragmentTransaction.commit();
        }

        return view;
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray attrCustom = context.obtainStyledAttributes(attrs, R.styleable.MissionContainerFragment);
        Integer v = attrCustom.getInteger(R.styleable.MissionContainerFragment_ViewStyle, 1);
        mViewStyle = ViewStyle.fromInt(v);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (ContainerFragmentMission) context;
        if (mListener == null)
            throw new RuntimeException("You must implement ContainerFragmentMission Interface");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mPager = null;
        mPagerAdapter = null;
        fMission = null;
    }

    public void notifyDataChange() {
        mPagerAdapter.notifyDataSetChanged();
    }

    public void refreshPagerData(List<Mission> missions) {
        mMissions = missions;
        ScreenSlidePagerAdapter screenSlidePagerAdapter = (ScreenSlidePagerAdapter) mPagerAdapter;

        if (screenSlidePagerAdapter != null)
            screenSlidePagerAdapter.updateMissions(missions);
    }

    public void setCurrentItem(int position) {
        if (mViewStyle == ViewStyle.VIEWSCROLL) {
            mPager.setCurrentItem(position, true);
        } else {
            fMission.fillViewFromActivity();
        }
    }

    // TODO When clicking on the last element from previous activity, both buttons get invisible
    private void setPagerChangeListener() {
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                mListener.wichViewIsTheCurrent(position);
                setNextAndPreviousVisibility(getView());
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        final int position = getActivity().getIntent().getIntExtra("mission_id", 0);
        mPager.post(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(position);
            }
        });
    }

    private void setNextAndPreviousVisibility(View view) {
        int currentItem = mPager.getCurrentItem();
        final Button next = view.findViewById(R.id.next_nav);
        final Button prev = view.findViewById(R.id.previous_nav);

        next.setVisibility(View.VISIBLE);
        prev.setVisibility(View.VISIBLE);

        if(currentItem == mPagerAdapter.getCount() - 1) {
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
                    int currentItem = mPager.getCurrentItem();
                    mPager.setCurrentItem(currentItem - 1);
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentItem = mPager.getCurrentItem();
                    mPager.setCurrentItem(currentItem + 1);
                }
            });
        } else {
            throw new RuntimeException("Button aren't settled in view");
        }
    }

    public interface ContainerFragmentMission {
        int wichViewIsTheCurrent(int page);
    }
}
