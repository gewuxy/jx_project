package lib.ys.view.swipeRefresh.interfaces;

/**
 * @author yuansui
 */
public interface IExtend {

    enum TState {
        /**
         * 共用属性
         */
        normal, // 正常
        loading, // 加载中

        /**
         * footer
         */
        failed, // 失败

        /**
         * header
         */
        ready, // 松开后加载
        finish, // 加载完成
    }

    void changeState(TState state);

    void onNormal();

    void onReady();

    void onLoading();

    void onFailed();

    void onFinish();
}
