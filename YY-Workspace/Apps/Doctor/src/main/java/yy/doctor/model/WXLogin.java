package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.WXLogin.TWXLogin;

/**
 * @auther : GuoXuan
 * @since : 2017/7/18
 */

public class WXLogin extends EVal<TWXLogin> {
    public enum  TWXLogin {
        access_token, // 接口调用凭证
        expires_in, // 凭证超时时间
        refresh_token, // 刷新access_token
        openid, // 授权用户唯一标识
        scope, // 用户授权的作用域
        unionid, // 当且仅当该移动应用已获得该用户的userinfo授权时，才会出现该字段

    }
}
