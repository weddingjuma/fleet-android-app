package com.mapotempo.lib.mission;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.services.Constants;
import com.mapbox.services.api.staticimage.v1.MapboxStaticImage;
import com.mapbox.services.api.staticimage.v1.models.StaticMarkerAnnotation;
import com.mapbox.services.commons.models.Position;
import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.MapotempoModelBaseInterface;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.MissionStatusActionInterface;
import com.mapotempo.fleet.api.model.submodel.LocationInterface;
import com.mapotempo.fleet.api.model.submodel.TimeWindowsInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.singnature.SignatureFragment;
import com.mapotempo.lib.utils.DateHelpers;
import com.mapotempo.lib.utils.MissionsStatusGeneric;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This fragment is a view detailed of a mission. it is working along side a <a href="https://developer.android.com/reference/android/support/v4/view/ViewPager.html" target="_blank"><u>Android ViewPager</u></a>  that allow users to swipe left/right side to get the previous/next mission's view.
 * Moreover this fragment provide the following functionality
 * <ul>
 * <li><b>Delete Mission</b>: Simply tag the current mission as deleted.</li>
 * <li><b>Update Status</b>: Change the Status of the current mission. Status are mostly custom, directly pull from database.</li>
 * <li><b>Go to Location</b>: An <a href="https://developer.android.com/reference/android/content/Intent.html" target="_blank"><u>Android Intent</u></a> which give the possibility to open a maps application in order to view the mission's geolocation from its lat/lng coordinates.</li>
 * </ul>
 * <p>
 * <h3>Integration</h3>
 * First and foremost, it is needed to implement the fragment through XML using the following class : <code> {@literal <fragment class="mapotempo.com.mapotempo_fleet_android.mission.MissionDetailsFragment"} </code>
 * <p>
 * This fragment require the implementation of {@link OnFragmentInteractionListener} directly in the Activity that hold the Detail Fragment.
 * This involve the override of {@link OnFragmentInteractionListener#onSingleMissionInteraction(MissionInterface)}. This interface is called when a modification has
 * been done to a mission.
 * </p>
 * As we are using a <a href="https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html" target="_blank"><u>RecyclerView</u></a> to manage the list, you shall notify the alteration on this fragment through this method.
 * <br><b>Example: </b>
 * <code>
 * <pre>
 * {@literal @Override}
 * public void onSingleMissionInteraction(MissionInterface mission) {
 *      MissionsListFragment missionsFragment = (MissionsListFragment) getSupportFragmentManager().findFragmentById(R.id.listMission);
 *
 *      if (missionsFragment != null)
 *      missionsFragment.mRecyclerView.getAdapter().notifyDataSetChanged();
 *
 *      Do here whatever you need with the MissionInterface object
 * }
 * </pre>
 * </code>
 */
public class MissionDetailsFragment extends Fragment {

    public MissionDetailsFragment() {
    }

    //    private static final String COLOR_RED = "e55e5e";
    private static final String COLOR_GREEN = "56b881";
//    private static final String COLOR_BLUE = "3887be";

    private MissionInterface mMission;

    private Context mContext;

    private ImageView mMapImageView;

    private ProgressBar mMapImageLoader;

    private BottomSheetBehavior mBottomSheetBehavior;

    private MapotempoModelBaseInterface.ChangeListener<MissionInterface> mCallback = new MapotempoModelBaseInterface.ChangeListener<MissionInterface>() {
        @Override
        public void changed(final MissionInterface mission, final boolean delete) {
            if (!delete)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMission = mission;
                        fillViewFromActivity();
                    }
                });
        }
    };

    // ==============
    // ==  Public  ==
    // ==============

    public void fillViewFromActivity() {
        if (mMission != null) {
            fillViewData(mMission);
        }
    }

    public void setMission(MissionInterface mission) {
        if (mission != null) {
            mMission = mission;
            mMission.addChangeListener(mCallback);
        } else {
            throw new RuntimeException("Mission passed to constructor must not be null");
        }
    }

    public static MissionDetailsFragment create(int pageNumber, MissionInterface mission) {
        MissionDetailsFragment fragment = new MissionDetailsFragment();
        Bundle args = new Bundle();

        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        fragment.setMission(mission);

        return fragment;
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.MapotempoTheme);

        View view = inflater.inflate(R.layout.fragment_mission, container, false);

        initBottomSheet(view);
        initActionButtons(view);

        mMapImageView = view.findViewById(R.id.mapImageView);

        mMapImageLoader = view.findViewById(R.id.mapLoader);

        return view;
    }

    @Override
    public void setInitialSavedState(SavedState state) {
        super.setInitialSavedState(state);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillViewFromActivity();
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // ======================================
    // ==  View.OnClickListener Interface  ==
    // ======================================

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity
     */
    public interface OnFragmentInteractionListener {
        /**
         * Callback triggered when a modification has been done to a mission
         *
         * @param mission a mission object from Mapotempo model Mission {@link MissionInterface}
         */
        void onSingleMissionInteraction(MissionInterface mission);
    }

    // =================
    // ==  Protected  ==
    // =================

    private void initBottomSheet(View view) {
        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void initActionButtons(View view) {
        ImageButton goToLocationButton = view.findViewById(R.id.go_to_location);
        goToLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMission != null) {
                    LocationInterface loc = mMission.getLocation();

                    Uri location = Uri.parse("geo:" + loc.getLat() + "," + loc.getLon() + "('mission')");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

                    PackageManager packageManager = getActivity().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                    boolean isIntentSafe = (activities.size() > 0);
                    for (ResolveInfo ri : activities) {
                        System.out.print(ri.activityInfo.name);
                        System.out.print(ri.activityInfo.describeContents());
                    }
                    if (isIntentSafe)
                        startActivity(mapIntent);
                }
            }
        });
    }

    protected void fillViewData(MissionInterface mission) {
        // Asynchronously fill the mapImageView when the widget is draw to recover dimensions.
        ViewTreeObserver vto = mMapImageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                mMapImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                Position position = Position.fromCoordinates(
                        mMission.getLocation().getLon(),
                        mMission.getLocation().getLat());

                StaticMarkerAnnotation marker = new StaticMarkerAnnotation.Builder()
                        .setName(com.mapbox.services.Constants.PIN_LARGE)
                        .setPosition(position)
                        .setColor(COLOR_GREEN)
                        .build();

                MapboxStaticImage veniceStaticImage = new MapboxStaticImage.Builder()
                        .setAccessToken(getString(R.string.mapbox_access_token))
                        .setStyleId(Constants.MAPBOX_STYLE_OUTDOORS)
                        .setStaticMarkerAnnotations(marker)
                        .setLat(mMission.getLocation().getLat()) // Image center Latitude
                        .setLon(mMission.getLocation().getLon()) // Image center longitude
                        .setZoom(15)
                        .setWidth(mMapImageView.getMeasuredWidth()) // Image width
                        .setHeight(mMapImageView.getMeasuredHeight()) // Image height
                        .setRetina(true) // Retina 2x image will be returned
                        .build();

                Picasso.with(getActivity()).load(veniceStaticImage.getUrl().toString()).error(R.drawable.ic_logo_mapo_hd_1).into(mMapImageView);
                return true;
            }
        });

        TextView name = getView().findViewById(R.id.name);
        name.setText(mission.getName());

        TextView date = getView().findViewById(R.id.delivery_date);
        date.setText(DateHelpers.parse(mission.getDate(), DateHelpers.DateStyle.HOURMINUTES));

        TextView duration = getView().findViewById(R.id.delivery_duration);
        duration.setText(getString(R.string.duration) + " : " + mission.getDuration());

        // FIXME SPLIT ADDRESS
        TextView address = getView().findViewById(R.id.delivery_address);
        address.setText(mission.getAddress().toString());

        TextView commentView = getView().findViewById(R.id.comment);
        commentView.setText(mission.getComment());

        TextView phoneView = getView().findViewById(R.id.phone);
        phoneView.setText(mission.getPhone());

        LinearLayout timeWindowsLayout = getView().findViewById(R.id.time_windows_container);
        timeWindowsLayout.removeAllViews();
        for (TimeWindowsInterface tw : mMission.getTimeWindow()) {
            TextView textView = new TextView(getContext());
            textView.setText(DateHelpers.parse(tw.getStart(), DateHelpers.DateStyle.HOURMINUTES)
                    + " - "
                    + DateHelpers.parse(tw.getEnd(), DateHelpers.DateStyle.HOURMINUTES));
            timeWindowsLayout.addView(textView);
        }

    /*FloatingActionButton statusBtn = getView().findViewById(R.id.statusBtn);
        // TextView company = getView().findViewById(R.id.company);
        // TextView details = getView().findViewById(R.id.details);
        // TextView status = getView().findViewById(R.id.mission_status);
        // TextView address = getView().findViewById(R.id.delivery_adress);
        /*FloatingActionButton statusBtn = getView().findViewById(R.id.statusBtn);

        int stateList = Color.parseColor(mission.getStatus().getColor());
        statusBtn.setBackgroundTintList(ColorStateList.valueOf(stateList));

        address.setText(mission.getAddress().toString());

        details.setText(mission.getComment());
        company.setText(mission.getCompanyId());
        status.setText(mission.getStatus().getLabel().toUpperCase());*/


        // ######### TEST #########
        /*ImageView attachment = getView().findViewById(R.id.attachment);
        Mission m = (Mission) mission;
        Attachment a = m.getAttachment();
        try {
            if (a != null) {
                InputStream is = a.getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                attachment.setImageBitmap(bitmap);
            }
        } catch (CouchbaseLiteException e) {
            System.err.println(e);
        }*/
    }

    // ===============
    // ==  Private  ==
    // ===============
    private void goSignatureActivity(Context context) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        SignatureFragment newFragment = SignatureFragment.newInstance();
        newFragment.setSignatureSaveListener(new SignatureFragment.SignatureSaveListener() {
            @Override
            public boolean onSignatureSave(Bitmap signatureBitmap) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                ByteArrayInputStream bi = new ByteArrayInputStream(stream.toByteArray());
                mMission.setAttachment("signature", "image/jpeg", bi);
                if (mMission.save()) {
                    Toast.makeText(mContext, R.string.save_signature_success, Toast.LENGTH_SHORT).show();
                    return true;
                } else
                    Toast.makeText(mContext, R.string.save_signature_fail, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        newFragment.show(ft, "t");
    }


    // #############################################
    // #############################################
    // ###                TO REMOVE              ###
    // #############################################
    // #############################################

    private boolean deleteMission() {
        boolean del = mMission.delete();
        mMission = null;
        return del;
    }

    private void updateCurrentMission() throws RuntimeException {
        currentMissionIsNotNull();
    }

    private void initDeletionForCurrentMission() throws RuntimeException {
        currentMissionIsNotNull();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog alert = builder.create();

        LayoutInflater inflate = getActivity().getLayoutInflater();
        View view = inflate.inflate(R.layout.yes_no_choice, null);

        alert.setView(view);
        alert.show();
        setOnClickListenersForDeletion(view, alert);
    }

    private void changeStatusForCurrentMission() throws RuntimeException {
        currentMissionIsNotNull();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog alert = builder.create();

        LayoutInflater inf = getActivity().getLayoutInflater();
        View view = inf.inflate(R.layout.change_status, null);
        TextView status = view.findViewById(R.id.current_status);

        status.setText(mMission.getStatus().getLabel().toUpperCase());
        status.setBackgroundColor(Color.parseColor(mMission.getStatus().getColor()));
        alert.setView(view);
        alert.show();

        setOnClickListenersForStatus(alert, view);
    }


    private void setOnClickListenersForStatus(final AlertDialog alert, View view) {
        MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        MapotempoFleetManagerInterface manager = mapotempoApplication.getManager();
        List<MissionStatusActionInterface> missionStatusTypes = manager.getMissionStatusActionAccessInterface().getByPrevious(mMission.getStatus());
        ArrayList<MissionsStatusGeneric<LinearLayout, MissionStatusActionInterface>> viewListStatus = buildViewFor(missionStatusTypes, view);

        if (viewListStatus != null && viewListStatus.size() > 0) {
            for (final MissionsStatusGeneric<LinearLayout, MissionStatusActionInterface> status : viewListStatus) {
                status.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO
                        mMission.setStatus(status.getType().getNextStatus());
                        mMission.save();
                        fillViewFromActivity();

                        alert.dismiss();
                    }
                });
            }
        }
    }

    private ArrayList<MissionsStatusGeneric<LinearLayout, MissionStatusActionInterface>> buildViewFor(List<MissionStatusActionInterface> missionStatusActions, View view) {
        ArrayList<MissionsStatusGeneric<LinearLayout, MissionStatusActionInterface>> statusViews = new ArrayList<>();

        for (MissionStatusActionInterface missionStatusType : missionStatusActions) {
            LinearLayout newLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.single_status, null);
            LinearLayout layoutContainer = view.findViewById(R.id.status_container);
            TextView textViewLabel = newLayout.findViewById(R.id.status_label);
            ImageView imageViewIcon = newLayout.findViewById(R.id.icon);
            MissionsStatusGeneric<LinearLayout, MissionStatusActionInterface> missionsStatusGeneric;

            String icon = null;
            String label = missionStatusType.getLabel();
            String color = missionStatusType.getNextStatus().getColor();

            textViewLabel.setText(label);
            imageViewIcon.setColorFilter(Color.parseColor(color));
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

    public void setOnClickListenersForDeletion(View view, final AlertDialog alert) {
        Button valid = view.findViewById(R.id.valid);
        Button cancel = view.findViewById(R.id.cancel);

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteMission()) {
                    alert.dismiss();
                    backToListActivity(getActivity().getBaseContext());
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

    private void backToListActivity(Context context) {
        // FIXME Reafactor this !
        /*if (context.getClass().equals(MissionActivity.class)) {
            ((Activity) context).onBackPressed();
        }*/
    }

    private void currentMissionIsNotNull() {
        if (mMission == null)
            throw new RuntimeException("Mission is already deleted or is invalid");
    }
}
