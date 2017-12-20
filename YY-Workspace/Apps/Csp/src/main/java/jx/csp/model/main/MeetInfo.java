package jx.csp.model.main;

import lib.ys.model.EVal;

/**
 * @auther : GuoXuan
 * @since : 2017/12/19
 */

public class MeetInfo extends EVal<MeetInfo.TMeetInfo> {

    public enum TMeetInfo {

        @Bind(asList = Meet.class)
        list,

        hideCount,
    }

}
