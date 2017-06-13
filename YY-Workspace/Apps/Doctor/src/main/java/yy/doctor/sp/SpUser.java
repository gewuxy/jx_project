package yy.doctor.sp;

import android.content.Context;

import java.util.Observable;
import java.util.concurrent.TimeUnit;

import lib.ys.util.sp.SpBase;
import yy.doctor.App;

/**
 * @auther yuansui
 * @since 2017/5/4
 */

public class SpUser extends SpBase {

    private static final String KFileName = "sp_user";

    private static SpUser mInst = null;

    public interface SpUserKey {
        String KProfileUpdateTime = "update_time";
        String KJPushRegisterId = "jp_register_id";
        String KAppUpdateTime = "app_update_time";
    }

    private SpUser(Context context, String fileName) {
        super(context, fileName);
    }

    public synchronized static SpUser inst() {
        if (mInst == null) {
            mInst = new SpUser(App.getContext(), KFileName);
        }
        return mInst;
    }

    @Override
    public void update(Observable o, Object arg) {
        mInst = null;
    }

    /**
     * 是否需要刷新个人数据, 暂定间隔为2小时
     *
     * @return
     */
    public boolean needUpdateProfile() {
        long time = System.currentTimeMillis();
        long diff = time - getLong(SpUserKey.KProfileUpdateTime);
        if (diff >= TimeUnit.HOURS.toMillis(2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存个人数据刷新时间
     */
    public void updateProfileRefreshTime() {
        save(SpUserKey.KProfileUpdateTime, System.currentTimeMillis());
    }

    /**
     * 是否需要更新app, 暂定间隔为2天
     * @return
     */
    public boolean needUpdateApp() {
        long time = System.currentTimeMillis();
        long diff = time - getLong(SpUserKey.KAppUpdateTime);
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
        save(SpUserKey.KAppUpdateTime, System.currentTimeMillis());
    }

    /**
     * 保存极光推送RegisterId
     * @param id
     */
    public void jPushRegisterId(String id) {
        save(SpUserKey.KJPushRegisterId, id);
    }

}
