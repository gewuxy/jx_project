package lib.bd.location;

import lib.ys.model.EVal;
import lib.bd.location.Place.TPlace;

/**
 * @author : GuoXuan
 * @since : 2017/5/23
 */

public class Place extends EVal<TPlace> {
    public enum TPlace {
        province,
        city,
        district,
    }
}
