package jx.csp.sp;

import android.content.Context;

import java.util.Observable;
import java.util.concurrent.TimeUnit;

import jx.csp.App;
import jx.csp.constant.AppType;
import jx.csp.constant.LangType;
import jx.csp.model.login.Advert;
import lib.ys.util.sp.SpBase;

/**
 * @author CaiXiang
 * @since 2017/6/28
 */

public class SpApp extends SpBase {

    private static final String KFileName = "sp_app";
    public static final int KDefaultVersion = 0;

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

    public void saveAdvert(Advert ad) {
        save(SpAppKey.KAdvert, ad);
    }

    public Advert getAdvert() {
        return getEV(SpAppKey.KAdvert, Advert.class);
    }

    public interface SpAppKey {
        String KUserName = "user_name";
        String KAppUpdateTime = "app_update_time";
        String KUserEmail = "email";
        String KSystemLang = "sys_lang";
        String KAppType = "app_type";
        String KLoginVideoVersion = "version";
        String KAdvert = "advert";
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
     * 保存登录视频的版本
     *
     * @param version
     */
    public void saveLoginVideoVersion(int version) {
        save(SpAppKey.KLoginVideoVersion, version);
    }

    /**
     * 获取版本
     *
     * @return
     */
    public int getLoginVideoVersion() {
        return getInt(SpAppKey.KLoginVideoVersion, KDefaultVersion);
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
     * @param type
     */
    public void saveSystemLang(LangType type) {
        save(SpAppKey.KSystemLang, type.ordinal());
    }

    public LangType getLangType() {
        return LangType.values()[getInt(SpAppKey.KSystemLang, LangType.en.ordinal())];
    }

    // 简体中文和繁体中文字符串资源要分别放到res/values-zh-rCN和res/values-zh-rTW下
    public void setAppType(@AppType int type) {
        save(SpAppKey.KAppType, type);
    }

    @AppType
    public int getAppType() {
        return getInt(SpAppKey.KAppType, AppType.inland);
    }
}
