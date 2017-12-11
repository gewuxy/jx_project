package jx.csp.model;

import jx.csp.model.VipPermission.TVip;
import lib.ys.model.EVal;

/**
 * @auther Huoxuyu
 * @since 2017/12/8
 */

public class VipPermission extends EVal<TVip> {

    public enum TVip {
        id,
        text,
        image,
    }
}
