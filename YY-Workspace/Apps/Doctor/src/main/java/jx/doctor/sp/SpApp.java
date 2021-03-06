package jx.doctor.sp;

import android.content.Context;

import java.util.Observable;
import java.util.concurrent.TimeUnit;

import lib.ys.util.sp.SpBase;
import jx.doctor.App;
import jx.doctor.model.Ad;

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
        String KFirstEnterExam = "first_enter_exam";
        String KFirstEnterQue = "first_enter_que";
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
     * 判断是否是第一次进入考试界面
     */
    public Boolean isFirstExam() {
        return getBoolean(SpAppKey.KFirstEnterExam, true);
    }

    public void noFirstExam() {
        save(SpAppKey.KFirstEnterExam, false);
    }

    /**
     * 判断是否是第一次进入问卷界面
     */
    public Boolean isFirstQue() {
        return getBoolean(SpAppKey.KFirstEnterQue, true);
    }

    public void noFirstQue() {
        save(SpAppKey.KFirstEnterQue, false);
    }

    public void saveAdvert(Ad ad) {
        save(SpAppKey.KAdvert, ad);
    }

    public Ad getAdvert() {
        return getEV(SpAppKey.KAdvert, Ad.class);
    }

}
