package lib.jx.ui.activity.base;

import android.os.Bundle;

import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.ui.activity.list.ListActivityEx;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.notify.Notifier.OnNotify;

/**
 * @auther yuansui
 * @since 2017/4/19
 */

abstract public class BaseListActivity<T, A extends IAdapter<T>> extends ListActivityEx<T, A> implements OnNotify {

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
