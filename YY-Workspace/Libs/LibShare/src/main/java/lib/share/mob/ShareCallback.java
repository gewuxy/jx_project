package lib.share.mob;

/**
 * @auther WangLan
 * @since 2017/11/1
 */

abstract public class ShareCallback {

    abstract public void shareComplete();
    abstract public void shareError();
    abstract public void shareCancel();
}
