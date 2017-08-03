package mapotempo.com.mapotempo_fleet_android.dummy;

import android.support.annotation.Nullable;

public class MissionModel {
    public int id;
    public String name;
    public String device;
    public String delivery_date;
    public Status status;
    public StatusColors sColor;

    public enum Status {
        PENDING (StatusColors.YELLOW),
        COMPLETED (StatusColors.GREEN),
        UNCOMPLETED (StatusColors.RED);

        private StatusColors color;

        Status(StatusColors color) {
            this.color = color;
        }

        public StatusColors getColor() {
            return this.color;
        }
    }

    public enum StatusColors {
        YELLOW ("#FF8100"),
        GREEN ("#9FF8BE"),
        RED ("#850000");

        private String hexaColor;

        StatusColors(String hexColor) {
            this.hexaColor = hexColor;
        }

        public String getHexaColor() {
            return this.hexaColor;
        }
    }

    public MissionModel(int id, String name, String device, String delivery_date, @Nullable Status status) {
        this.id            = id;
        this.name          = name;
        this.delivery_date = delivery_date;
        this.device = device;
        this.status = (status == null) ? Status.PENDING : status;
        this.sColor = this.status.getColor();
    }

    @Override
    public String toString() {
        return name + " | " + delivery_date;
    }
}