package lib.ys.view.swipeRefresh.interfaces;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author yuansui
 */
public interface IExtend {

    @IntDef({
            IExtendStatus.normal,
            IExtendStatus.loading,
            IExtendStatus.failed,
            IExtendStatus.ready,
            IExtendStatus.finish,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface IExtendStatus {
        /******
         * 共用属性
         */
        int normal = 0; // 正常
        int loading = 1; // 加载中

        /******
         * footer
         */
        int failed = 2; // 失败

        /********
         * header
         */
        int ready = 3; // 松开后加载
        int finish = 4; // 加载完成
    }

    void changeStatus(@IExtendStatus int status);

    void onNormal();

    void onReady();

    void onLoading();

    void onFailed();

    void onFinish();
}
