package jx.csp.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @auther HuoXuYu
 * @since 2018/1/18
 */
@StringDef({
        WatchPwdType.setPwd,
        WatchPwdType.delete,
        WatchPwdType.getPwd,
})
@Retention(RetentionPolicy.SOURCE)
public @interface WatchPwdType {
    String setPwd = "1";    //设置密码
    String delete = "2";    //删除密码
    String getPwd = "3";    //即时获取密码
}
