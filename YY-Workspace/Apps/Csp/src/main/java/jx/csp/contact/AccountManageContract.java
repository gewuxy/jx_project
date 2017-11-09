package jx.csp.contact;

import android.view.View.OnClickListener;

import jx.csp.constant.LoginType;
import jx.csp.model.Profile.TProfile;
import lib.platform.Platform.Type;
import lib.yy.contract.IContract;
import lib.yy.network.Result;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public interface AccountManageContract {

    interface V extends IContract.View {

        /**
         * 判断绑定状态
         *
         * @param id
         * @param tips 提示字段
         * @param key  区分的字段
         */
        void judgeBindStatus(int id, String tips, TProfile key);
        void unBindDialog(int id, int thirdPartyId, String tips);

        /**
         * 绑定成功
         *
         * @param data 传递的数据
         * @param id   传递的Id
         */
        void bindRefreshItem(String data, int id);

        /**
         * 解绑刷新item
         *
         * @param id
         */
        void unBindRefreshItem(int id);

        /**
         * 确定是否解绑
         *
         * @param hint
         * @param l
         */
        void confirmUnBindDialog(CharSequence hint, OnClickListener l);

    }

    interface P extends IContract.Presenter<V> {

        /**
         * 进行第三方授权
         *
         * @param type
         */
        void doAuth(Type type, int id);

        /**
         * 绑定第三方账号
         *
         * @param id
         * @param uniqueId     需要绑定的第三方账号唯一标识,,解绑时无此参数
         * @param thirdPartyId 1代表微信，2代表微博，3代表facebook,4代表twitter,5代表YaYa医师,解绑操作只需传递此字段
         * @param nickName     第三方账号的昵称,解绑时无此参数
         * @param gender       性别,解绑时无此参数
         * @param avatar       头像,解绑时无此参数
         */
        void bindThirdParty(int id,
                            int thirdPartyId,
                            String uniqueId,
                            String nickName,
                            String gender,
                            String avatar);

        /**
         * 保存第三方昵称
         *
         * @param r
         * @param id
         * @param nickName
         * @param bindType
         */
        void setSaveThirdPartyNickName(Result r, int id, String nickName, @LoginType int bindType);

        /**
         * 解绑邮箱, 手机号
         *
         * @param id
         * @param type
         */
        void unBindMobileOrEmailReq(int id, int type);

        /**
         * 解绑第三方账号
         *
         * @param id
         * @param thirdPartyId 1代表微信，2代表微博，3代表facebook,4代表twitter,5代表YaYa医师,解绑操作只需传递此字段
         */
        void unBindThirdPartyReq(int id, int thirdPartyId);

        /**
         * 成功解绑账号
         *
         * @param r
         * @param id
         * @param key
         */
        void unBindEmailOrMobileSuccess(Result r, int id, TProfile key);

        void unBindThirdPartySuccess(Result r, int id);
    }
}
