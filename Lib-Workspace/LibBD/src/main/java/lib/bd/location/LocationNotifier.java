package lib.bd.location;

import lib.ys.model.NotifierEx;

public final class LocationNotifier extends NotifierEx<OnLocationNotify> {

    private static LocationNotifier mInst;

    private LocationNotifier() {
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

    @Override
    protected void callback(OnLocationNotify o, int type, Object data) {
        LocateUnit unit = null;
        if (data instanceof LocateUnit) {
            unit = (LocateUnit) data;
            o.onLocationResult(unit.mSuccess, unit.mGps);
        }
    }

    public static class LocateUnit {
        public boolean mSuccess;
        public Gps mGps;
    }
}
