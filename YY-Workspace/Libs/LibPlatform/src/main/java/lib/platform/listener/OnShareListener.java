package lib.platform.listener;

/**
 * @auther WangLan
 * @since 2017/11/1
 */

public interface OnShareListener {
    void onShareSuccess();

    void onShareError(String message);

    void onShareCancel();
}
