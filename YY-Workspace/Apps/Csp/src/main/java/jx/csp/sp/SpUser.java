package jx.csp.sp;

import android.content.Context;

import java.util.Observable;
import java.util.concurrent.TimeUnit;

import jx.csp.App;
import lib.ys.util.sp.SpBase;

import static jx.csp.sp.SpUser.SpUserKey.KMainVpPage;

/**
 * @auther yuansui
 * @since 2017/5/4
 */

public class SpUser extends SpBase {

    private static final String KFileName = "sp_user";

    private static SpUser mInst = null;

    public interface SpUserKey {
        String KProfileUpdateTime = "update_time";
        String KIsShowRecordAgainDialog = "show_record_again_dialog";
        String KMainVpPage = "main_ac_vp_page";
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
     * 保存个人数据刷新时间
     */
    public void updateProfileRefreshTime() {
        save(SpUserKey.KProfileUpdateTime, System.currentTimeMillis());
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
     * 判断是否需要显示覆盖录制的dialog
     *
     * @return
     */
    public boolean showRerecordingDialog() {
        return getBoolean(SpUserKey.KIsShowRecordAgainDialog, true);
    }

    /**
     * 保存状态
     */
    public void neverShowRerecordingDialog() {
        save(SpUserKey.KIsShowRecordAgainDialog, false);
    }

    /**
     * 获得离开首页vp的页面
     */
    public int getMainAcVpPage() {
        return getInt(KMainVpPage, 0);
    }

    /**
     * 保存离开首页时，vp的页面
     *
     * @param page
     */
    public void saveMainPage(int page) {
        save(SpUserKey.KMainVpPage, page);
    }

}
