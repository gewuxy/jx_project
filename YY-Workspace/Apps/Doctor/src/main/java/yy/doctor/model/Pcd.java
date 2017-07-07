package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.Pcd.TPcd;

/**
 * pcd = province city district
 *
 * @auther Huoxuyu
 * @since 2017/7/4
 */
public class Pcd extends EVal<TPcd> {

    public static final int KProvince = 0;
    public static final int KCity = 1;
    public static final int KDistrict = 2;

    public static final String KSplit = " ";
    public static final int KMaxCount = 3;

    public static final int KLevelEnd = 3;

    public enum TPcd {
        alpha,
        id,
        level,
        name,
        preId,
        regionType,
        spell,
    }
}
