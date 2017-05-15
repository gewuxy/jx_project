package lib.yy.network;

import lib.ys.network.resp.ListRespEx;
import lib.yy.BaseConstants;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class ListResp<T> extends ListRespEx<T> {

    @Override
    public int getCodeOk() {
        return BaseConstants.KNetworkOk;
    }
}
