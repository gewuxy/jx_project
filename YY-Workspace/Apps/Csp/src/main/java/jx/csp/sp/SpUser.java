package jx.csp.sp;

import android.content.Context;

import java.util.Observable;

import lib.ys.util.sp.SpBase;
import jx.csp.App;

/**
 * @auther yuansui
 * @since 2017/5/4
 */

public class SpUser extends SpBase {

    private static final String KFileName = "sp_user";

    private static SpUser mInst = null;

    public interface SpUserKey {
        String KProfileUpdateTime = "update_time";
        String KIsShowSkipToNextPageDialog = "show_skip_to_next_page_dialog";
        String KIsShowRecordAgainDialog = "show_record_again_dialog";
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
     * 判断是否需要显示跳转到下/上一页继续录制的dialog
     *
     * @return
     */
    public boolean showSkipPageDialog() {
        return getBoolean(SpUserKey.KIsShowSkipToNextPageDialog, true);
    }

    /**
     * 保存状态
     */
    public void neverShowSkipPageDialog() {
        save(SpUserKey.KIsShowSkipToNextPageDialog, false);
    }

    /**
     * 判断是否需要显示覆盖录制的dialog
     *
     * @return
     */
    public boolean showRecordAgainDialog() {
        return getBoolean(SpUserKey.KIsShowSkipToNextPageDialog, true);
    }

    /**
     * 保存状态
     */
    public void neverShowRecordAgainDialog() {
        save(SpUserKey.KIsShowSkipToNextPageDialog, false);
    }

}
