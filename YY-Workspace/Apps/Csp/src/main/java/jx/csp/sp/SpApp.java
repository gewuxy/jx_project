package jx.csp.sp;

import android.content.Context;

import java.util.Observable;
import java.util.concurrent.TimeUnit;

import lib.ys.util.sp.SpBase;
import jx.csp.App;

/**
 * @author CaiXiang
 * @since 2017/6/28
 */

public class SpApp extends SpBase {

    private static final String KFileName = "sp_app";

    private static SpApp mInst = null;

    public synchronized static SpApp inst() {
        if (mInst == null) {
            mInst = new SpApp(App.getContext(), KFileName);
        }
        return mInst;
    }

    public SpApp(Context context, String fileName) {
        super(context, fileName);
    }

    public interface SpAppKey {
        String KUserName = "user_name";
        String KAppUpdateTime = "app_update_time";
        String KUserEmail = "email";
        String KUserMobile = "mobile";
        String KSystemLanguage = "language";
        String KCountry = "country";
    }

    @Override
    public void update(Observable observable, Object data) {
        mInst = null;
    }

    /**
     * 保存用户名
     *
     * @param userName
     */
    public void saveUserName(String userName) {
        save(SpAppKey.KUserName, userName);
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String getUserName() {
        return getString(SpAppKey.KUserName);
    }

    /**
     * 保存用户的邮箱
     *
     * @param userEmail
     */
    public void saveUserEmail(String userEmail) {
        save(SpAppKey.KUserEmail, userEmail);
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String getUserEmail() {
        return getString(SpAppKey.KUserEmail);
    }

    /**
     * 保存用户名
     *
     * @param userMobile
     */
    public void saveUserMobile(String userMobile) {
        save(SpAppKey.KUserMobile, userMobile);
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String getUserMobile() {
        return getString(SpAppKey.KUserMobile);
    }

    /**
     * 是否需要检查app有没有更新, 暂定间隔为2天
     *
     * @return
     */
    public boolean needUpdateApp() {
        long time = System.currentTimeMillis();
        long diff = time - getLong(SpAppKey.KAppUpdateTime);
        if (diff >= TimeUnit.DAYS.toMillis(2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存更新app刷新时间
     */
    public void updateAppRefreshTime() {
        save(SpAppKey.KAppUpdateTime, System.currentTimeMillis());
    }

    /**
     * 保存系统语言
     *
     * @param s
     */
    public void saveSystemLanguage(String s) {
        save(SpAppKey.KSystemLanguage, s);
    }

    /**
     * 获取系统语言
     *
     * @return
     */
    public String getSystemLanguage() {
        return getString(SpAppKey.KSystemLanguage, "zh");
    }

    /**
     * 保存国家
     *
     * @param s
     */
    public void saveCountry(String s) {
        save(SpAppKey.KCountry, s);
    }

    /**
     * 获取国家
     *
     * @return
     */
    public String getCountry() {
        return getString(SpAppKey.KCountry, "CN");
    }
}
