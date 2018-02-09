package jx.csp.contact;

import lib.jx.contract.IContract;
import lib.network.model.interfaces.IResult;

/**
 * @auther : GuoXuan
 * @since : 2018/1/19
 */
public interface StarContract {

    int KReqStar = 0;

    interface V extends IContract.View {

        void onNetworkSuccess(int id, IResult r);
    }

    interface P<T extends V> extends IContract.Presenter<T> {

        void getDataFromNet(int netType);
    }
}
