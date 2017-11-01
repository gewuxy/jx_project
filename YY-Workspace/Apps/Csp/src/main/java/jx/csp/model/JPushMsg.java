package jx.csp.model;

import jx.csp.model.JPushMsg.TJPushMsg;
import lib.ys.model.EVal;

/**
 * @author Huoxuyu
 * @since 2017/10/20
 */

public class JPushMsg extends EVal<TJPushMsg> {

    public enum TJPushMsg {
        result,
        msgType,
    }

}
