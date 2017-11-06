package lib.platform.listener;

import lib.platform.model.AuthParams;

/**
 * @auther yuansui
 * @since 2017/11/6
 */
public interface OnAuthListener {
    void onAuthSuccess(AuthParams params);

    void onAuthError(String message);

    void onAuthCancel();
}
