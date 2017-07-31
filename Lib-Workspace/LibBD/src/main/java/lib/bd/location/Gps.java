package lib.bd.location;

import lib.bd.location.Gps.TGps;
import lib.ys.model.EVal;

public class Gps extends EVal<TGps> {

    public enum TGps {
        longitude,
        latitude,

        @Bind(Place.class)
        place, // 地点(省市区)
    }
}