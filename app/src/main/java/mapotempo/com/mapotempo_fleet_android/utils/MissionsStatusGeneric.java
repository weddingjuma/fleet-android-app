package mapotempo.com.mapotempo_fleet_android.utils;

public class MissionsStatusGeneric<V, T> {

    private V view;
    private T type;

    /**
     * A Generic class that hold a view and a type, mostly used for the missions status
     * @param view Any object descendant of View.
     * @param type Type of a status, can be anything from the database.
     * @throws NullPointerException
     */
    public MissionsStatusGeneric(V view, T type) throws NullPointerException {
        this.view = view;
        this.type = type;

        if (view == null)
            throw new NullPointerException("View can't be null");

        if (type == null)
            throw new NullPointerException("type can't be null");
    }

    public V getView() {
        return view;
    }

    public T getType() {
        return type;
    }
}
