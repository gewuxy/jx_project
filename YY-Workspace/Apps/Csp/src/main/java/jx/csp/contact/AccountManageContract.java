package jx.csp.contact;

import android.view.View.OnClickListener;

import jx.csp.constant.BindId;
import jx.csp.model.Profile.TProfile;
import jx.csp.ui.activity.me.bind.BaseAccountActivity.RelatedId;
import lib.network.model.interfaces.IResult;
import lib.platform.Platform.Type;
import lib.yy.contract.IContract;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public interface AccountManageContract {

    interface V extends IContract.View {

        /**
         * 判断绑定状态
         *
         * @param tips 提示字段
         * @param key  区分的字段
         */
        void judgeBindStatus(@BindId int bindId, String tips, TProfile key);

        void showUnBindDialog(@BindId int bindId, String tips);

        /**
         * 判断第三方的绑定状态
         *
         * @param type
         * @param bindId
         * @param tips
         */
        void judgeBindStatus(@BindId int bindId, Type type, String tips);

        /**
         * 刷新item
         *
         * @param id
         */
        void refreshItem(@RelatedId int id);

        /**
         * 刷新item
         *
         * @param data 传递的数据
         * @param id   传递的Id
         */
        void refreshItem(@RelatedId int id, String data);

        /**
         * 确定是否解绑
         *
         * @param hint
         * @param l
         */
        void showConfirmUnBindDialog(CharSequence hint, OnClickListener l);

        /**
         * 检查是否安装微信
         */
        void showCheckWXAppDialog();

    }

    interface P extends IContract.Presenter<V> {

        /**
         * 进行第三方授权
         *
         * @param type
         */
        void auth(@BindId int bindId, Type type);

        /**
         * 绑定第三方账号
         *
         * @param uniqueId 需要绑定的第三方账号唯一标识,,解绑时无此参数
         * @param bindId   {@link BindId}
         * @param nickName 第三方账号的昵称,解绑时无此参数
         * @param gender   性别,解绑时无此参数
         * @param avatar   头像,解绑时无此参数
         */
        void bind(@BindId int bindId,
                  String uniqueId,
                  String nickName,
                  String gender,
                  String avatar);

        /**
         * 解绑第三方账号
         *
         * @param bindId {@link BindId}
         */
        void unBind(@BindId int bindId);

        /**
         * 保存第三方昵称
         *
         * @param nickName
         * @param bindId
         */
        void saveNickName(@BindId int bindId, String nickName);

        /**
         * 成功解绑账号
         *
         * @param r
         * @param id
         * @param key
         */
        void onUnBindSuccess(IResult r, int id, TProfile key);

        void onUnBindSuccess(IResult r, int id);
    }
}
