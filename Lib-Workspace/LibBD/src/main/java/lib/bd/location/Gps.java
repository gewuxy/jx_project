package lib.bd.location;

import lib.bd.location.Gps.TGps;
import lib.ys.model.EVal;
import lib.ys.model.inject.BindObj;

public class Gps extends EVal<TGps> {

    public enum TGps {
        longitude,
        latitude,

        @BindObj(Place.class)
        place, // 地点(省市区)
    }
}