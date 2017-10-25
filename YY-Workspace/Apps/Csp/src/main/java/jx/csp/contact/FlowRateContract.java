package jx.csp.contact;

import android.support.annotation.IdRes;

import jx.csp.presenter.PresenterEx;
import jx.csp.ui.ViewEx;

/**
 * 流量管理
 *
 * @auther yuansui
 * @since 2017/10/24
 */
public interface FlowRateContract {

    interface V extends ViewEx {
        /**
         * 设置实际支付金额
         */
        void setMoney();

        void setInput();

        void setPayStatus();

        void setHighlight(@IdRes int id);

        void setSurplus();
    }

    interface P extends PresenterEx<V> {
        void recharge();
    }
}
