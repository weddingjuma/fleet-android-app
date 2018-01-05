package com.mapotempo.lib.actions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.MissionStatusActionInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.missions.MissionsListFragment;
import com.mapotempo.lib.utils.SVGDrawableHelper;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MissionInterface} and makes a call to the
 * specified {@link MissionsListFragment.OnMissionSelectedListener}.
 */
class ActionsRecyclerViewAdapter extends RecyclerView.Adapter<ActionsRecyclerViewAdapter.ViewHolder> {

    private ActionsListFragment.OnMissionActionSelectedListener mListener;
    private int mActionsCount = 0;
    private List<MissionStatusActionInterface> mActions;

    // ===================
    // ==  Constructor  ==
    // ===================

    public ActionsRecyclerViewAdapter(Context context, ActionsListFragment.OnMissionActionSelectedListener listener, List<MissionStatusActionInterface> actions) {
        mActions = actions;
        mActionsCount = actions.size();
        mListener = listener;
    }

    // ======================================
    // ==  RecyclerView.Adapter Interface  ==
    // ======================================

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_actions_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MissionStatusActionInterface action = mActions.get(position);
        holder.setMission(action, position);
    }

    @Override
    public int getItemCount() {
        return mActionsCount;
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void notifyDataSyncHasChanged(List<MissionStatusActionInterface> actions) {
        mActions = actions;
        mActionsCount = actions.size();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MissionStatusActionInterface mItem;
        final View mView;
        final ImageView mIcon;
        final TextView mDescription;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIcon = view.findViewById(R.id.icon_view);
            mDescription = view.findViewById(R.id.description_view);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMissionActionSelected(mItem.getNextStatus());
                }
            });
        }

        void setMission(MissionStatusActionInterface action, final int position) {
            mItem = mActions.get(position);
            Drawable d = new BitmapDrawable();
            Drawable drawable = SVGDrawableHelper.getDrawableFromSVGPath(action.getNextStatus().getSVGPath(), "#FFFFFF", d);
            mIcon.setImageDrawable(drawable);
            mIcon.setBackgroundColor(Color.parseColor(action.getNextStatus().getColor()));
            mDescription.setText(action.getLabel());
        }
    }
}
