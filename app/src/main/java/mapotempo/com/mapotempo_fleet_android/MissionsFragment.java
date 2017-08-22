package mapotempo.com.mapotempo_fleet_android;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.accessor.MissionAccessInterface;
import com.mapotempo.fleet.core.accessor.Access;
import com.mapotempo.fleet.core.exception.CoreException;
import com.mapotempo.fleet.core.model.Mission;

import java.util.ArrayList;
import java.util.List;
/**
 * A fragment representing a list of Missions.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMissionsInteractionListener}
 */
public class MissionsFragment extends Fragment {

    private OnMissionsInteractionListener mListener;
    private MapotempoFleetManagerInterface mManager;
    private MissionsRecyclerViewAdapter mRecycler;
    private MissionAccessInterface iMissionAccess;
    private List<Mission> mMissions = new ArrayList<>();
    private int mColumnCount = 1;
    private Access.ChangeListener<Mission> missionChangeListener = new Access.ChangeListener<Mission>() {
        @Override
        public void changed(final List<Mission> missions) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MissionContainerFragment singleFragment = (MissionContainerFragment) getFragmentManager().findFragmentById(R.id.base_fragment);

                    mRecycler.notifyDataSyncHasChanged(missions);

                    if (singleFragment != null)
                        singleFragment.refreshPagerData(missions);
                }
            });
        }
    };

    protected RecyclerView recyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMissionsInteractionListener) {
            mListener = (OnMissionsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMissionsInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setManagerAndMissions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missions_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        if (recyclerView instanceof RecyclerView) {

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
            }

            mRecycler = new MissionsRecyclerViewAdapter(getContext(), mListener, mMissions);
            recyclerView.setAdapter(mRecycler);
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (iMissionAccess != null)
            iMissionAccess.removeChangeListener(missionChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        MapotempoApplication app = (MapotempoApplication) getActivity().getApplicationContext();
        if (app.isConnected()) {
            mRecycler.notifyDataSyncHasChanged(app.getManager().getMissionAccess().getAll());
            if (iMissionAccess != null)
                iMissionAccess.removeChangeListener(missionChangeListener);
            setManagerAndMissions();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecycler = null;
    }

    public void setCurrentMission (int position) {
        if (mRecycler != null)
            mRecycler.setCurrentMission(position);
    }

    private void setManagerAndMissions() {
        MapotempoApplication mapotempoApplication = (MapotempoApplication) getContext().getApplicationContext();
        if (!mapotempoApplication.isConnected())
            return;

        mManager = mapotempoApplication.getManager();
        iMissionAccess = mManager.getMissionAccess();
        mMissions = iMissionAccess.getAll();

        attachCallBack(iMissionAccess);
    }

    private void attachCallBack(MissionAccessInterface iMissionAccess) {
        iMissionAccess.addChangeListener(missionChangeListener);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other mFragments contained in that
     * activity.
     */
    public interface OnMissionsInteractionListener {
        View.OnClickListener onListMissionsInteraction(int position);
    }
}
