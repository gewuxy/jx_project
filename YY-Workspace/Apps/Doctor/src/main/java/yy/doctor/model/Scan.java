package yy.doctor.model;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindList;
import yy.doctor.model.Scan.TScan;

/**
 * @auther WangLan
 * @since 2017/7/27
 */

public class Scan extends EVal<TScan> {
    public enum TScan{
        @BindList(Integer.class)
        masterId,

        @BindList(String.class)
        name,
    }
}
