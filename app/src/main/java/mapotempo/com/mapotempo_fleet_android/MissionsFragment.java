package mapotempo.com.mapotempo_fleet_android;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;

import com.mapotempo.fleet.api.MapotempoFleetManagerInterface;
import com.mapotempo.fleet.api.model.MissionInterface;
import com.mapotempo.fleet.api.model.accessor.AccessInterface;
import com.mapotempo.fleet.api.model.accessor.MissionAccessInterface;

import java.util.ArrayList;
import java.util.List;
/**
 * This fragment is responsible for display all missions assigned to the user currently connected.
 * Each view displayed by this fragment is composed of 3 main UI elements :
 * <ul>
 * <li>The name </li>
 * <li>The delivery date</li>
 * <li>The delivery hour</li>
 * </ul>
 *
 * <h3>Integration</h3>
 * First and foremost, it is needed to implement the fragment through XML using the following class : <code> {@literal <fragment class="mapotempo.com.mapotempo_fleet_android.MissionContainerFragment"} </code>
 * <p>
 * This fragment require the implementation of {@link OnMissionsInteractionListener} directly in the Activity that hold the List Fragment.
 * Then Override the {@link OnMissionsInteractionListener#onListMissionsInteraction(int)}
 * which force to return an Android's click Listener. If you don't know about Event Listeners please check the documentation here : <a href="https://developer.android.com/reference/android/view/View.OnClickListener.html" target="_blank">Android Listener</a> <br>
 * Feel free to put any logic inside the listener. Keep in mind that the position returned can be used to get the detailed view of the mission triggered by a click.
 * </p>
 *
 * <b>Here is an example of usability: </b>
 * <pre>
 *     {@literal @Override}
 *     public View.OnClickListener onListMissionsInteraction(final int position) {
 *         View.OnClickListener onClick = new View.OnClickListener() {
 *             {@literal @Override}
 *             public void onClick(View v) {
 *                 intent = new Intent(v.getContext(), YouNewActivity.class);
 *                 intent.putExtra("mission_position", position);
 *                 v.getContext().startActivity(intent);
 *             }
 *         }
 *         return onClick;
 *    }
 * </pre>
 */
public class MissionsFragment extends Fragment {

    private OnMissionsInteractionListener mListener;
    private MapotempoFleetManagerInterface mManager;
    private MissionsRecyclerViewAdapter mRecycler;
    private MissionAccessInterface iMissionAccess;
    private List<MissionInterface> mMissions = new ArrayList<>();
    private int mColumnCount = 1;
    private AccessInterface.ChangeListener<MissionInterface> missionChangeListener = new AccessInterface.ChangeListener<MissionInterface>() {
        @Override
        public void changed(final List<MissionInterface> missions) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MissionContainerFragment singleFragment = (MissionContainerFragment) getFragmentManager().findFragmentById(R.id.base_fragment);

                    mRecycler.notifyDataSyncHasChanged(missions);

                    if (singleFragment != null)
                        singleFragment.refreshPagerData(missions);
                }
            });
        }
    };

    protected RecyclerView recyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMissionsInteractionListener) {
            mListener = (OnMissionsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMissionsInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setManagerAndMissions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missions_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        if (recyclerView instanceof RecyclerView) {

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
            }

            mRecycler = new MissionsRecyclerViewAdapter(getContext(), mListener, mMissions);
            recyclerView.setAdapter(mRecycler);
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (iMissionAccess != null)
            iMissionAccess.removeChangeListener(missionChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        MapotempoApplication app = (MapotempoApplication) getActivity().getApplicationContext();
        if (app.getManager() != null) {
            mRecycler.notifyDataSyncHasChanged(app.getManager().getMissionAccess().getAll());
            if (iMissionAccess != null)
                iMissionAccess.removeChangeListener(missionChangeListener);
            setManagerAndMissions();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecycler = null;
    }

    public void setCurrentMission (int position) {
        if (mRecycler != null)
            mRecycler.setCurrentMission(position);
    }

    private void setManagerAndMissions() {
        MapotempoApplication mapotempoApplication = (MapotempoApplication) getContext().getApplicationContext();
        if (mapotempoApplication.getManager() == null)
            return;

        mManager = mapotempoApplication.getManager();
        iMissionAccess = mManager.getMissionAccess();
        mMissions = iMissionAccess.getAll();

        attachCallBack(iMissionAccess);
    }

    private void attachCallBack(MissionAccessInterface iMissionAccess) {
        iMissionAccess.addChangeListener(missionChangeListener);
    }

    /**
     * This interface must be implemented by activities that contain {@link MissionsFragment}
     */
    public interface OnMissionsInteractionListener {
        /**
         * A Callback triggered when an item list is created. Use it to set a click listener to each of them.
         * @param position return the item list position
         * @return View.OnClickListener
         */
        View.OnClickListener onListMissionsInteraction(int position);
    }
}
