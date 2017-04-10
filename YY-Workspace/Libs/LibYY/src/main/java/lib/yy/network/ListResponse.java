package lib.yy.network;

import lib.ys.network.resp.ListResponseSimpleEx;
import lib.yy.BaseConstants;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class ListResponse<T> extends ListResponseSimpleEx<T> {

    @Override
    public int getCodeOk() {
        return BaseConstants.KNetworkOk;
    }
}
