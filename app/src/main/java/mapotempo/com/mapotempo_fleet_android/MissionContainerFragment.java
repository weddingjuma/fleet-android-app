package mapotempo.com.mapotempo_fleet_android;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
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
import android.widget.LinearLayout;

import mapotempo.com.mapotempo_fleet_android.dummy.MissionsManager;


/**
 * create an instance of this fragment.
 */
public class MissionContainerFragment extends Fragment {
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private MissionFragment fMission;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        LinearLayout content = view.findViewById(R.id.mission_view_content);

        if (mViewStyle == ViewStyle.VIEWSCROLL) {
            mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
            ViewPager viewPager = (ViewPager) getActivity().getLayoutInflater().inflate(R.layout.view_pager, null);
            mPager = viewPager.findViewById(R.id.mission_viewpager);
            mPager.setPageTransformer(true, new ZoomOutPageTransformer());

            content.addView(viewPager);
            mPager.setAdapter(mPagerAdapter);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void notifyDataChange() {
        mPagerAdapter.notifyDataSetChanged();
    }

    public void setCurrentItem(int id) {
        if (mViewStyle == ViewStyle.VIEWSCROLL) {
            mPager.setCurrentItem(id, true);
        } else {
            fMission.fillViewFromActivity(id);
        }
    }
}
