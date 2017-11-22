package jx.csp.contact;

import android.content.Context;
import android.view.View;

import jx.csp.constant.CaptchaType;
import jx.csp.ui.activity.me.bind.BaseSetActivity.RelatedId;
import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/11/22
 */

public interface BindPhoneContract {

    interface V extends IContract.View{


        /**
         * 获取验证码
         */
        void getCaptcha();

        /**
         * 关闭页面
         */
        void closePage();

    }
    interface P extends IContract.Presenter<V>{

        /**
         * 展示验证码dialog
         */
        void showCaptchaDialog(Context context, View v);

        /**
         * 获取验证码
         *
         * @param id
         * @param userName
         * @param type
         */
        void confirmBindAccount(@RelatedId int id, String userName, String captcha, @CaptchaType String type);

        /**
         * 绑定手机号成功
         */
        void bindPhoneSuccess();

        /**
         * 检查是否有手机号
         */
        void checkMobile(String phone);
    }
}
