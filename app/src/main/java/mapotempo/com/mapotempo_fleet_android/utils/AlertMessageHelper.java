package mapotempo.com.mapotempo_fleet_android.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import mapotempo.com.mapotempo_fleet_android.R;

public class AlertMessageHelper {

    private AlertMessageHelper() { }

    /**
     * Public helper which will display an alert box with text/xmlID given
     * @param context The context where the alert must be displayed
     * @param customView the view to be Inflate.
     * @param title Custom title
     * @param message Custom message
     * @param details Custom Details
     */
    public static void errorAlert(Context context, Integer customView, int title, int message, int details) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alert = builder.create();
        LayoutInflater inflater = LayoutInflater.from(context);
        View box;

        if (customView == null) {
            box = inflater.inflate(R.layout.alert_dialog, null);
        } else {
            box = inflater.inflate(customView, null);
        }

        TextView titleView = box.findViewById(R.id.dialog_title);
        TextView errorView = box.findViewById(R.id.dialog_message);
        TextView detailsView = box.findViewById(R.id.dialog_details);

        titleView.setText(title);
        errorView.setText(message);
        detailsView.setText(details);

        alert.setView(box);
        alert.show();
    }
}
