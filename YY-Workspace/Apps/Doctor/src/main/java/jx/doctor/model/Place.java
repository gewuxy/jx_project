package jx.doctor.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import lib.bd.location.Gps;
import lib.bd.location.Gps.TGps;
import lib.ys.model.EVal;
import lib.ys.util.TextUtil;
import jx.doctor.model.Place.TPlace;


/**
 * @author : GuoXuan
 * @since : 2017/5/23
 */

public class Place extends EVal<TPlace> {

    public static final String KSplit = " ";

    public static final int KProvince = 0;
    public static final int KCity = 1;
    public static final int KDistrict = 2;

    public enum TPlace {
        province,
        city,
        district,
    }

    public Place(List<String> mDescs) {
        // FIXME: 临时方案
        if (mDescs.size() >= 2) {
            put(TPlace.province, mDescs.get(0));
            put(TPlace.city, mDescs.get(1));
        }

        if (mDescs.size() > 2) {
            put(TPlace.district, mDescs.get(2));
        }
    }

    public Place(Gps gps) {
        put(TPlace.province, gps.getString(TGps.province));
        put(TPlace.city, gps.getString(TGps.city));
        put(TPlace.district, gps.getString(TGps.district));
    }

    public Place(String desc) {
        if (TextUtil.isEmpty(desc)) {
            return;
        }

        String[] addresses = desc.split(KSplit);
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

    public Place(@NonNull String p, @NonNull String c, @Nullable String d) {
        put(TPlace.province, p);
        put(TPlace.city, c);
        put(TPlace.district, d);
    }

    /**
     * 获取描述
     *
     * @return
     */
    public String getDesc() {
        String p = getString(TPlace.province);
        String c = getString(TPlace.city);
        String d = getString(TPlace.district);

        return generateDesc(p, c, d);
    }

    /**
     * 根据省市区生成展示的字符串
     *
     * @param p
     * @param c
     * @param d
     * @return
     */
    private String generateDesc(String p, String c, String d) {
        StringBuffer b = new StringBuffer()
                .append(p)
                .append(KSplit)
                .append(c);
        if (TextUtil.isNotEmpty(d)) {
            b.append(KSplit).append(d);
        }
        return b.toString();
    }
}
