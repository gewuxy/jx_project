package lib.um.update;

import android.content.Context;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * @author yuansui
 */
public class U_Update {
    /**
     * 检查更新
     *
     * @param context
     * @param silent  是否使用静默模式, false为提示, true为只有新版本的时候才提示
     */
    public static void checkUpdate(final Context context, final boolean silent) {
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: {
                        // has update
                        UmengUpdateAgent.showUpdateDialog(context, updateInfo);
                    }
                    break;
                    case UpdateStatus.No: {
                        // has no update
                        if (!silent) {
                            Toast.makeText(context, "没有更新", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case UpdateStatus.NoneWifi: {
                        // none wifi
                        if (!silent) {
                            Toast.makeText(context, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case UpdateStatus.Timeout: {
                        // time out
                        if (!silent) {
                            Toast.makeText(context, "超时", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
            }
        });
        UmengUpdateAgent.update(context);
    }
}
