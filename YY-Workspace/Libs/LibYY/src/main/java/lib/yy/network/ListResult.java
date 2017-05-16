package lib.yy.network;

import lib.ys.network.result.ListResultEx;
import lib.yy.BaseConstants;

/**
 * @author CaiXiang
 * @since 2017/4/10
 */
public class ListResult<T> extends ListResultEx<T> {

    @Override
    public int getCodeOk() {
        return BaseConstants.KNetworkOk;
    }
}
