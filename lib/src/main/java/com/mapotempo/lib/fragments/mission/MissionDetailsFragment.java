/*
 * Copyright © Mapotempo, 2018
 *
 * This file is part of Mapotempo.
 *
 * Mapotempo is free software. You can redistribute it and/or
 * modify since you respect the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Mapotempo is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the Licenses for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Mapotempo. If not, see:
 * <http://www.gnu.org/licenses/agpl.html>
 */

package com.mapotempo.lib.fragments.mission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mapotempo.fleet.dao.model.Mission;
import com.mapotempo.fleet.dao.model.MissionAction;
import com.mapotempo.fleet.dao.model.MissionActionType;
import com.mapotempo.fleet.dao.model.MissionStatusType;
import com.mapotempo.fleet.dao.model.nested.Address;
import com.mapotempo.fleet.dao.model.nested.Quantity;
import com.mapotempo.fleet.dao.model.nested.SopacLOG;
import com.mapotempo.fleet.dao.model.nested.TimeWindow;
import com.mapotempo.fleet.manager.MapotempoFleetManager;
import com.mapotempo.lib.MapotempoApplicationInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.actions.ActionsListFragment;
import com.mapotempo.lib.fragments.actions.ActionsRecyclerViewAdapter;
import com.mapotempo.lib.fragments.base.MapotempoBaseFragment;
import com.mapotempo.lib.fragments.survey.SurveyAddressDialogFragment;
import com.mapotempo.lib.fragments.survey.SurveyCommentDialogFragment;
import com.mapotempo.lib.fragments.survey.SurveyPictureDialogFragment;
import com.mapotempo.lib.fragments.survey.SurveyQuantityDialogFragment;
import com.mapotempo.lib.fragments.survey.SurveySignatureDialogFragment;
import com.mapotempo.lib.fragments.survey.SurveySopacNFCTemperatureDialogFragment;
import com.mapotempo.lib.utils.AddressHelper;
import com.mapotempo.lib.utils.AlertNavDialog;
import com.mapotempo.lib.utils.DateHelpers;
import com.mapotempo.lib.utils.PhoneNumberHelper;
import com.mapotempo.lib.utils.SVGDrawableHelper;
import com.mapotempo.lib.utils.StaticMapURLHelper;
import com.mapotempo.lib.view.action.MissionActionPanel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;

public class MissionDetailsFragment extends MapotempoBaseFragment implements
    SurveySignatureDialogFragment.SurveySignatureListener,
    SurveyQuantityDialogFragment.SurveyQuantityListener,
    SurveyPictureDialogFragment.SurveyPictureListener,
    SurveyAddressDialogFragment.SurveyAddressListener,
    SurveyCommentDialogFragment.SurveyCommentListener,
    SurveySopacNFCTemperatureDialogFragment.SurveySopacNFCTemperatureListener
{

    public MissionDetailsFragment()
    {
    }

    private static final String COLOR_GREEN = "56b881";

    private Mission mMission;

    private FrameLayout mMapContainer;
    private ProgressBar mMapLoader;
    private ImageView mMapImageView;
    private ImageView mMapMarker;

    private TextView mMissionName;
    private TextView mMissionReference;

    private LinearLayout mLayoutDeliveryAddress;
    private TextView mTextViewDeliveryAddress;

    private TextView mTextViewHours;
    private TextView mTextViewDate;
    private TextView mTextViewDuration;
    private LinearLayout mLayoutTextViewDuration;

    private LinearLayout mLayoutTimeWindows;
    private LinearLayout mLayoutTimeWindowsContainer;

    private LinearLayout mLayoutPhone;
    private TextView mTextViewPhone;

    private LinearLayout mLayoutComment;
    private TextView mTextViewComment;

    private ImageView mPictureCheckView;
    private ImageView mSignatureCheckView;
    private ImageView mCommentCheckView;

    private MissionActionPanel mStatusCurrent;
    private FloatingActionButton mStatusFirstAction;
    private FloatingActionButton mStatusSecondAction;
    private FloatingActionButton mStatusThirdAction;
    private FloatingActionButton mStatusMoreAction;

    private LinearLayout mLayoutQuantities;
    private LinearLayout mLayoutQuantitiesContainer;

    private LinearLayout mLayoutTemperatures;
    private LinearLayout mLayoutTemperaturesContainer;

    private BottomSheetBehavior mBottomSheetBehavior;

    private boolean mButtonsVisibility = true;

    private OnMissionDetailsFragmentListener mListener;

    static int MAP_IMAGE_WIDTH_QUALITY = 500;

    // ==============
    // ==  Public  ==
    // ==============

    public void fillViewFromActivity()
    {
        if (mMission != null && isAdded())
        {
            fillViewData(mMission);
            initNavigationAction(mMission);
            initMainActionButtons(mMission);
            initActionButtons(mMission);
        }
    }

    public void setMission(Mission mission)
    {
        if (mission != null)
        {
            mMission = mission;
        }
        else
        {
            throw new RuntimeException("Mission passed to constructor must not be null");
        }
    }

    public static MissionDetailsFragment create(int pageNumber, Mission mission)
    {
        MissionDetailsFragment fragment = new MissionDetailsFragment();
        Bundle args = new Bundle();

        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        fragment.setMission(mission);

        return fragment;
    }

    public boolean onBackPressed()
    {
        if (BottomSheetBehavior.STATE_COLLAPSED != mBottomSheetBehavior.getState())
        {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        }
        return false;
    }

    // ===================================
    // ==  Android Fragment Life cycle  ==
    // ===================================

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnMissionDetailsFragmentListener)
        {
            mListener = (OnMissionDetailsFragmentListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement " + OnMissionDetailsFragmentListener.class.getName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_mission, container, false);

        // If the screen isn't too small, hide all buttons from UI.
        mButtonsVisibility = !((getResources().getConfiguration().screenLayout &
            Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL);

        // Map view
        mMapContainer = view.findViewById(R.id.mapContainer);
        mMapImageView = view.findViewById(R.id.mapImageView);
        mMapLoader = view.findViewById(R.id.mapLoader);
        mMapMarker = view.findViewById(R.id.mapMarker);

        // Header view
        mMissionName = view.findViewById(R.id.name);
        mMissionReference = view.findViewById(R.id.reference);
        mStatusCurrent = view.findViewById(R.id.status);

        // Location view
        mLayoutDeliveryAddress = view.findViewById(R.id.delivery_address_layout);
        mTextViewDeliveryAddress = view.findViewById(R.id.delivery_address);

        // Date view
        mTextViewHours = view.findViewById(R.id.delivery_planned_hours);
        mTextViewDate = view.findViewById(R.id.delivery_planned_date);
        mTextViewDuration = view.findViewById(R.id.delivery_duration);
        mLayoutTextViewDuration = view.findViewById(R.id.delivery_duration_layout);

        // TimeWindows view
        mLayoutTimeWindows = view.findViewById(R.id.time_windows_layout);
        mLayoutTimeWindowsContainer = view.findViewById(R.id.time_windows_container);

        // Phone view
        mLayoutPhone = view.findViewById(R.id.phone_layout);
        mTextViewPhone = view.findViewById(R.id.phone);

        // Comment view
        mLayoutComment = view.findViewById(R.id.comment_layout);
        mTextViewComment = view.findViewById(R.id.comment);

        // Attachment view
        mPictureCheckView = view.findViewById(R.id.picture_check);
        mSignatureCheckView = view.findViewById(R.id.signature_check);
        mCommentCheckView = view.findViewById(R.id.comment_check);

        // Action button
        mStatusFirstAction = view.findViewById(R.id.first_action);
        mStatusSecondAction = view.findViewById(R.id.second_action);
        mStatusThirdAction = view.findViewById(R.id.third_action);
        mStatusMoreAction = view.findViewById(R.id.more_action);

        mMapImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onMapImageViewClick(mMission);
            }
        });

        // Quantities view
        mLayoutQuantities = view.findViewById(R.id.quantities_layout);
        mLayoutQuantitiesContainer = view.findViewById(R.id.quantities_container);

        // Temperatures view
        mLayoutTemperatures = view.findViewById(R.id.temperatures_layout);
        mLayoutTemperaturesContainer = view.findViewById(R.id.temperatures_container);

        return view;
    }

    //
    @Override
    public void onResume()
    {
        super.onResume();
        fillViewFromActivity();
        initBottomSheet(getView());
    }


    // ===============================
    // ==  SurveySignatureListener  ==
    // ===============================

    @Override
    public boolean onSignatureSave(Bitmap signatureBitmap, String signatoryName)
    {
        mMission.setSurveySignature(signatureBitmap);
        mMission.setSurveySignatoryName(signatoryName);
        return mMission.save();
    }

    @Override
    public boolean onSignatureClear()
    {
        mMission.clearSurveySignature();
        mMission.clearSurveySignatoryName();
        return mMission.save();
    }

    // ==============================
    // ==  SurveyQuantityDialogFragment  ==
    // ==============================

    @Override
    public boolean onQuantitySave(List<Quantity> editQuantities)
    {
        mMission.setQuantities(editQuantities);
        return mMission.save();
    }

    @Override
    public boolean onQuantityClear()
    {
        List<Quantity> quantities = mMission.getQuantities();
        for (Quantity quantity : quantities)
        {
            quantity.removeSurveyQuantity();
        }
        mMission.setQuantities(quantities);
        return mMission.save();
    }

    // ==============================
    // ==  SurveyPictureListener  ==
    // ==============================

    @Override

    public boolean onPictureSave(Bitmap signatureBitmap)
    {
        mMission.setSurveyPicture(signatureBitmap);
        return mMission.save();
    }

    @Override
    public boolean onPictureClear()
    {
        mMission.clearSurveyPicture();
        return mMission.save();
    }

    // ==============================
    // ==  SurveyPictureListener  ==
    // ==============================

    @Override
    public boolean onAddressSave(Address address)
    {
        mMission.setSurveyAddress(address);
        return mMission.save();
    }

    @Override
    public boolean onAddressClear()
    {
        mMission.clearSurveyAddress();
        return mMission.save();
    }

    // =============================
    // ==  SurveyCommentListener  ==
    // =============================

    @Override
    public boolean onCommentSave(String comment)
    {
        mMission.setSurveyComment(comment);
        return mMission.save();
    }

    @Override
    public boolean onCommentClear()
    {
        mMission.clearSurveyComment();
        return mMission.save();
    }

    // ========================================
    // ==  SurveySopacNFCTemperatureListener ==
    // ========================================


    @Override
    public boolean onSopacNFCSave(List<SopacLOG> sopacLOGs)
    {
        return true;
    }

    @Override
    public boolean onSopacNFCClear()
    {
        return true;
    }

    @Override
    public boolean onSopacNFCClearOne(List<SopacLOG> sopacLOGs)
    {
        mMission.setSurveySopacLOGS(sopacLOGs);
        return mMission.save();
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void goSurveySignatureFragment()
    {
        String tag = "signatureDialog";
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);
        SurveySignatureDialogFragment newFragment = SurveySignatureDialogFragment.newInstance(mMission.getSurveySignature(), mMission.getSurveySignatoryName());
        newFragment.setSurveySignatureListener(this);
        newFragment.show(ft, tag);
    }

    public void goSurveyPictureFragment()
    {
        String tag = "pictureDialog";
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);
        SurveyPictureDialogFragment newFragment = SurveyPictureDialogFragment.newInstance(mMission.getSurveyPicture());
        newFragment.setSurveyPictureListener(this);
        newFragment.show(ft, tag);
    }

    public void goSurveyQuantitiesFragment()
    {
        String tag = "quantityDialog";
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);
        SurveyQuantityDialogFragment newFragment = SurveyQuantityDialogFragment.newInstance(mMission.getQuantities());
        newFragment.setListener(this);
        newFragment.show(ft, tag);
    }

    public void goSurveyAddressFragment()
    {
        String tag = "addressDialog";
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);
        SurveyAddressDialogFragment newFragment = SurveyAddressDialogFragment.newInstance(mMission.getSurveyAddress().isValid() ? mMission.getSurveyAddress() : mMission
            .getAddress());
        newFragment.setSurveyAddressListener(this);
        newFragment.show(ft, tag);
    }

    public void goSurveyCommentFragment()
    {
        String tag = "commentDialog";
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);
        SurveyCommentDialogFragment newFragment = SurveyCommentDialogFragment.newInstance(mMission.getSurveyComment());
        newFragment.setSurveyCommentListener(this);
        newFragment.show(ft, tag);
    }

    public void goSurveySopacTemperatureFragment(@Nullable Long sopacId)
    {
        String tag = "sopacDialog";
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);
        SurveySopacNFCTemperatureDialogFragment newFragment = SurveySopacNFCTemperatureDialogFragment.newInstance(mMission
            .getSurveySopacLOGS(), sopacId);
        newFragment.setSurveySopacNFCTemperatureListener(this);
        newFragment.show(ft, tag);
    }

    // ===============
    // ==  Private  ==
    // ===============

    private void initBottomSheet(View view)
    {
        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                switch (newState)
                {
                case BottomSheetBehavior.STATE_COLLAPSED:
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {

            }
        });

        View.OnClickListener listenerMore = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
                MapotempoFleetManager manager = mapotempoApplication.getManager();
                List<MissionActionType> actions = manager.getMissionActionTypeAccessInterface().byPreviousStatusType(mMission.getStatusType());
                if (actions.size() > 0)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        };
        mStatusMoreAction.setOnClickListener(listenerMore);
        mStatusCurrent.setOnClickListener(listenerMore);
    }

    private void initNavigationAction(final Mission mission)
    {
        mLayoutDeliveryAddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mMission != null)
                {
                    com.mapotempo.fleet.dao.model.nested.Location loc = (mission.getSurveyLocation().isValid()) ? mission.getSurveyLocation() :
                        mission.getLocation();
                    // Check lat/lon object
                    if (loc.isValid())
                    {
                        Intent fakeIntent = new Intent(Intent.ACTION_VIEW);
                        fakeIntent.setData(Uri.parse("geo:0,0"));
                        PackageManager packageManager = getActivity().getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(fakeIntent, 0);
                        try
                        {
                            AlertNavDialog.Builder dialog = new AlertNavDialog.Builder(MissionDetailsFragment.this.getContext());
                            dialog.setView(R.layout.navs_launcher_grid)
                                .setActivities(activities)
                                .setPackageManager(packageManager)
                                .setLat(loc.getLat())
                                .setLng(loc.getLon())
                                .setOnClick(new AlertNavDialog.OnMapsAppSelected()
                                {
                                    @Override
                                    public void onSelected(Intent mapIntent)
                                    {
                                        if (isAdded())
                                        {
                                            startActivity(mapIntent);
                                        }
                                    }
                                })
                                .build()
                                .show();
                        } catch (MissingResourceException e)
                        {
                            System.out.print(e);
                        }
                    }
                }
            }
        });
    }

    private void fillViewData(Mission mission)
    {
        mapVisibilityManager();
        detailsContentManager(mission);
        detailsVisibilityManager();
    }

    private void mapVisibilityManager()
    {
        final com.mapotempo.fleet.dao.model.nested.Location location = mMission.getSurveyLocation().isValid() ? mMission.getSurveyLocation() : mMission.getLocation();
        final MissionDetailsFragment INSTANCE = this;

        // Asynchronously fill the mapImageView when the widget is draw to retrieve dimensions.
        if (location.isValid())
        {
            mMapContainer.setVisibility(View.VISIBLE);
            ViewTreeObserver vto = mMapImageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {
                public boolean onPreDraw()
                {
                    if (INSTANCE.isAdded())
                    {
                        mMapImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                        // Init visibility
                        mMapLoader.setVisibility(View.VISIBLE);
                        mMapImageView.setVisibility(View.GONE);
                        mMapMarker.setVisibility(View.GONE);

                        // Coloration depend of marker type
                        if (mMission.getSurveyLocation().isValid())
                            mMapMarker.setColorFilter(getResources().getColor(R.color.colorMapoBlue));
                        else
                            mMapMarker.setColorFilter(getResources().getColor(R.color.colorMapoGreen));

                        // Prepare Request
                        int x = mMapImageView.getMeasuredWidth();
                        int y = mMapImageView.getMeasuredHeight();
                        double ratio = (double) y / (double) x;
                        x = MAP_IMAGE_WIDTH_QUALITY;
                        y = (int) (x * ratio);

                        String MapUrl = new StaticMapURLHelper.TileHostingURLBuilder()
                            .setBaseURL(getString(R.string.tilehosting_base_url))
                            .setKey(getString(R.string.tilehosting_access_token))
                            .setLat(location.getLat())
                            .setLon(location.getLon())
                            .setWidth(x)
                            .setHeight(y)
                            .setZoom(18)
                            .Build();

                        // Init request with Picasso
                        Picasso.with(getActivity())
                            .load(MapUrl)
                            .error(R.drawable.bg_world_mapbox_v10)
                            .into(mMapImageView, new Callback()
                            {
                                @Override
                                public void onSuccess()
                                {
                                    mMapLoader.setVisibility(View.GONE);
                                    mMapImageView.setVisibility(View.VISIBLE);
                                    mMapMarker.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onError()
                                {
                                    mMapLoader.setVisibility(View.GONE);
                                    mMapImageView.setVisibility(View.VISIBLE);
                                    mMapMarker.setVisibility(View.GONE);
                                }
                            });

                        return true;
                    }
                    return true;
                }
            });
        }
        else
            mMapContainer.setVisibility(View.GONE);
    }

    private void detailsContentManager(Mission mission)
    {
        // - Name and Reference
        mMissionName.setText(mission.getName());
        mMissionReference.setText(mission.getReference());

        // - Date
        mTextViewHours.setText(DateHelpers.parse(mission.getETAOrDefault(), DateHelpers.DateStyle.HOURMINUTES));
        mTextViewDate.setText(DateHelpers.parse(mission.getETAOrDefault(), DateHelpers.DateStyle.SHORTDATE));
        mTextViewDuration.setText(DateHelpers.FormatedHour(getContext(), mission.getDuration()));

        Address addressInterface;

        if (mission.getSurveyAddress().isValid())
            addressInterface = mission.getSurveyAddress();
        else
            addressInterface = mission.getAddress();

        String fullAdress = String.format("%s%s%s %s%s",
            AddressHelper.addBackDashIfNonNull(addressInterface.getStreet(), ""),
            AddressHelper.addBackDashIfNonNull(addressInterface.getDetail(), ""),
            addressInterface.getPostalcode(),
            AddressHelper.addBackDashIfNonNull(addressInterface.getCity(), ""),
            addressInterface.getCountry().trim());

        // - Address
        mTextViewDeliveryAddress.setText(fullAdress);

        // - Phone number
        mTextViewPhone.setText(PhoneNumberHelper.intenationalPhoneNumber(mission.getPhone()));

        // - Comment
        mTextViewComment.setText(mission.getComment());

        // - TimeWindows
        mLayoutTimeWindowsContainer.removeAllViews();
        for (TimeWindow tw : mMission.getTimeWindows())
        {
            TextView textView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.time_windows_text_view, null);
            String date = String.format("%s - %s", DateHelpers.parse(tw.getStart(), DateHelpers.DateStyle.HOURMINUTES), DateHelpers.parse(tw.getEnd(), DateHelpers.DateStyle.HOURMINUTES));
            textView.setText(date);
            mLayoutTimeWindowsContainer.addView(textView);
        }

        // - Quantities
        mLayoutQuantities.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goSurveyQuantitiesFragment();
            }
        });
        mLayoutQuantitiesContainer.removeAllViews();
        List<Quantity> quantities = mMission.getQuantities();
        for (Quantity quantity : quantities)
        {
            if (!quantity.isValid()) { continue; }

            View quantityLayout = getLayoutInflater().inflate(R.layout.quantity_layout, null);

            TextView icon = quantityLayout.findViewById(R.id.quantity_icon);
            TextView label = quantityLayout.findViewById(R.id.quantity_label);

            // PREPARE ICONS WITH FONT AWESOME LIBRARY
            String iconName = quantity.getUnitIcon().replace("-", "_icon_");
            String fontIcon;

            try
            {
                fontIcon = getString(getResources().getIdentifier(iconName, "string", getActivity().getPackageName()));
            } catch (Resources.NotFoundException e)
            {
                Log.e("Ressource Not Found", e.getMessage());
                fontIcon = getString(R.string.fa_icon_archive);
            }

            icon.setText(fontIcon);
            icon.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome-webfont.ttf"));

            // SET LABEL AND VALUE
            label.setText(String.valueOf(quantity.getPreferedQuantity()) + " " + quantity.getLabel());

            mLayoutQuantitiesContainer.addView(quantityLayout);
        }

        // - Temperatures
        mLayoutTemperaturesContainer.removeAllViews();
        List<SopacLOG> sopacLOGList = mMission.getSurveySopacLOGS();
        for (final SopacLOG sopacLOG : sopacLOGList)
        {
            if (!sopacLOG.isValid()) { continue; }

            View temperatureLayout = getLayoutInflater().inflate(R.layout.temperature_layout, null);
            temperatureLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    goSurveySopacTemperatureFragment(sopacLOG.getTagID());
                }
            });

            TextView device_id = temperatureLayout.findViewById(R.id.device_id);
            TextView temperature = temperatureLayout.findViewById(R.id.temperature);
            ImageView warning = temperatureLayout.findViewById(R.id.warning);
            device_id.setText(Long.toString(sopacLOG.getTagID()));
            temperature.setText(Float.toString(sopacLOG.getTemperatures().get(sopacLOG.getTemperatures().size() - 1).getValue()) + sopacLOG.getUnity());
            warning.setVisibility(sopacLOG.getBreaches() > 0 ? View.VISIBLE : View.INVISIBLE);
            mLayoutTemperaturesContainer.addView(temperatureLayout);
        }
    }

    private void detailsVisibilityManager()
    {
        mMissionReference.setVisibility(isEmptyTextView(mMissionReference) ? View.VISIBLE : View.GONE);
        mLayoutTextViewDuration.setVisibility(isEmptyTextView(mTextViewDuration) ? View.VISIBLE : View.GONE);
        mLayoutDeliveryAddress.setVisibility((isEmptyTextView(mTextViewDeliveryAddress) ? View.VISIBLE : View.GONE));
        mLayoutTimeWindows.setVisibility((mLayoutTimeWindowsContainer.getChildCount() > 0 ? View.VISIBLE : View.GONE));
        mLayoutQuantities.setVisibility((mLayoutQuantitiesContainer.getChildCount() > 0 ? View.VISIBLE : View.GONE));
        mLayoutTemperatures.setVisibility((mLayoutTemperaturesContainer.getChildCount() > 0 ? View.VISIBLE : View.GONE));
        mLayoutPhone.setVisibility(isEmptyTextView(mTextViewPhone) ? View.VISIBLE : View.GONE);
        mLayoutComment.setVisibility(isEmptyTextView(mTextViewComment) ? View.VISIBLE : View.GONE);
        mPictureCheckView.setVisibility(mMission.getSurveyPicture() != null ? View.VISIBLE : View.GONE);
        mCommentCheckView.setVisibility(mMission.getSurveyComment() != null ? View.VISIBLE : View.GONE);
        mSignatureCheckView.setVisibility(mMission.getSurveySignature() != null ? View.VISIBLE : View.GONE);
    }

    private boolean isEmptyTextView(TextView textView)
    {
        return textView.getText().toString().trim().length() > 0;
    }

    private void initActionButtons(final Mission mission)
    {
        List<MissionActionType> actions = mMission.getStatusType().getNextActionType();
        ActionsListFragment fragment = (ActionsListFragment) getChildFragmentManager().findFragmentById(R.id.actions_fragment);
        if (fragment != null)
        {
            fragment.setActions(actions);
            fragment.setOnActionSelectedListener(new ActionsRecyclerViewAdapter.OnMissionActionSelectedListener()
            {
                @Override
                public void onMissionActionSelected(MissionActionType action)
                {

                    doAction(mission, action);
                    initMainActionButtons(mission);
                    initActionButtons(mMission);
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
        }
    }

    private void initMainActionButtons(final Mission mission)
    {
        // Current status
        MissionStatusType statusType = mission.getStatusType();
        Drawable drawable = SVGDrawableHelper.getDrawableFromSVGPath(statusType.getSVGPath(), "#ffffff", new BitmapDrawable());
        mStatusCurrent.setBackgroundColor(Color.parseColor(statusType.getColor()));
        mStatusCurrent.setImageDrawable(drawable);
        mStatusCurrent.setText(statusType.getLabel());

        // Next actions
        int idx = 0;
        FloatingActionButton floatingActionButtonArray[] = {mStatusFirstAction, mStatusSecondAction, mStatusThirdAction};
        final MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        MapotempoFleetManager manager = mapotempoApplication.getManager();
        List<MissionActionType> actions = manager.getMissionActionTypeAccessInterface().byPreviousStatusType(mission.getStatusType());

        for (FloatingActionButton button : floatingActionButtonArray)
        {
            if (idx < actions.size() && mButtonsVisibility)
            {
                final MissionActionType action = actions.get(idx);
                Drawable d = SVGDrawableHelper.getDrawableFromSVGPath(action.getNextStatus().getSVGPath(), "#ffffff", new BitmapDrawable());
                button.setImageDrawable(d);
                button.show();
                button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(action.getNextStatus().getColor())));
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        doAction(mission, action);
                    }
                });
            }
            else
                button.setVisibility(View.GONE);
            idx++;
        }

        if (actions.size() <= 3)
            mStatusMoreAction.setVisibility(View.GONE);
        else
            mStatusMoreAction.setVisibility(View.VISIBLE);
    }

    private void doAction(Mission mission, MissionActionType action)
    {
        final MapotempoApplicationInterface mapotempoApplication = (MapotempoApplicationInterface) getActivity().getApplicationContext();
        MapotempoFleetManager manager = mapotempoApplication.getManager();

        /**
         * Create a new location
         * TODO: Refactor me at getNativeLocation Call
         */
        Location loc = getNativeLocation();
        com.mapotempo.fleet.dao.model.nested.Location locationInterface = null;

        if (loc != null)
            locationInterface = new com.mapotempo.fleet.dao.model.nested.Location(manager, loc.getLatitude(), loc.getLongitude());

        MissionAction ma = manager.getMissionActionAccessInterface().create(
            manager.getCompany(),
            mMission,
            action,
            new Date(),
            locationInterface);

        // Set mission status type
        mission.setStatus(action.getNextStatus());
        mission.save();
    }

    /**
     * Try to get the current Location
     * TODO: DELETE ME, Code redundant with MapLocationPickerFragment.getNativeLocation method
     *
     * @return
     */
    private Location getNativeLocation()
    {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationManager mLocMngr = (LocationManager) getActivity().getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            if (mLocMngr.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                return mLocMngr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            else if (mLocMngr.isProviderEnabled(LocationManager.GPS_PROVIDER))
                return mLocMngr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return null;
    }
}
