package mapotempo.com.mapotempo_fleet_android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.accessor.MissionAccessInterface;
import com.mapotempo.fleet.core.exception.CoreException;
import com.mapotempo.fleet.core.model.Mission;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import mapotempo.com.mapotempo_fleet_android.dummy.MissionsManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MissionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MissionFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mInteraction;
    private int mPageNumber = 0;
    private Mission mMission;

    public MissionFragment() { }

    public static MissionFragment create(int pageNumber, Mission mission) {
        MissionFragment fragment = new MissionFragment();
        Bundle args = new Bundle();

        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        fragment.setMission(mission);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = (getArguments() != null) ? getArguments().getInt("page") : 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mission, container, false);
        setButtonsBehaviors(view);
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
        fillViewFromActivity(mPageNumber);
    }

    public boolean fillViewFromActivity(int position) {
        if (mMission != null) {
            displayViewData(mMission);
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInteraction = null;
    }

    protected void displayViewData(Mission mission) {
        TextView name = getView().findViewById(R.id.name);
        TextView company = getView().findViewById(R.id.company);
        TextView details = getView().findViewById(R.id.details);
        TextView status = getView().findViewById(R.id.mission_status);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String missionDate = dateFormat.format(mission.getDeliveryDate());

        name.setText(mission.getName());
        details.setText(missionDate);
        company.setText(mission.getCompanyId());
        status.setText("NOT SET");
        status.setBackgroundColor(MissionsManager.fakeStatusColor());
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

        Log.w("NOT IMPLEMENTED", "Delete can't be performed on this version");

        backToListActivity();
    }

    private void changeStatusForCurrentMission() throws RuntimeException {
        currentMissionIsNotNull();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog alert = builder.create();

        LayoutInflater inf = getActivity().getLayoutInflater();
        View view = inf.inflate(R.layout.change_status, null);
        TextView status = view.findViewById(R.id.current_status);

        Log.w("MISSIONS STATUS", "Missions status is not implemented yet");

//        status.setText(mMission.status.toString());
//        alert.setView(view);
//        alert.show();

//        setOnClickListenersForStatus(alert, view);
    }

    private void setOnClickListenersForStatus(final AlertDialog alert, View view) {
        LinearLayout completed = view.findViewById(R.id.completed_status);
        LinearLayout uncompleted = view.findViewById(R.id.uncompleted_status);
        LinearLayout pending = view.findViewById(R.id.pending_status);

//        completed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mManager.setMissionStatusTo(MissionModel.Status.COMPLETED, mMission.id);
//                mInteraction.onSingleMissionInteraction(mMission);
//                fillViewFromActivity(mMission.id);
//                alert.dismiss();
//            }
//        });
//
//        uncompleted.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mManager.setMissionStatusTo(MissionModel.Status.UNCOMPLETED, mMission.id);
//                mInteraction.onSingleMissionInteraction(mMission);
//                fillViewFromActivity(mMission.id);
//                alert.dismiss();
//            }
//        });
//
//        pending.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mManager.setMissionStatusTo(MissionModel.Status.PENDING, mMission.id);
//                mInteraction.onSingleMissionInteraction(mMission);
//                fillViewFromActivity(mMission.id);
//                alert.dismiss();
//            }
//        });
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
//            displayViewData(mMission);
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

    public void setMission(Mission mission) {
        mMission = mission;
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
     * to the activity and potentially other mFragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onSingleMissionInteraction(Mission mission);
    }
}
