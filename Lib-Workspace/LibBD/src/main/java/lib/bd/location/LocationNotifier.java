package lib.bd.location;

import java.util.ArrayList;
import java.util.List;

public final class LocationNotifier {

    private static LocationNotifier mInst;

    private static List<OnLocationNotify> mObservers;

    private LocationNotifier() {
        mObservers = new ArrayList<>();
    }

    public static LocationNotifier inst() {
        if (mInst == null) {
            synchronized (LocationNotifier.class) {
                if (mInst == null) {
                    mInst = new LocationNotifier();
                }
            }
        }
        return mInst;
    }

    public synchronized void add(OnLocationNotify observer) {
        mObservers.add(observer);
    }

    public synchronized void remove(OnLocationNotify observer) {
        mObservers.remove(observer);
    }

    /**
     * 发布
     */
    public synchronized void notify(boolean success, Gps gps) {
        for (OnLocationNotify o : mObservers) {
            if (o != null) {
                o.onLocationResult(success, gps);
            }
        }
    }
}
