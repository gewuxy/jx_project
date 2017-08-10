package lib.network.model;

/**
 * 网络重试的参数配置
 *
 * @author yuansui
 */
public class NetworkRetry {
    private int mCount;
    private long mDelay;

    public NetworkRetry() {
        mCount = 3;
        mDelay = 1000;
    }

    public NetworkRetry(int count, long delay) {
        mCount = count;
        mDelay = delay;
    }

    /**
     * 重试次数
     *
     * @param count
     * @return
     */
    public NetworkRetry count(int count) {
        mCount = count;
        return this;
    }

    /**
     * 重试间隔
     *
     * @param delay
     * @return
     */
    public NetworkRetry delay(int delay) {
        mDelay = delay;
        return this;
    }

    public boolean reduce() {
        mCount--;
        if (mCount < 0) {
            return false;
        }
        return true;
    }

    public long getDelay() {
        return mDelay;
    }
}
