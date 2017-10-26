package jx.csp.contact;

import android.view.View.OnClickListener;

import jx.csp.model.Profile.TProfile;
import jx.csp.presenter.PresenterEx;
import jx.csp.ui.ViewEx;
import lib.yy.network.Result;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public interface AccountManageContract {
    
    interface V extends ViewEx{

        /**
         * 判断绑定状态
         * @param id
         * @param tips 提示字段
         * @param key  区分的字段
         */
        void judgeBindStatus(int id, String tips, TProfile key);

        /**
         * 绑定成功
         * @param data 传递的数据
         * @param id   传递的Id
         */
        void bindSuccess(String data, int id);

        /**
         * 解绑成功
         * @param r
         * @param id
         * @param key
         */
        void unBindSuccess(Result r, int id, TProfile key);

        /**
         * 确定是否解绑
         * @param hint
         * @param l
         */
        void confirmUnBindDialog(CharSequence hint, OnClickListener l);
        
    }
    
    interface P extends PresenterEx{

        /**
         * 解绑邮箱, 手机号
         * @param id
         * @param type
         */
        void unBindMobileAndEmail(int id, int type);

        /**
         * 绑定或解绑第三方账号
         * @param id
         * @param uniqueId  需要绑定的第三方账号唯一标识,,解绑时无此参数
         * @param thirdPartyId  1代表微信，2代表微博，3代表facebook,4代表twitter,5代表YaYa医师,解绑操作只需传递此字段
         * @param nickName  第三方账号的昵称,解绑时无此参数
         * @param gender    性别,解绑时无此参数
         * @param avatar    头像,解绑时无此参数
         * @param key       识别号
         * @param tips      解绑提示字段
         */
        void changeThirdPartyBindStatus(int id,
                                        int thirdPartyId,
                                        String uniqueId,
                                        String nickName,
                                        String gender,
                                        String avatar,
                                        TProfile key,
                                        String tips);
    }
}
