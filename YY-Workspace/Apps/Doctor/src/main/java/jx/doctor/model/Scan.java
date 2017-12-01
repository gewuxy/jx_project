package jx.doctor.model;

import lib.ys.model.EVal;
import jx.doctor.model.Scan.TScan;

/**
 * @auther WangLan
 * @since 2017/7/27
 */

public class Scan extends EVal<TScan> {

    public enum TScan {
        @Bind(asList = Integer.class)
        masterId,

        @Bind(asList = String.class)
        name,
    }
}
