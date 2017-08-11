package mapotempo.com.mapotempo_fleet_android.dummy;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by julien on 04/08/17.
 */

public class MissionsManager {
    private static final MissionsManager ourInstance = new MissionsManager();

    public static MissionsManager getInstance() {
        return ourInstance;
    }

    private MissionsManager() {
    }

    private ArrayList<Integer> orderedMissions = new ArrayList<Integer>();
    private Map<Integer, MissionModel> missionModelHashMap = new HashMap<Integer, MissionModel>();

    private ArrayList<Integer> orderedMissionsHistoric = new ArrayList<Integer>();
    private Map<Integer, MissionModel> missionModelHashMapHistoric = new HashMap<>();

    private void addItem(MissionModel item) {
        this.orderedMissions.add(item.id);
        this.missionModelHashMap.put(item.id, item);
    }

    static public int fakeStatusColor() {
        List<MissionModel.Status> statusList = new ArrayList<>();
        int randomStatusNumber = (int) (Math.random() * (2) + 1);

        statusList.add(MissionModel.Status.PENDING);
        statusList.add(MissionModel.Status.COMPLETED);
        statusList.add(MissionModel.Status.UNCOMPLETED);

        MissionModel.Status s = statusList.get(randomStatusNumber);

        MissionModel.StatusColors color = s.getColor();

        return Color.parseColor(color.getHexaColor());
    }

    private MissionModel createMissionItem(int position) {


        int id = 5 + (int)(Math.random() * ((100 - 5) + 1));
        int namesId = 0 + (int)(Math.random() * ((4 - 0) + 1));
        Date d = new Date();
        List<String> names = new ArrayList<>();

        names.add("Paris");
        names.add("London");
        names.add("Royan");
        names.add("Bordeaux");
        names.add("Nantes");

        String dateFormatted = "0" + d.getDay() + "/" + "0" + d.getMonth() + " (" + id + ")";
        String name          = names.get(namesId) + "(" + Integer.toString(position) + ")";
        String delivery_date =  dateFormatted;
        String device        = "GPS(Fleet)";



        return new MissionModel(position, name, device, delivery_date, null);
    }

    public Map<Integer, MissionModel> emuleAsetOfFakeMissions(int maxMissions) {
        for (int i = 0; i < maxMissions; i++) {
            addItem(this.createMissionItem(i));
        }

        return this.missionModelHashMap;
    }

    public MissionModel getFirstMission() {
        if (this.missionModelHashMap.isEmpty()) {
            return new MissionModel(0, "fake", "fake", "2222", null);
        }
        return this.missionModelHashMap.get(this.orderedMissions.get(0));
    }

    public MissionModel getMissionById(int id) {
        if (this.missionModelHashMap.containsKey(id)) {
            return this.missionModelHashMap.get(id);
        } else {
            throw new RuntimeException("Mission id doesn't exist");
        }
    }

    public String getColorForMissionId(int id) {
        return this.missionModelHashMap.get(id).status.getColor().getHexaColor();
    }

    public int getMissionId(int position) {
        return this.orderedMissions.get(position);
    }

    public boolean removeMission(int id) {
        if (this.missionModelHashMap.containsKey(id)) {
            int listIndex = this.orderedMissions.indexOf(id);

            // Keep track of mission deleted
            this.missionModelHashMapHistoric.put(id, this.missionModelHashMap.get(id));
            this.orderedMissionsHistoric.add(id);

            this.missionModelHashMap.remove(id);
            this.orderedMissions.remove(listIndex);

            return true;
        } else {
//            throw new RuntimeException("Mission id doesn't exist");
            return true;
        }
    }

    public int getIndexOf(int id) {
        return (this.orderedMissions.indexOf(id));
    }

    public void setMissionStatusTo(MissionModel.Status s, int id) {
        this.missionModelHashMap.get(id).status = s;
    }

    public int getMissionsCount() {
        return this.orderedMissions.size();
    }
}