package jx.csp.model.meeting;

import lib.ys.model.EVal;

/**
 * 星评
 *
 * @auther : GuoXuan
 * @since : 2018/1/18
 */
public class Code extends EVal<Code.TCode> {
    public enum TCode {
        starStatus, // true,
        startCodeUrl, // www.cspmeeting.com/qrcode/share/123.png,
        serverTime, // 1516259043131
    }
}
