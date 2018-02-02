package com.mapotempo.lib.fragments.actions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.MissionStatusActionInterface;
import com.mapotempo.lib.R;
import com.mapotempo.lib.fragments.missions.MissionsListFragment;
import com.mapotempo.lib.utils.SVGDrawableHelper;
import com.mapotempo.lib.view.action.MissionActionPanel;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MissionInterface} and makes a call to the
 * specified {@link MissionsListFragment.OnMissionSelectedListener}.
 */
class ActionsRecyclerViewAdapter extends RecyclerView.Adapter<ActionsRecyclerViewAdapter.ViewHolder> {

    private ActionsListFragment.OnMissionActionSelectedListener mListener;
    private int mActionsCount = 0;
    private List<MissionStatusActionInterface> mActions;
    private Context mContext;

    // ===================
    // ==  Constructor  ==
    // ===================

    public ActionsRecyclerViewAdapter(Context context, ActionsListFragment.OnMissionActionSelectedListener listener, List<MissionStatusActionInterface> actions) {
        mActions = actions;
        mActionsCount = actions.size();
        mListener = listener;
        mContext = context;
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
        final MissionActionPanel mActionView;

        public ViewHolder(View view) {
            super(view);
            mActionView = view.findViewById(R.id.status_panel);
            mActionView.setOnClickListener(new View.OnClickListener() {
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
            mActionView.setImageDrawable(drawable);
            mActionView.setText(action.getNextStatus().getLabel());
            mActionView.setBackgroundColor(Color.parseColor(action.getNextStatus().getColor()));
        }
    }
}
