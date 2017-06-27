package lib.yy.ui.activity.base;

import android.os.Bundle;

import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.ui.activity.list.GroupListActivityEx;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.notify.Notifier.OnNotify;

/**
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */

abstract public class BaseGroupListActivity<GROUP, CHILD, A extends IGroupAdapter<GROUP, CHILD>>
        extends GroupListActivityEx<GROUP, CHILD, A>
        implements OnNotify {

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
