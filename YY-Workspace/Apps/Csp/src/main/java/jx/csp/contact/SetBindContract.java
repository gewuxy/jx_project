package jx.csp.contact;

import android.widget.EditText;

import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/31
 */

public interface SetBindContract {

    interface V extends IContract.View {

        /**
         * 设置按钮状态
         *
         * @param enabled true可以点击, false不可点击
         */
        void setChanged(boolean enabled);

        /**
         * 初始化按钮的状态
         */
        void initButtonStatus();

        /**
         * 绑定邮箱成功后跳转
         */
        void setBindEmailSuccessJump();

        /**
         * 添加item里的view
         */
        void addItemCaptchaView();

        /**
         * 展示验证码dialog
         */
        void showCaptchaDialog();

        /**
         * 获取item文本
         *
         * @param relatedId
         */
        String getItemText(int relatedId);

        void getCaptcha();

        /**
         * 关闭页面
         */
        void onFinish();
    }

    interface P extends IContract.Presenter<V> {

        /**
         * 检查密码规则
         *
         * @param et
         */
        void checkPwd(EditText et);

        /**
         * 确认绑定
         *
         * @param id
         * @param userName
         * @param num
         */
        void confirmBindAccount(int id, String userName, String num);

        /**
         * 获取验证码
         *
         * @param id
         * @param userName
         * @param type
         */
        void getCaptcha(int id, String userName, String type);

        /**
         * 修改密码
         *
         * @param id
         * @param oldPwd
         * @param newPwd
         */
        void modifyPwd(int id, String oldPwd, String newPwd);

        /**
         * 绑定手机号成功
         */
        void saveBindPhone();

        /**
         * 校对是否有手机号
         */
        void equalsMobile();

    }
}
