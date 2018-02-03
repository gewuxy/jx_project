package jx.csp.model.main;

import lib.ys.model.EVal;

/**
 * @auther : GuoXuan
 * @since : 2018/2/2
 */
public class Photo extends EVal<Photo.TPhoto> {

    public enum TPhoto {
        path, // 路径
        choice, // 是否被选择
        cover, // 是否被盖住
    }

}
