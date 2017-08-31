package mapotempo.com.mapotempo_fleet_android.utils;

public class ListItemCustom {
    public int icon;
    public String title;
    public int color;

    public ListItemCustom() { super(); }

    /**
     * Hold specs for each element in the DrawerList
     * @param icon the ID of an icon (Used by the R class)
     * @param title Title of the list element
     * @param color Color of the icon
     */
    public ListItemCustom(int icon, String title, int color) {
        super();
        this.icon = icon;
        this.title = title;
        this.color = color;
    }
}
