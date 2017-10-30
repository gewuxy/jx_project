package jx.csp.contact;

import android.content.Intent;
import android.support.annotation.IdRes;

import lib.yy.contract.BaseContract;

/**
 * 流量管理
 *
 * @auther yuansui
 * @since 2017/10/24
 */
public interface FlowRateContract {

    interface V extends BaseContract.BaseView {
        /**
         * 设置实际支付金额
         */
        void setActualPaymentMoney();

        /**
         * 设置输入规则
         */
        void setInputRule();

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
        void setSurplusFlowRate();

        /**
         * 设置支付调用第三方
         * ping++ or paypal
         */
        void setPayPalPay(String orderId);
        void setPingPay(String info);

        /**
         * 设置支付结果回调处理
         */
        void setResultDeal(int requestCode, int resultCode, Intent data);
    }

    interface P extends BaseContract.BasePresenter{

        /**
         * 请求服务器接口
         *
         * @param id
         * @param flow
         * @param channel
         */
        void getNetworkReq(int id, int flow, String channel);
    }
}
