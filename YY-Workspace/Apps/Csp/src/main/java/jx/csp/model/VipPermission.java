package jx.csp.model;

import jx.csp.model.VipPermission.TVip;
import lib.ys.model.EVal;

/**
 * 会员管理权限
 *
 * @auther HuoXuYu
 * @since 2017/12/8
 */

public class VipPermission extends EVal<TVip> {

    public VipPermission(String text, int color, int image) {
        put(TVip.text, text);
        put(TVip.color, color);
        put(TVip.image, image);
    }

    public enum TVip {
        text,
        color,
        image,
    }
}
