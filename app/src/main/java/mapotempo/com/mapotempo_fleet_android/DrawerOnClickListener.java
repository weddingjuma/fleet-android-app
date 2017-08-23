package mapotempo.com.mapotempo_fleet_android;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DrawerOnClickListener implements ListView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case 0:
                break;
            case 1:
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                view.getContext().startActivity(intent);
                break;
            case 2:
            case 3:
            default:
                break;
        }
    }
}
