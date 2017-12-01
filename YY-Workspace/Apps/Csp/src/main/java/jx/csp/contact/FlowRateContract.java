package jx.csp.contact;

import android.content.Intent;
import android.support.annotation.IdRes;

import lib.jx.contract.IContract;

/**
 * 流量管理
 *
 * @auther yuansui
 * @since 2017/10/24
 */
public interface FlowRateContract {

    interface V extends IContract.View {
        /**
         * 设置支付金额
         */
        void setPayMoney();

        /**
         * 设置输入
         */
        void setInput();

        /**
         * 设置按钮的改变
         */
        void setPayStatus();

        /**
         * 设置高亮
         *
         * @param id
         */
        void setHighlight(@IdRes int id);

        /**
         * 设置支付调用第三方
         * ping++ or paypal
         */
        void setPayPalPay();

        void setPingPay(String charge);

        /**
         * 设置剩余流量
         */
        void setSurplusFlowRate();
    }

    interface P extends IContract.Presenter<V> {

        /**
         * 确认付款
         *
         * @param id
         * @param flow
         * @param channel
         */
        void confirmPay(int id, int flow, String channel);

        /**
         * 设置支付结果回调处理
         */
        void setCallBack(int id, int requestCode, int resultCode, Intent data);
    }
}
