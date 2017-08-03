package mapotempo.com.mapotempo_fleet_android;

import android.graphics.Color;
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
import java.util.Map;

import mapotempo.com.mapotempo_fleet_android.dummy.MissionsManager;
import mapotempo.com.mapotempo_fleet_android.dummy.MissionModel;

/**
 * A fragment representing a list of Missions.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMissionsInteractionListener}
 * interface. Or use {@link MapotempoFragmentsController} as a parent class of each activities.
 */
public class MissionsFragment extends Fragment {

    private OnMissionsInteractionListener mListener;
    private Map<Integer, MissionModel> mMissions;
    private MissionsManager mManager;
    private int mColumnCount = 1;
    private Context mContext;

    protected RecyclerView recyclerView;
    private MissionsRecyclerViewAdapter mRecycler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mManager = MissionsManager.getInstance();

        if (savedInstanceState == null)
            mMissions = mManager.emuleAsetOfFakeMissions(5);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recyclerView != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missions_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        if (recyclerView instanceof RecyclerView) {

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(mContext, mColumnCount));
            }

            mRecycler = new MissionsRecyclerViewAdapter(mContext, mListener);
            recyclerView.setAdapter(mRecycler);
        }

        attachAddButton(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void attachAddButton(View view) {
        FloatingActionButton btn = view.findViewById(R.id.fab);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(getClass().getName(), "Not implemented");
            }
        });
    }

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setCurrentMission (MissionModel mission) {
        if (mRecycler != null)
            mRecycler.setCurrentMission(mission.id);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnMissionsInteractionListener {
        View.OnClickListener onListMissionsInteraction(MissionModel item);
    }
}
