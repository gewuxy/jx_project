package jx.csp.contact;

import android.content.Intent;
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

        /**
         * 设置输入规则
         */
        void setInput();

        /**
         * 设置按钮的改变
         */
        void setPayStatus();

        /**
         * 设置高亮
         * @param id
         */
        void setHighlight(@IdRes int id);

        /**
         * 设置剩余流量
         */
        void setSurplus();

        /**
         * 支付调用
         */
        void payPalPay(String orderId);
        void pingPay(String info);

        /**
         * 回调处理
         */
        void setCallBack(int requestCode, int resultCode, Intent data);
    }

    interface P extends PresenterEx{

        void getDataFormNet(int id, int flow, String channel);
    }
}
