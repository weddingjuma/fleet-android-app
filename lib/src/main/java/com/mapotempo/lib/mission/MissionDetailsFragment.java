package com.mapotempo.lib.mission;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.MapotempoModelBaseInterface;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.MissionStatusActionInterface;
import com.mapotempo.fleet.api.model.MissionStatusTypeInterface;
import com.mapotempo.fleet.api.model.submodel.LocationInterface;
import com.mapotempo.fleet.api.model.submodel.TimeWindowsInterface;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.actions.ActionsListFragment;
import com.mapotempo.lib.signature.SignatureFragment;
import com.mapotempo.lib.utils.DateHelpers;
import com.mapotempo.lib.utils.PhoneNumberHelper;
import com.mapotempo.lib.utils.SVGDrawableHelper;
import com.mapotempo.lib.utils.StaticMapURLHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
 * This fragment require the implementation of {@link OnMissionDetailsFragmentListener} directly in the Activity that hold the Detail Fragment.
 * This involve the override of {@link OnMissionDetailsFragmentListener#onMapImageViewClick)}. This interface is called when a modification has
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity
     */
    public interface OnMissionDetailsFragmentListener {
        /**
         * Callback triggered when a modification has been done to a mission
         *
         * @param mission a mission object from Mapotempo model Mission {@link MissionInterface}
         */
        void onMapImageViewClick(MissionInterface mission);
    }

    private static final String COLOR_GREEN = "56b881";

    private MissionInterface mMission;

    private ProgressBar mMapLoader;
    private ImageView mMapImageView;
    private ImageView mMapMarker;

    private TextView mMissionName;
    private TextView mMissionReference;
    private TextView mMissionStatusLabel;

    private LinearLayout mLayoutDelivery;
    private TextView mTextViewDeliveryStreet;
    private TextView mTextViewDeliveryAddress;
    private TextView mTextViewDeliveryAddressDetails;

    private TextView mTextViewDate;
    private TextView mTextViewDuration;

    private LinearLayout mLayoutTimeWindows;
    private LinearLayout mLayoutTimeWindowsContainer;

    private LinearLayout mLayoutPhone;
    private TextView mTextViewPhone;

    private LinearLayout mLayoutComment;
    private TextView mTextViewComment;

    private Button mNavigationAction;
    private FloatingActionButton mStatusCurrent;
    private FloatingActionButton mStatusFirstAction;
    private FloatingActionButton mStatusSecondAction;
    private FloatingActionButton mStatusThirdAction;
    private FloatingActionButton mStatusMoreAction;

    private BottomSheetBehavior mBottomSheetBehavior;

    private ActionsListFragment mActionFragment;

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

    private MissionDetailsFragment.OnMissionDetailsFragmentListener mListener;

    static int MAP_IMAGE_WIDTH_QUALITY = 500;

    // ==============
    // ==  Public  ==
    // ==============

    public void fillViewFromActivity() {
        if (mMission != null) {
            fillViewData(mMission);
            initNavigationAction(mMission);
            initMainActionButtons(mMission);
            initActionButtons(mMission);
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

    public boolean onBackPressed() {
        if (BottomSheetBehavior.STATE_COLLAPSED != mBottomSheetBehavior.getState()) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        }
        return false;
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (MissionDetailsFragment.OnMissionDetailsFragmentListener) context;
        if (mListener == null)
            throw new RuntimeException("You must implement OnMissionFocusListener Interface");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_mission, container, false);

        // Map view
        mMapImageView = view.findViewById(R.id.mapImageView);
        mMapLoader = view.findViewById(R.id.mapLoader);
        mMapMarker = view.findViewById(R.id.mapMarker);

        // Header view
        mMissionName = view.findViewById(R.id.name);
        mMissionReference = view.findViewById(R.id.reference);
        mMissionStatusLabel = view.findViewById(R.id.status_label);

        // Location view
        mLayoutDelivery = view.findViewById(R.id.delivery_address_layout);
        mTextViewDeliveryStreet = view.findViewById(R.id.delivery_street);
        mTextViewDeliveryAddress = view.findViewById(R.id.delivery_address);
        mTextViewDeliveryAddressDetails = view.findViewById(R.id.delivery_address_details);

        // Date view
        mTextViewDate = view.findViewById(R.id.delivery_date);
        mTextViewDuration = view.findViewById(R.id.delivery_duration);

        // TimeWindows view
        mLayoutTimeWindows = view.findViewById(R.id.time_windows_layout);
        mLayoutTimeWindowsContainer = view.findViewById(R.id.time_windows_container);

        // Phone view
        mLayoutPhone = view.findViewById(R.id.phone_layout);
        mTextViewPhone = view.findViewById(R.id.phone);

        // Comment view
        mLayoutComment = view.findViewById(R.id.comment_layout);
        mTextViewComment = view.findViewById(R.id.comment);

        // Action button
        mNavigationAction = view.findViewById(R.id.navigation_launcher);
        mStatusCurrent = view.findViewById(R.id.status);
        mStatusFirstAction = view.findViewById(R.id.first_action);
        mStatusSecondAction = view.findViewById(R.id.second_action);
        mStatusThirdAction = view.findViewById(R.id.third_action);
        mStatusMoreAction = view.findViewById(R.id.more_action);

        mMapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMapImageViewClick(mMission);
            }
        });

        return view;
    }

    //
    @Override
    public void onResume() {
        super.onResume();
        fillViewFromActivity();
        initBottomSheet(getView());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mMission != null)
            mMission.removeChangeListener(mCallback);
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void initBottomSheet(View view) {
        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        View.OnClickListener listenerMore = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
                MapotempoFleetManagerInterface manager = mapotempoApplication.getManager();
                List<MissionStatusActionInterface> actions = manager.getMissionStatusActionAccessInterface().getByPrevious(mMission.getStatus());
                if (actions.size() > 0)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        };
        mStatusMoreAction.setOnClickListener(listenerMore);
        mStatusCurrent.setOnClickListener(listenerMore);


    }

    private void initNavigationAction(final MissionInterface mission) {
        mNavigationAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMission != null) {
                    LocationInterface loc = mission.getLocation();

                    // Check lat/lon object
                    if (loc.isValide()) {
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
            }
        });
    }

    private void fillViewData(MissionInterface mission) {
        mapVisivilityManager();
        detailsContentManager(mission);
        detailsVisibilityManager();
    }

    private void mapVisivilityManager() {
        // Asynchronously fill the mapImageView when the widget is draw to retrieve dimensions.
        if (mMission.getLocation().isValide()) {
            ViewTreeObserver vto = mMapImageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    mMapImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Init visibility
                    mMapLoader.setVisibility(View.VISIBLE);
                    mMapImageView.setVisibility(View.GONE);
                    mMapMarker.setVisibility(View.GONE);

                    // Prepare Request
                    int x = mMapImageView.getMeasuredWidth();
                    int y = mMapImageView.getMeasuredHeight();
                    double ratio = (double) y / (double) x;
                    x = MAP_IMAGE_WIDTH_QUALITY;
                    y = (int) (x * ratio);

                    String MapUrl = new StaticMapURLHelper.TileHostingURLBuilder()
                            .setBaseURL(getString(R.string.tilehosting_base_url))
                            .setKey(getString(R.string.tilehosting_access_token))
                            .setLat(mMission.getLocation().getLat())
                            .setLon(mMission.getLocation().getLon())
                            .setWidth(x)
                            .setHeight(y)
                            .setZoom(18)
                            .Build();

                    // Init request with Picasso
                    Picasso.with(getActivity())
                            .load(MapUrl)
                            .error(R.drawable.bg_world_mapbox_v10)
                            .into(mMapImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mMapLoader.setVisibility(View.GONE);
                                    mMapImageView.setVisibility(View.VISIBLE);
                                    mMapMarker.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onError() {
                                    mMapLoader.setVisibility(View.GONE);
                                    mMapImageView.setVisibility(View.VISIBLE);
                                    mMapMarker.setVisibility(View.GONE);
                                }
                            });

                    return true;
                }
            });
        }
    }

    private void detailsContentManager(MissionInterface mission) {
        mMissionName.setText(mission.getName());
        mMissionReference.setText(mission.getReference());

        mTextViewDate.setText(DateHelpers.parse(mission.getDate(), DateHelpers.DateStyle.HOURMINUTES));
        mTextViewDuration.setText(DateHelpers.FormatedHour(mission.getDuration()));

        mTextViewDeliveryStreet.setText(String.format("%s", mission.getAddress().getStreet()));
        mTextViewDeliveryAddress.setText(String.format("%s %s", mission.getAddress().getPostalcode(), mission.getAddress().getCity()));
        mTextViewDeliveryAddressDetails.setText(mission.getAddress().getDetail());

        mTextViewPhone.setText(PhoneNumberHelper.intenationalPhoneNumber(mission.getPhone()));

        mTextViewComment.setText(mission.getComment());

        mLayoutTimeWindowsContainer.removeAllViews();
        for (TimeWindowsInterface tw : mMission.getTimeWindow()) {
            TextView textView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.time_windows_text_view, null);
            String date = String.format("%s - %s", DateHelpers.parse(tw.getStart(), DateHelpers.DateStyle.HOURMINUTES), DateHelpers.parse(tw.getEnd(), DateHelpers.DateStyle.HOURMINUTES));
            textView.setText(date);
            mLayoutTimeWindowsContainer.addView(textView);
        }
    }

    private void detailsVisibilityManager() {
        mMissionReference.setVisibility(isEmptyTextView(mMissionReference) ? View.VISIBLE : View.GONE);
        mTextViewDuration.setVisibility(isEmptyTextView(mTextViewDuration) ? View.VISIBLE : View.GONE);
        mLayoutDelivery.setVisibility((isEmptyTextView(mTextViewDeliveryAddress) || isEmptyTextView(mTextViewDeliveryStreet) || isEmptyTextView(mTextViewDeliveryAddressDetails)) ? View.VISIBLE : View.GONE);
        mTextViewDeliveryStreet.setVisibility(isEmptyTextView(mTextViewDeliveryStreet) ? View.VISIBLE : View.GONE);
        mTextViewDeliveryAddress.setVisibility(isEmptyTextView(mTextViewDeliveryAddress) ? View.VISIBLE : View.GONE);
        mTextViewDeliveryAddressDetails.setVisibility(isEmptyTextView(mTextViewDeliveryAddressDetails) ? View.VISIBLE : View.GONE);
        mLayoutTimeWindows.setVisibility((mLayoutTimeWindowsContainer.getChildCount() > 0 ? View.VISIBLE : View.GONE));
        mLayoutPhone.setVisibility(isEmptyTextView(mTextViewPhone) ? View.VISIBLE : View.GONE);
        mLayoutComment.setVisibility(isEmptyTextView(mTextViewComment) ? View.VISIBLE : View.GONE);
    }

    private boolean isEmptyTextView(TextView textView) {
        return textView.getText().toString().trim().length() > 0;
    }

    private void goSignatureFragment(Context context) {
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
                    Toast.makeText(getContext(), R.string.save_signature_success, Toast.LENGTH_SHORT).show();
                    return true;
                } else
                    Toast.makeText(getContext(), R.string.save_signature_fail, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        newFragment.show(ft, "t");
    }

    private void initActionButtons(final MissionInterface mission) {
        final MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        MapotempoFleetManagerInterface manager = mapotempoApplication.getManager();
        List<MissionStatusActionInterface> actions = manager.getMissionStatusActionAccessInterface().getByPrevious(mission.getStatus());
        ActionsListFragment fragment = (ActionsListFragment) getChildFragmentManager().findFragmentById(R.id.actions_fragment);
        if (fragment != null) {
            fragment.setActions(actions);
            fragment.setOnActionSelectedListener(new ActionsListFragment.OnMissionActionSelectedListener() {
                @Override
                public void onMissionActionSelected(MissionStatusTypeInterface newStatus) {
                    mission.setStatus(newStatus);
                    mission.save();
                    initMainActionButtons(mission);
                    initActionButtons(mMission);
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
        }
    }

    private void initMainActionButtons(final MissionInterface mission) {
        // Current status
        MissionStatusTypeInterface statusType = mission.getStatus();
        Drawable drawable = SVGDrawableHelper.getDrawableFromSVGPath(statusType.getSVGPath(), "#ffffff", new BitmapDrawable());
        mStatusCurrent.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(statusType.getColor())));
        mStatusCurrent.setImageDrawable(drawable);
        mMissionStatusLabel.setText(statusType.getLabel());

        // Next actions
        int idx = 0;
        FloatingActionButton floatingActionButtonArray[] = {mStatusFirstAction, mStatusSecondAction, mStatusThirdAction};
        final MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        MapotempoFleetManagerInterface manager = mapotempoApplication.getManager();
        List<MissionStatusActionInterface> actions = manager.getMissionStatusActionAccessInterface().getByPrevious(mission.getStatus());

        for (FloatingActionButton button : floatingActionButtonArray) {
            if (idx < actions.size()) {
                final MissionStatusActionInterface action = actions.get(idx);
                Drawable d = SVGDrawableHelper.getDrawableFromSVGPath(action.getNextStatus().getSVGPath(), "#ffffff", new BitmapDrawable());
                button.setImageDrawable(d);
                button.show();
                button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(action.getNextStatus().getColor())));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mission.setStatus(action.getNextStatus());
                        mission.save();
                        initMainActionButtons(mission);
                        initActionButtons(mMission);
                    }
                });
            } else
                button.setVisibility(View.GONE);
            idx++;
        }

        if (actions.size() <= 3)
            mStatusMoreAction.setVisibility(View.GONE);
        else
            mStatusMoreAction.setVisibility(View.VISIBLE);
    }
}

// #############################################
// #############################################
// ###                TO REMOVE              ###
// #############################################
// #############################################
//
//    private void changeStatusForCurrentMission() throws RuntimeException {
//        currentMissionIsNotNull();
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        final AlertDialog alert = builder.create();
//
//        LayoutInflater inf = getActivity().getLayoutInflater();
//        View view = inf.inflate(R.layout.change_status, null);
//        TextView status = view.findViewById(R.id.current_status);
//
//        status.setText(mMission.getStatus().getLabel().toUpperCase());
//        status.setBackgroundColor(Color.parseColor(mMission.getStatus().getColor()));
//        alert.setView(view);
//        alert.show();
//
//        setOnClickListenersForStatus(alert, view);
//    }
//
//
//    private void setOnClickListenersForStatus(final AlertDialog alert, View view) {
//        MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
//        MapotempoFleetManagerInterface manager = mapotempoApplication.getManager();
//        List<MissionStatusActionInterface> missionStatusTypes = manager.getMissionStatusActionAccessInterface().getByPrevious(mMission.getStatus());
//        ArrayList<MissionsStatusGeneric<LinearLayout, MissionStatusActionInterface>> viewListStatus = buildViewFor(missionStatusTypes, view);
//
//        if (viewListStatus != null && viewListStatus.size() > 0) {
//            for (final MissionsStatusGeneric<LinearLayout, MissionStatusActionInterface> status : viewListStatus) {
//                status.getView().setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // TODO
//                        mMission.setStatus(status.getType().getNextStatus());
//                        mMission.save();
//                        fillViewFromActivity();
//
//                        alert.dismiss();
//                    }
//                });
//            }
//        }
//    }
//
//    private ArrayList<MissionsStatusGeneric<LinearLayout, MissionStatusActionInterface>> buildViewFor(List<MissionStatusActionInterface> missionStatusActions, View view) {
//        ArrayList<MissionsStatusGeneric<LinearLayout, MissionStatusActionInterface>> statusViews = new ArrayList<>();
//
//        for (MissionStatusActionInterface missionStatusType : missionStatusActions) {
//            LinearLayout newLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.single_status, null);
//            LinearLayout layoutContainer = view.findViewById(R.id.status_container);
//            TextView textViewLabel = newLayout.findViewById(R.id.status_label);
//            ImageView imageViewIcon = newLayout.findViewById(R.id.icon);
//            MissionsStatusGeneric<LinearLayout, MissionStatusActionInterface> missionsStatusGeneric;
//
//            String icon = null;
//            String label = missionStatusType.getLabel();
//            String color = missionStatusType.getNextStatus().getColor();
//
//            textViewLabel.setText(label);
//            imageViewIcon.setColorFilter(Color.parseColor(color));
//            layoutContainer.addView(newLayout);
//
//            try {
//                missionsStatusGeneric = new MissionsStatusGeneric(newLayout, missionStatusType);
//                statusViews.add(missionsStatusGeneric);
//            } catch (NullPointerException e) {
//                e.getStackTrace();
//            }
//        }
//
//        return statusViews;
//    }
//
//    private void currentMissionIsNotNull() {
//        if (mMission == null)
//            throw new RuntimeException("Mission is already deleted or is invalid");
//    }
//}
