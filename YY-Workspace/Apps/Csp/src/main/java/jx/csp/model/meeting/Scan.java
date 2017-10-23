package jx.csp.model.meeting;

import jx.csp.model.meeting.Scan.TScan;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2017/10/20
 */

public class Scan extends EVal<TScan> {

    public enum TScan {
        courseId,

        /**
         * {@link Course.PlayType}
         */
        playType
    }
}
