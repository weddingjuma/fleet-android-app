package mapotempo.com.mapotempo_fleet_android;

import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.TextView;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.View;

import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.core.model.Mission;

import java.util.ArrayList;
import java.util.List;

import mapotempo.com.mapotempo_fleet_android.MissionsFragment.OnMissionsInteractionListener;
import mapotempo.com.mapotempo_fleet_android.utils.DateHelpers;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Mission} and makes a call to the
 * specified {@link MissionsFragment.OnMissionsInteractionListener}.
 */
public class MissionsRecyclerViewAdapter extends RecyclerView.Adapter<MissionsRecyclerViewAdapter.ViewHolder> {

    private OnMissionsInteractionListener mListener;
    private Context mContext;
    private List<View> mListViews = new ArrayList<>();
    private boolean orientLandscape;

    private int missionsCount = 0;
    private List<MissionInterface> mMissions;


    private int mCurrentPositionInView = 0;

    public MissionsRecyclerViewAdapter(Context context, OnMissionsInteractionListener listener, List<MissionInterface> missions) {
        mMissions = missions;
        missionsCount = missions.size();
        mContext = context;
        mListener = listener;
        orientLandscape = (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_missions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MissionInterface mission = mMissions.get(position);

        String missionDate = DateHelpers.parse(mission.getDeliveryDate(), DateHelpers.DateStyle.SHORTDATE);
        String missionHour = DateHelpers.parse(mission.getDeliveryDate(), DateHelpers.DateStyle.HOURMINUTES);

        holder.mItem = mission;
        holder.mName.setText(mission.getName());
        holder.mCompany.setText(mission.getCompanyId());
        holder.mDelivery_date.setText(missionDate);
        holder.mDelivery_hour.setText(missionHour);
        holder.mStatus.setBackgroundColor(Color.parseColor("#" + mission.getStatus().getColor()));
        holder.mView.setOnClickListener(mListener.onListMissionsInteraction(position));

        mListViews.add(holder.mView);

        if (position == 0 && mCurrentPositionInView == 0)
            setCurrentMission(0);
    }

    public void setCurrentMission(int position) {
        if (mListViews.size() > 0 && orientLandscape) {
            View newView = mListViews.get(position);
            View oldView = mListViews.get(mCurrentPositionInView);

            newView.setBackgroundColor(Color.parseColor("#e2ecfd"));
            if (position != mCurrentPositionInView)
                oldView.setBackgroundColor(Color.WHITE);

            mCurrentPositionInView = position;
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        mListViews = null;
        mListener = null;
        mContext = null;
    }

    @Override
    public int getItemCount() {
        return missionsCount;
    }

    public void notifyDataSyncHasChanged(List<MissionInterface> missions) {
        mMissions = missions;
        missionsCount = missions.size();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public MissionInterface mItem;
        public final View mView;
        public final TextView mName;
        public final TextView mCompany;
        public final RelativeLayout mStatus;
        public final TextView mDelivery_hour;
        public final TextView mDelivery_date;

        public ViewHolder(View view) {
            super(view);

            mView = view;
            mName = view.findViewById(R.id.name);
            mCompany = view.findViewById(R.id.company);
            mStatus = view.findViewById(R.id.mission_status);
            mDelivery_hour = view.findViewById(R.id.delivery_hour);
            mDelivery_date = view.findViewById(R.id.delivery_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
