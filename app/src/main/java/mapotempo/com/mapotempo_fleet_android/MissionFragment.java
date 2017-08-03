package mapotempo.com.mapotempo_fleet_android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mapotempo.com.mapotempo_fleet_android.dummy.MissionModel;
import mapotempo.com.mapotempo_fleet_android.dummy.MissionsManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MissionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MissionFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "page";

    private OnFragmentInteractionListener mInteraction;
    private MissionsManager mManager;
    private MissionModel mMission;
    private int mPageNumber;

    public MissionFragment() {

    }

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static MissionFragment create(int pageNumber) {
        MissionFragment fragment = new MissionFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If no mission has been provided by clicking,
        // use the first one in the current set of data
        if (mMission == null) {
            mPageNumber = (getArguments() != null) ? getArguments().getInt(ARG_PAGE) : 0;
            mManager = MissionsManager.getInstance();
            mMission = mManager.getMissionById(mManager.getMissionId(mPageNumber));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission, container, false);

        setButtonsBehaviors(view);
        mInteraction.wichViewIsTheCurrent(mMission);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mInteraction = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BaseFragmentForSingleView");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.fillViewFromActivity(mMission.id);
    }

    public boolean fillViewFromActivity(int missionId) {
        mManager = MissionsManager.getInstance();
        mMission = mManager.getMissionById(missionId);

        this.displayViewData(mMission);
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInteraction = null;
    }

    protected void displayViewData(MissionModel mission) {
        TextView name = getView().findViewById(R.id.name);
        TextView device = getView().findViewById(R.id.device);
        TextView details = getView().findViewById(R.id.details);
        TextView status = getView().findViewById(R.id.mission_status);


        name.setText(mission.name);
        details.setText(mission.delivery_date);
        device.setText(mission.device);
        status.setText(mission.status.toString());
        status.setBackgroundColor(Color.parseColor(mission.status.getColor().getHexaColor()));
    }

    /*****************************
     * BUTTONS CLICK LISTENERS
     */

    private void setButtonsBehaviors(View view) {
        Button update = view.findViewById(R.id.updateBtn);
        Button delete = view.findViewById(R.id.deleteBtn);
        Button status = view.findViewById(R.id.statusBtn);
        Button location = view.findViewById(R.id.go_to_location);

        update.setOnClickListener(this);
        delete.setOnClickListener(this);
        status.setOnClickListener(this);
        location.setOnClickListener(this);
    }

    /**********************************
     * BUTTONS OPERATIONS
     */

    private void updateCurrentMission() throws RuntimeException {
        currentMissionIsNotNull();
    }

    private void deleteCurrentMission() throws RuntimeException {
        currentMissionIsNotNull();

        boolean removed = mManager.removeMission(mMission.id);
        if (removed) {
            if (mManager.getMissionsCount() > 0)
                this.fillViewFromActivity(mManager.getMissionId(0));

            this.backToListActivity();
        }
    }

    private void changeStatusForCurrentMission() throws RuntimeException {
        currentMissionIsNotNull();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog alert = builder.create();

        LayoutInflater inf = getActivity().getLayoutInflater();
        View view = inf.inflate(R.layout.change_status, null);
        TextView status = view.findViewById(R.id.current_status);

        status.setText(mMission.status.toString());
        alert.setView(view);
        alert.show();

        setOnClickListenersForStatus(alert, view);
    }

    private void setOnClickListenersForStatus(final AlertDialog alert, View view) {
        LinearLayout completed = view.findViewById(R.id.completed_status);
        LinearLayout uncompleted = view.findViewById(R.id.uncompleted_status);
        LinearLayout pending = view.findViewById(R.id.pending_status);

        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setMissionStatusTo(MissionModel.Status.COMPLETED, mMission.id);
                mInteraction.onSingleMissionInteraction(mMission);
                fillViewFromActivity(mMission.id);
                alert.dismiss();
            }
        });

        uncompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setMissionStatusTo(MissionModel.Status.UNCOMPLETED, mMission.id);
                mInteraction.onSingleMissionInteraction(mMission);
                fillViewFromActivity(mMission.id);
                alert.dismiss();
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setMissionStatusTo(MissionModel.Status.PENDING, mMission.id);
                mInteraction.onSingleMissionInteraction(mMission);
                fillViewFromActivity(mMission.id);
                alert.dismiss();
            }
        });
    }

    private void fakeUriLocation() {
        String fakeUri = "geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California";
        Uri location = Uri.parse(fakeUri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe)
            startActivity(mapIntent);
    }

    private void backToListActivity() {
        mInteraction.onSingleMissionInteraction(mMission);

        if (getActivity().getClass().equals(SingleMissionView.class)) {
            // Silence is golden
        } else {
            mMission = mManager.getFirstMission();
            displayViewData(mMission);
        }
    }

    /**************************************************
     *
     * Utils
     *
     */

    private void currentMissionIsNotNull() {
        if (mMission == null)
            throw new RuntimeException("Mission is already deleted or is invalid");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.updateBtn:
                updateCurrentMission();
                break;
            case R.id.deleteBtn:
                deleteCurrentMission();
                break;
            case R.id.statusBtn:
                changeStatusForCurrentMission();
                break;
            case R.id.go_to_location:
                fakeUriLocation();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onSingleMissionInteraction(MissionModel mission);
        MissionModel wichViewIsTheCurrent(MissionModel m);
    }
}
