package mapotempo.com.mapotempo_fleet_android.utils;

public class MissionsStatusGeneric<V, T> {

    private V view;
    private T type;

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
