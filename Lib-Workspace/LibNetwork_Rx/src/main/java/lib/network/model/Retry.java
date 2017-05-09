package lib.network.model;

/**
 * 网络重试的参数配置
 *
 * @author yuansui
 */
public class Retry {
    private int mCount;
    private long mDelay;

    public Retry() {
        mCount = 3;
        mDelay = 1000;
    }

    public Retry(int count, long delay) {
        mCount = count;
        mDelay = delay;
    }

    /**
     * 重试次数
     *
     * @param count
     * @return
     */
    public Retry count(int count) {
        mCount = count;
        return this;
    }

    /**
     * 重试间隔
     *
     * @param delay
     * @return
     */
    public Retry delay(int delay) {
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
