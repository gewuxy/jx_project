package jx.csp.contact;

import lib.jx.contract.IContract.Presenter;
import lib.jx.contract.IContract.View;

/**
 * @auther Huoxuyu
 * @since 2017/12/12
 */

public interface VipManageContract {

    interface V extends View {

        /**
         * 设置会员数据
         *
         * @param id
         * @param packageStart
         * @param packageEnd
         * @param meetCount
         */
        void setAdapterData(int id, long packageStart, long packageEnd, int meetCount);
    }

    interface P extends Presenter<V> {

        /**
         * 查看会员套餐
         */
        void checkPackage();
    }
}
