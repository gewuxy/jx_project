package yaya.csp.model.login;

import lib.ys.model.EVal;
import yaya.csp.model.login.login.TLogin;

/**
 * @auther WangLan
 * @since 2017/9/27
 */

public class login extends EVal<TLogin> {
    public enum TLogin{
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
