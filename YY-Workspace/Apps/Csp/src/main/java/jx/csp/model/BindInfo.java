package jx.csp.model;

import jx.csp.constant.BindId;
import jx.csp.model.BindInfo.TBindInfo;
import lib.ys.model.EVal;

/**
 * @auther Huoxuyu
 * @since 2017/10/31
 */

public class BindInfo extends EVal<TBindInfo> {

    public enum TBindInfo {
        /**
         * {@link BindId}
         */
        thirdPartyId,

        nickName,
    }
}
