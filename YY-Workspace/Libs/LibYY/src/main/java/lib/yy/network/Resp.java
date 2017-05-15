package lib.yy.network;

import lib.ys.network.resp.RespEx;
import lib.yy.BaseConstants;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class Resp<T> extends RespEx<T> {

    @Override
    public int getCodeOk() {
        return BaseConstants.KNetworkOk;
    }
}
