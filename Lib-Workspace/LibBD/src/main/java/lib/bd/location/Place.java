package lib.bd.location;

import lib.bd.location.Place.TPlace;
import lib.ys.model.EVal;
import lib.ys.util.TextUtil;


/**
 * @author : GuoXuan
 * @since : 2017/5/23
 */

public class Place extends EVal<TPlace> {

    public static final int KMaxCount = 3;
    public static final String KSplit = " ";

    public static final int KProvince = 0;
    public static final int KCity = 1;
    public static final int KDistrict = 2;

    public enum TPlace {
        province,
        city,
        district,
    }

    public Place() {
    }

    public Place(String pcd) {
        if (TextUtil.isEmpty(pcd)) {
            return;
        }

        String[] addresses = pcd.split(KSplit);
        for (int i = 0; i < addresses.length; ++i) {
            switch (i) {
                case KProvince: {
                    put(TPlace.province, addresses[i]);
                }
                break;
                case KCity: {
                    put(TPlace.city, addresses[i]);
                }
                break;
                case KDistrict: {
                    put(TPlace.district, addresses[i]);
                }
                break;
            }
        }
    }

    public Place(String p, String c, String d) {
        put(TPlace.province, p);
        put(TPlace.city, c);
        put(TPlace.district, d);
    }

    @Override
    public String toString() {
        String p = getString(TPlace.province);
        String c = getString(TPlace.city);
        String d = getString(TPlace.district);

        return generatePcd(p, c, d);
    }

    /**
     * 根据省市区生成展示的字符串
     *
     * @param p
     * @param c
     * @param d
     * @return
     */
    private String generatePcd(String p, String c, String d) {
        StringBuffer b = new StringBuffer()
                .append(p)
                .append(KSplit)
                .append(c);
        if (TextUtil.isNotEmpty(d)) {
            b.append(KSplit).append(d);
        }
        return b.toString();
    }

//    private String generatePcd(String[] pcd) {
//        if (pcd == null || pcd.length < KMaxCount) {
//            return ConstantsEx.KEmptyValue;
//        }
//        return generatePcd(pcd[KProvince], pcd[KCity], pcd[KDistrict]);
//    }
}
