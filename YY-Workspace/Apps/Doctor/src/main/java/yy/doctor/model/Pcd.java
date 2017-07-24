package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.Pcd.TPcd;

/**
 * // FIXME: 到底是什么....里面没有pcd任何相关信息
 * pcd = province city district
 *
 * @auther Huoxuyu
 * @since 2017/7/4
 */
public class Pcd extends EVal<TPcd> {

    //如果level等于3就没有下一级了
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
