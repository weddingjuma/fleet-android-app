package com.mapotempo.lib.mission;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.accessor.AccessInterface;
import com.mapotempo.fleet.api.model.accessor.MissionAccessInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;

import java.util.ArrayList;
import java.util.List;

import mapotempo.com.lib.R;

/**
 * This fragment is responsible for display all missions assigned to the user currently connected.
 * Each view displayed by this fragment is composed of 3 main UI elements :
 * <ul>
 * <li>The name </li>
 * <li>The delivery date</li>
 * <li>The delivery hour</li>
 * </ul>
 * <p>
 * <h3>Integration</h3>
 * First and foremost, it is needed to implement the fragment through XML using the following class : <code> {@literal <fragment class="mapotempo.com.mapotempo_fleet_android.mission.MissionsPagerFragment"} </code>
 * <p>
 * This fragment require the implementation of {@link OnMissionSelectedListener} directly in the Activity that hold the List Fragment.
 * Then Override the {@link OnMissionSelectedListener#onMissionSelected(int)}
 * which force to return an Android's onMissionFocus Listener. If you don't know about Event Listeners please check the documentation here : <a href="https://developer.android.com/reference/android/view/View.OnClickListener.html" target="_blank">Android Listener</a> <br>
 * Feel free to put any logic inside the listener. Keep in mind that the position returned can be used to get the detailed view of the mission triggered by a onMissionFocus.
 * </p>
 * <p>
 * <b>Here is an example of usability: </b>
 * <pre>
 *     {@literal @Override}
 *     public View.OnClickListener onMissionSelected(final int position) {
 *         View.OnClickListener onClick = new View.OnClickListener() {
 *             {@literal @Override}
 *             public void onClick(View v) {
 *                 intent = new Intent(v.getContext(), YouNewActivity.class);
 *                 intent.putExtra("mission_position", position);
 *                 v.getContext().startActivity(intent);
 *             }
 *         }
 *         return onClick;
 *    }
 * </pre>
 */
public class MissionsListFragment extends Fragment {

    private OnMissionSelectedListener mListener;
    private MapotempoFleetManagerInterface mManager;
    private MissionsRecyclerViewAdapter mRecyclerAdapter;
    private MissionAccessInterface iMissionAccess;
    private List<MissionInterface> mMissions = new ArrayList<>();
    private int mColumnCount = 1;
    private AccessInterface.ChangeListener<MissionInterface> missionChangeListener = new AccessInterface.ChangeListener<MissionInterface>() {
        @Override
        public void changed(final List<MissionInterface> missions) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerAdapter.notifyDataSyncHasChanged(missions);
                }
            });
        }
    };

    public RecyclerView mRecyclerView;

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMissionSelectedListener) {
            mListener = (OnMissionSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMissionSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missions_list, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new MissionsRecyclerViewAdapter(getContext(), new OnMissionSelectedListener() {
            @Override
            public void onMissionSelected(int position) {
                mListener.onMissionSelected(position);
            }
        }, mMissions);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setManagerAndMissions();
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

        MapotempoApplicationInterface app = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        if (app.getManager() != null) {
            mRecyclerAdapter.notifyDataSyncHasChanged(app.getManager().getMissionAccess().getAll());
            if (iMissionAccess != null)
                iMissionAccess.removeChangeListener(missionChangeListener);
            setManagerAndMissions();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerAdapter = null;
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void setCurrentMission(int position) {
        mRecyclerView.smoothScrollToPosition(position);
        if (mRecyclerAdapter != null)
            mRecyclerAdapter.setMissionFocus(position);
    }

    /**
     * This interface must be implemented by activities that contain {@link MissionsListFragment}
     */
    public interface OnMissionSelectedListener {
        /**
         * A Callback triggered when an item list is created. Use it to set a onMissionFocus listener to each of them.
         *
         * @param position return the item list position
         * @return View.OnClickListener
         */
        void onMissionSelected(int position);
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void setManagerAndMissions() {
        MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getContext().getApplicationContext();
        if (mapotempoApplication.getManager() == null)
            return;

        mManager = mapotempoApplication.getManager();
        iMissionAccess = mManager.getMissionAccess();
        mMissions = iMissionAccess.getAll();

        attachCallBack(iMissionAccess);
    }

    private void attachCallBack(MissionAccessInterface iMissionAccess) {
        iMissionAccess.addChangeListener(missionChangeListener);
    }
}
