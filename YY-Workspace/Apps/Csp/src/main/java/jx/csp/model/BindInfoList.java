package jx.csp.model;

import jx.csp.Constants.LoginType;
import jx.csp.model.BindInfoList.TBindInfo;
import lib.ys.model.EVal;

/**
 * @auther Huoxuyu
 * @since 2017/10/31
 */

public class BindInfoList extends EVal<TBindInfo> {

    public enum TBindInfo {

        @LoginType
        thirdPartyId,

        nickName,
    }
}
