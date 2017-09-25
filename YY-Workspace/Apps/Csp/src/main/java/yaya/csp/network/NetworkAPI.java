package yaya.csp.network;

import inject.annotation.network.API;
import inject.annotation.network.APIFactory;
import inject.annotation.network.method.POST;
import inject.annotation.network.method.UPLOAD;

/**
 * @auther yuansui
 * @since 2017/8/16
 */
@APIFactory(
        host = "https://app.medyaya.cn/api/",
        hostDebuggable = "http://59.111.90.245:8084/v7/api/"
)
public class NetworkAPI {

    @API
    interface User{

        /**
         * 头像上传
         *
         * @param file
         */
        @UPLOAD("user/updateAvatar")
        void upload(byte[] file);

        /**
         * 修改密码
         *
         * @param oldPwd 旧密码
         * @param newPwd 新密码
         */
        @POST("user/resetPwd")
        void changePwd(String oldPwd, String newPwd);

        /**
         * 绑定邮箱
         *
         * @param email 用户名
         */
        @POST("user/toBind")
        void bindEmail(String email);

    }

}
