package mapotempo.com.mapotempo_fleet_android;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.TextView;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import mapotempo.com.mapotempo_fleet_android.MissionsFragment.OnMissionsInteractionListener;
import mapotempo.com.mapotempo_fleet_android.dummy.MissionsManager;
import mapotempo.com.mapotempo_fleet_android.dummy.MissionModel;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MissionModel} and makes a call to the
 * specified {@link MissionsFragment.OnMissionsInteractionListener}.
 */
public class MissionsRecyclerViewAdapter extends RecyclerView.Adapter<MissionsRecyclerViewAdapter.ViewHolder> {

    private final OnMissionsInteractionListener mListener;
    private final MissionsManager mManager;
    private final Context mContext;
    private List<View> mListViews = new ArrayList<>();
    private int mCurrentPositionView = 0;

    public MissionsRecyclerViewAdapter(Context context, OnMissionsInteractionListener listener) {
        mManager = MissionsManager.getInstance();
        mContext = context;

        if (listener instanceof OnMissionsInteractionListener && listener != null) {
            mListener = listener;
        } else {
            throw new RuntimeException("Listener call back must be initialized on : " + context.getClass().getName());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_missions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //holder is a mission
        int missionId = mManager.getMissionId(position);
        final MissionModel mission = mManager.getMissionById(missionId);

        holder.mItem = mission;
        holder.mName.setText(mission.name);
        holder.mDevice.setText(mission.device);
        holder.mDelivery_date.setText(mission.delivery_date);
        holder.mDevice.setText(mission.device);
        holder.mStatus.setBackgroundColor(Color.parseColor(mManager.getColorForMissionId(missionId)));

        holder.mView.setOnClickListener(mListener.onListMissionsInteraction(mission));
        holder.mView.setBackgroundColor((position == 0) ? Color.RED : Color.WHITE);

        mListViews.add(holder.mView);

        checkMissionStatus(holder);
    }

    private void checkMissionStatus(final ViewHolder holder) {
        AppCompatImageButton checkBtn = holder.mView.findViewById(R.id.check_button);
        if (checkBtn == null) return;

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.setMissionStatusTo(MissionModel.Status.COMPLETED, holder.mItem.id);
            }
        });
    }

    public void setCurrentMission(int position) {
        if (mListViews.size() > 0) {
            View newView = mListViews.get(position);
            View oldView = mListViews.get(mCurrentPositionView);

            newView.setBackgroundColor(Color.RED);
            oldView.setBackgroundColor(Color.WHITE);

            mCurrentPositionView = position;
        }
    }

    @Override
    public int getItemCount() {
        return mManager.getMissionsCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName;
        public final TextView mDevice;
        public final TextView mDelivery_date;
        public final RelativeLayout mStatus;
        public MissionModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = view.findViewById(R.id.name);
            mDevice = view.findViewById(R.id.device);
            mStatus = view.findViewById(R.id.mission_status);
            mDelivery_date = view.findViewById(R.id.delivery_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
