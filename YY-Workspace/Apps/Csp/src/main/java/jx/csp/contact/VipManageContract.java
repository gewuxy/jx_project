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
         * @param id 套餐id
         * @param unlimited 是否无期限 0表示否 1表示是
         * @param startTime  套餐开始时间
         * @param endTime    套餐结束时间
         * @param meetTotalCount 会议总数
         */
        void setPackageData(int id, int unlimited, long startTime, long endTime, int meetTotalCount);
        
        void setPermission(List<VipPermission> list);
    }

    interface P extends Presenter<V> {

        /**
         * 查看会员套餐
         */
        void checkPackage();
    }
}
