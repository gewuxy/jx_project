package lib.network.model.err;

import lib.network.Network;

/**
 * @auther yuansui
 * @since 2017/7/19
 */

public class TimeoutError extends NetError {

    public TimeoutError(int code) {
        super(code, Network.getConfig().getTimeoutToast());
    }
}
