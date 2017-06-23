package lib.ys.view.swipeRefresh.interfaces;

import android.support.annotation.IntDef;

/**
 * @author yuansui
 */
public interface Extend {

    @IntDef({
            ExtendState.normal,
            ExtendState.loading,
            ExtendState.failed,
            ExtendState.ready,
            ExtendState.finish,
    })
    @interface ExtendState {
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

    void changeState(@ExtendState int state);

    void onNormal();

    void onReady();

    void onLoading();

    void onFailed();

    void onFinish();
}
