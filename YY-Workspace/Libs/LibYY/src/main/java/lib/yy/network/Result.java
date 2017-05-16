package lib.yy.network;

import lib.ys.network.result.ResultEx;
import lib.yy.BaseConstants;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class Result<T> extends ResultEx<T> {

    @Override
    public int getCodeOk() {
        return BaseConstants.KNetworkOk;
    }
}
