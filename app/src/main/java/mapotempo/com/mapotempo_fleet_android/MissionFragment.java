package mapotempo.com.mapotempo_fleet_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.core.base.MapotempoModelBase;
import com.mapotempo.fleet.core.model.Mission;
import com.mapotempo.fleet.core.model.submodel.Location;
import com.mapotempo.fleet.core.model.submodel.MissionCommand;

import java.util.ArrayList;
import java.util.List;

import mapotempo.com.mapotempo_fleet_android.utils.DateHelpers;
import mapotempo.com.mapotempo_fleet_android.utils.MissionsStatusGeneric;


/**
 * This fragment is a view detailed of a mission. it is working along side a <a href="https://developer.android.com/reference/android/support/v4/view/ViewPager.html" target="_blank"><u>Android ViewPager</u></a>  that allow users to swipe left/right side to get the previous/next mission's view.
 * Moreover this fragment provide the following functionality
 * <ul>
 * <li><b>Delete Mission</b>: Simply tag the current mission as deleted.</li>
 * <li><b>Update Status</b>: Change the Status of the current mission. Status are mostly custom, directly pull from database.</li>
 * <li><b>Go to Location</b>: An <a href="https://developer.android.com/reference/android/content/Intent.html" target="_blank"><u>Android Intent</u></a> which give the possibility to open a maps application in order to view the mission's geolocation from its lat/lng coordinates.</li>
 * </ul>
 *
 * <h3>Integration</h3>
 * First and foremost, it is needed to implement the fragment through XML using the following class : <code> {@literal <fragment class="mapotempo.com.mapotempo_fleet_android.MissionFragment"} </code>
 * <p>
 * This fragment require the implementation of {@link OnFragmentInteractionListener} directly in the Activity that hold the Detail Fragment.
 * This involve the override of {@link OnFragmentInteractionListener#onSingleMissionInteraction(Mission)}. This interface is called when a modification has been done to a mission.
 * </p>
 * As we are using a <a href="https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html" target="_blank"><u>RecyclerView</u></a> to manage the list, you shall notify the alteration on this fragment through this method.
 * <br><b>Example: </b>
 * <code>
 * <pre>
 * {@literal @Override}
 * public void onSingleMissionInteraction(Mission mission) {
 *      MissionsFragment missionsFragment = (MissionsFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
 *
 *      if (missionsFragment != null)
 *      missionsFragment.recyclerView.getAdapter().notifyDataSetChanged();
 *
 *      Do here whatever you need with the Mission object
 * }
 * </pre>
 * </code>
 *
 */

public class MissionFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mInteraction;
    private MissionInterface mMission;
    private MapotempoModelBase.ChangeListener<Mission> mCallback = new MapotempoModelBase.ChangeListener<Mission>() {
        @Override
        public void changed(final Mission mission) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMission = mission;
                    fillViewFromActivity();
                }
            });
        }
    };

    public MissionFragment() { }

    public static MissionFragment create(int pageNumber, MissionInterface mission) {
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
            throw new RuntimeException(context.toString() + " must implement BaseFragmentForSingleView");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mMission != null)
            mMission.removeChangeListener(mCallback);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInteraction = null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillViewFromActivity();
    }

    public boolean fillViewFromActivity() {
        if (mMission != null) {
            displayViewData(mMission);
        }
        return true;
    }

    protected void displayViewData(MissionInterface mission) {
        TextView name = getView().findViewById(R.id.name);
        TextView company = getView().findViewById(R.id.company);
        TextView details = getView().findViewById(R.id.details);
        TextView status = getView().findViewById(R.id.mission_status);
        TextView date = getView().findViewById(R.id.delivery_date);
        TextView address = getView().findViewById(R.id.delivery_adress);
        FloatingActionButton statusBtn = getView().findViewById(R.id.statusBtn);

        int stateList = Color.parseColor("#" + mission.getStatus().getColor());
        statusBtn.setBackgroundTintList(ColorStateList.valueOf(stateList));

        name.setText(mission.getName());
        address.setText(mission.getAddress().toString());
        date.setText(DateHelpers.parse(mission.getDeliveryDate(), DateHelpers.DateStyle.FULLDATE));
        details.setText(details.getText());
        company.setText(mission.getCompanyId());
        status.setText(mission.getStatus().getLabel().toUpperCase());
    }

    /*****************************
     * BUTTONS CLICK LISTENERS
     */

    private void setButtonsBehaviors(View view) {
//        Button update = view.findViewById(R.id.updateBtn);
        FloatingActionButton delete = view.findViewById(R.id.delete);
        FloatingActionButton status = view.findViewById(R.id.statusBtn);
        ImageButton location = view.findViewById(R.id.go_to_location);

//        update.setOnClickListener(this);
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

    private void initDeletionForCurrentMission() throws RuntimeException {
        currentMissionIsNotNull();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog alert = builder.create();

        LayoutInflater inflate = getActivity().getLayoutInflater();
        View view = inflate.inflate(R.layout.yes_no_choice, null);

        alert.setView(view);
        alert.show();
        setOnClickListenersForDeletion(view, alert);
    }

    private void changeStatusForCurrentMission() throws RuntimeException {
        currentMissionIsNotNull();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final AlertDialog alert = builder.create();

        LayoutInflater inf = getActivity().getLayoutInflater();
        View view = inf.inflate(R.layout.change_status, null);
        TextView status = view.findViewById(R.id.current_status);

        status.setText(mMission.getStatus().getLabel().toUpperCase());
        status.setBackgroundColor(Color.parseColor("#" + mMission.getStatus().getColor()));
        alert.setView(view);
        alert.show();

        setOnClickListenersForStatus(alert, view);
    }

    private void setOnClickListenersForStatus(final AlertDialog alert, View view) {
        MapotempoApplication mapotempoApplication = (MapotempoApplication) getActivity().getApplicationContext();
        MapotempoFleetManagerInterface manager = mapotempoApplication.getManager();
        List<MissionCommand> missionStatusTypes = mMission.getStatus().getCommands();
        ArrayList<MissionsStatusGeneric<LinearLayout, MissionCommand>> viewListStatus = buildViewFor(missionStatusTypes, view);

        if (viewListStatus != null && viewListStatus.size() > 0) {
            for (final MissionsStatusGeneric<LinearLayout, MissionCommand> status : viewListStatus) {
                status.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMission.setStatus(status.getType().getMissionStatusType());
                        mMission.save();
                        fillViewFromActivity();

                        alert.dismiss();
                    }
                });
            }
        }
    }

    private ArrayList<MissionsStatusGeneric<LinearLayout, MissionCommand>> buildViewFor(List<MissionCommand> missionStatusTypes, View view) {
        ArrayList<MissionsStatusGeneric<LinearLayout, MissionCommand>> statusViews = new ArrayList<>();

        for (MissionCommand missionStatusType : missionStatusTypes) {
            LinearLayout newLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.single_status, null);
            LinearLayout layoutContainer = view.findViewById(R.id.status_container);
            TextView textViewLabel = newLayout.findViewById(R.id.status_label);
            ImageView imageViewIcon = newLayout.findViewById(R.id.icon);
            MissionsStatusGeneric<LinearLayout, MissionCommand> missionsStatusGeneric;

            String icon = null;
            String label = missionStatusType.getLabel();
            String color = missionStatusType.getMissionStatusType().getColor();

            textViewLabel.setText(label);
            imageViewIcon.setColorFilter(Color.parseColor("#" + color));
            layoutContainer.addView(newLayout);

            try {
                missionsStatusGeneric = new MissionsStatusGeneric(newLayout, missionStatusType);
                statusViews.add(missionsStatusGeneric);
            } catch (NullPointerException e) {
                e.getStackTrace();
            }
        }

        return statusViews;
    }

    private void fakeUriLocation() {
        Location loc = mMission.getLocation();

        Uri location = Uri.parse("geo:" + loc.getLat() + "," + loc.getLon());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
        boolean isIntentSafe = (activities.size() > 0);

        if (isIntentSafe)
            startActivity(mapIntent);
    }

    private void backToListActivity(Context context) {
        mInteraction.onSingleMissionInteraction(mMission);

        if (context.getClass().equals(SingleMissionView.class)) {
            // Silence is golden
            ((Activity) context).onBackPressed();
        } else {
//            displayViewData(mMission);
        }
    }

    private void currentMissionIsNotNull() {
        if (mMission == null)
            throw new RuntimeException("Mission is already deleted or is invalid");
    }

    public void setMission(MissionInterface mission) {
        if (mission != null) {
            mMission = mission;
            mMission.addChangeListener(mCallback);
        } else {
            throw new RuntimeException("Mission passed to constructor must not be null");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.updateBtn:
//                updateCurrentMission();
//                break;
            case R.id.delete:
                initDeletionForCurrentMission();
                break;
            case R.id.statusBtn:
                changeStatusForCurrentMission();
                break;
            case R.id.go_to_location:
                fakeUriLocation();
                break;
        }
    }

    public void setOnClickListenersForDeletion(View view, final AlertDialog alert) {
        Button valid = view.findViewById(R.id.valid);
        Button cancel = view.findViewById(R.id.cancel);

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteMission()) {
                    alert.dismiss();
                    backToListActivity(getContext());
                } else {
                    // Silence now;
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    private boolean deleteMission() {
        return mMission.delete();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity
     */
    public interface OnFragmentInteractionListener {
        /**
         * Callback triggered when a modification has been done to a mission
         * @param mission a mission object from Mapotempo model Mission {@link Mission}
         */
        void onSingleMissionInteraction(MissionInterface mission);
    }
}
