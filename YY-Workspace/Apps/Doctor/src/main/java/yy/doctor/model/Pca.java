package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.Pca.TPca;

/**
 * @auther Huoxuyu
 * @since 2017/7/4
 */

public class Pca extends EVal<TPca>{

    public enum TPca {
        alpha,
        id,
        level,
        name,
        preId,
        regionType,
        spell,
    }
}
