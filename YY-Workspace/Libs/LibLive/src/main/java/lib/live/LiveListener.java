package lib.live;

import android.os.Bundle;

import lib.live.pull.PullListener;
import lib.live.push.PushListener;

/**
 * @author CaiXiang
 * @since 2017/12/15
 */

public class LiveListener implements PushListener, PullListener {

    @Override
    public void onPushFail() {
    }

    @Override
    public void onNetStatus(Bundle var1) {
    }

    @Override
    public void load() {
    }

    @Override
    public void begin() {
    }

    @Override
    public void end() {
    }
}
