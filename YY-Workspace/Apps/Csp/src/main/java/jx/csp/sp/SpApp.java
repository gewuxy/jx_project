package jx.csp.sp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import java.util.Observable;
import java.util.concurrent.TimeUnit;

import jx.csp.App;
import jx.csp.constant.AppType;
import jx.csp.constant.LangType;
import jx.csp.model.login.Advert;
import lib.ys.YSLog;
import lib.ys.util.sp.SpBase;

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

    public void saveAdvert(Advert ad) {
        save(SpAppKey.KAdvert, ad);
    }

    public Advert getAdvert() {
        return getEV(SpAppKey.KAdvert, Advert.class);
    }

    public interface SpAppKey {
        String KUserName = "user_name";
        String KUserEmail = "email";
        String KSystemLang = "sys_lang";
        String KAppType = "app_type";
        String KAdvert = "advert";
        String KGuide = "guide";
        String KAppUpdateTime = "app_update_time";
        String KShowSlideDialog = "show_slide_dialog";
        String KGuidelines = "guidelines";
        String KAppVersion = "app_version";
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

    /**
     * 保存已显示过引导页状态
     */
    public void saveGuideState() {
        save(SpAppKey.KGuide, false);
    }

    /**
     * 是否需要显示引导页
     *
     * @return true 表示要显示   false 表示已经显示过了，不需要再显示
     */
    public boolean getGuideState() {
        return getBoolean(SpAppKey.KGuide, true);
    }

    /**
     * 保存已显示新手指引状态
     */
    public void saveGuidelines() {
        save(SpAppKey.KGuidelines, false);
    }

    /**
     * 首次进入首页新手指引显示
     *
     * @return
     */
    public boolean getGuidelines() {
        return getBoolean(SpAppKey.KGuidelines, true);
    }

    /**
     * 判断是否需要显示滑动将无法续录的dialog
     *
     * @return
     */
    public boolean showSlideDialog() {
        return getBoolean(SpAppKey.KShowSlideDialog, true);
    }

    /**
     * 保存状态
     */
    public void neverShowSlideDialog() {
        save(SpAppKey.KShowSlideDialog, false);
    }

    /**
     * 是否需要检查app有没有新版本, 暂定间隔为1天
     *
     * @return
     */
    public boolean needCheckAppVersion() {
        long time = System.currentTimeMillis();
        long diff = time - getLong(SpAppKey.KAppUpdateTime);
        if (diff >= TimeUnit.DAYS.toMillis(1)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存检查app版本信息时间
     */
    public void saveCheckAppVersionTime() {
        save(SpAppKey.KAppUpdateTime, System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2));
    }

    /**
     * 获取当前app版本号
     * @param context
     * @return
     */
    public int getCurrentVersion(Context context) {
        int version = 0;
        try {
            PackageInfo packageInfo = context
                    .getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionCode;
            YSLog.d(TAG, "版本号 = " + version);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public void setAppOldVersion(int version) {
        save(SpAppKey.KAppVersion, version);
    }

    public int getAppOldVersion(){
        return getInt(SpAppKey.KAppVersion);
    }
}
