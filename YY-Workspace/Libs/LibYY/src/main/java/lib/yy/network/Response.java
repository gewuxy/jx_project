package lib.yy.network;

import lib.ys.network.resp.ResponseEx;
import lib.yy.BaseConstants;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
abstract public class Response<T> extends ResponseEx<T> {

    @Override
    public int getCodeOk() {
        return BaseConstants.KNetworkOk;
    }
}
