package jx.doctor.sp;

import android.content.Context;

import java.util.Observable;
import java.util.concurrent.TimeUnit;

import lib.ys.util.sp.SpBase;
import jx.doctor.App;

/**
 * @auther yuansui
 * @since 2017/5/4
 */

public class SpUser extends SpBase {

    private static final String KFileName = "sp_user";

    private static SpUser mInst = null;

    public interface SpUserKey {
        String KProfileUpdateTime = "update_time";
        String KBadgeNum = "badge_num";
        String KIsShowBind = "is_show_bind";
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
     * 保存小红点数
     *
     * @param num
     */
    public void updateBadgeNum(int num) {
        save(SpUserKey.KBadgeNum, num);
    }

    /**
     * 小红点数
     *
     * @return
     */
    public int badgeNum() {
        return getInt(SpUserKey.KBadgeNum, 0);
    }

    /**
     * 判断是否需要显示绑定的dialog
     *
     * @return
     */
    public boolean isShowBind() {
        return getBoolean(SpUserKey.KIsShowBind, true);
    }

    /**
     * 保存绑定状态
     */
    public void saveShowBind() {
        save(SpUserKey.KIsShowBind, false);
    }

}
