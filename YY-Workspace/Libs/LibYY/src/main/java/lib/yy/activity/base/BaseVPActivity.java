package lib.yy.activity.base;

import android.os.Bundle;

import lib.ys.activity.ViewPagerActivityEx;
import lib.yy.Notifier;
import lib.yy.Notifier.NotifyType;
import lib.yy.Notifier.OnNotify;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
abstract public class BaseVPActivity extends ViewPagerActivityEx  implements OnNotify {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Notifier.inst().add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Notifier.inst().remove(this);
    }

    protected void notify(@NotifyType int type, Object data) {
        Notifier.inst().notify(type, data);
    }

    protected void notify(@NotifyType int type) {
        Notifier.inst().notify(type);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
    }

}
