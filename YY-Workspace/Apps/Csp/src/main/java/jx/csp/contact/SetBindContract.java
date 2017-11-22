package jx.csp.contact;

import android.widget.EditText;

import jx.csp.ui.activity.me.bind.BaseSetActivity.RelatedId;
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
        void confirmBindAccount(@RelatedId int id, String userName, String num);

        /**
         * 修改密码
         *
         * @param id
         * @param oldPwd
         * @param newPwd
         */
        void modifyPwd(@RelatedId int id, String oldPwd, String newPwd);
    }
}
