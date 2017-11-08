package jx.csp.model.login;

import jx.csp.model.login.Login.TLogin;
import lib.ys.model.EVal;

/**
 * @auther WangLan
 * @since 2017/9/27
 */

public class Login extends EVal<TLogin> {
    public enum TLogin {
        id,
        userName, // 真实姓名
        nickName, // 昵称
        password,
        email,
        mobile,
        country,
        province,
        city,
        district,
        avatar, // 头像
        info, // 个人简介
        token, // token值
    }
}
