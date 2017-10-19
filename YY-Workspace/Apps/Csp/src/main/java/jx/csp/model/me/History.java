package jx.csp.model.me;

import jx.csp.model.me.History.THistory;
import lib.ys.model.EVal;

/**
 * @auther WangLan
 * @since 2017/10/13
 */

public class History extends EVal<THistory> {

    public enum THistory {
        headimg, // 单位号头像
        acceptName, // 单位号名称
        sign, // 会议简介
        acceptId, // 接收者id
    }
}
