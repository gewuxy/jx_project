package lib.bd.location;

import lib.bd.location.Gps.TGps;
import lib.ys.model.EVal;

public class Gps extends EVal<TGps> {

    public enum TGps {
        longitude,
        latitude,

        province,
        city,
        district,
    }
}