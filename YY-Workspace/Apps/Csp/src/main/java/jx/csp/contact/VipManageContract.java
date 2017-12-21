package jx.csp.contact;

import java.util.List;

import jx.csp.model.VipPermission;
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
         * @param packageId
         * @param packageStart
         * @param packageEnd
         * @param meetCount
         */
        void setPackageData(int packageId, long packageStart, long packageEnd, int meetCount);
        
        void setPermission(List<VipPermission> list);
    }

    interface P extends Presenter<V> {

        /**
         * 查看会员套餐
         */
        void checkPackage();
    }
}
