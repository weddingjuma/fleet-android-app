package mapotempo.com.mapotempo_fleet_android.mission;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapotempo.fleet.api.model.MissionInterface;

import java.util.List;

import mapotempo.com.mapotempo_fleet_android.R;
import mapotempo.com.mapotempo_fleet_android.mission.MissionsListFragment.OnMissionSelectedListener;
import mapotempo.com.mapotempo_fleet_android.utils.DateHelpers;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MissionInterface} and makes a call to the
 * specified {@link OnMissionSelectedListener}.
 */
public class MissionsRecyclerViewAdapter extends RecyclerView.Adapter<MissionsRecyclerViewAdapter.ViewHolder> {

    private MissionsListFragment.OnMissionSelectedListener mListener;
    private int missionsCount = 0;
    private List<MissionInterface> mMissions;
    private int mMissionFocus = 0;

    // ===================
    // ==  Constructor  ==
    // ===================

    public MissionsRecyclerViewAdapter(Context context, OnMissionSelectedListener listener, List<MissionInterface> missions) {
        mMissions = missions;
        missionsCount = missions.size();
        mListener = listener;
    }

    // ======================================
    // ==  RecyclerView.Adapter Interface  ==
    // ======================================

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_missions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MissionInterface mission = mMissions.get(position);
        holder.setMission(mission, position);
        holder.setBackgroundFocus(position == mMissionFocus);
    }

    @Override
    public int getItemCount() {
        return missionsCount;
    }

    // ==============
    // ==  Public  ==
    // ==============

    public void setMissionFocus(int position) {
        int oldPosition = mMissionFocus;
        mMissionFocus = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(mMissionFocus);
    }

    public void notifyDataSyncHasChanged(List<MissionInterface> missions) {
        mMissions = missions;
        missionsCount = missions.size();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MissionInterface mItem;
        final View mView;
        final TextView mName;
        final TextView mCompany;
        final RelativeLayout mStatus;
        final TextView mDelivery_hour;
        final TextView mDelivery_date;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.name);
            mCompany = view.findViewById(R.id.company);
            mStatus = view.findViewById(R.id.mission_status);
            mDelivery_hour = view.findViewById(R.id.delivery_hour);
            mDelivery_date = view.findViewById(R.id.delivery_date);
        }

        void setMission(MissionInterface mission, final int position) {
            String missionDate = DateHelpers.parse(mission.getDate(), DateHelpers.DateStyle.SHORTDATE);
            String missionHour = DateHelpers.parse(mission.getDate(), DateHelpers.DateStyle.HOURMINUTES);
            mItem = mission;
            mName.setText(mission.getName());
            mCompany.setText(mission.getCompanyId());
            mDelivery_date.setText(missionDate);
            mDelivery_hour.setText(missionHour);
            mStatus.setBackgroundColor(Color.parseColor(mission.getStatus().getColor()));
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onMissionSelected(position);
                }
            });
        }

        void setBackgroundFocus(boolean focus) {
            if (focus)
                mView.setBackgroundColor(Color.parseColor("#e2ecfd"));
            else
                mView.setBackgroundColor(Color.WHITE);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
