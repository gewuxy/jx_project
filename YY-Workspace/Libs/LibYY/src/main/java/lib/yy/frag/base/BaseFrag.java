package lib.yy.frag.base;


import lib.ys.ui.frag.FragEx;
import lib.yy.Notifier;
import lib.yy.Notifier.NotifyType;
import lib.yy.Notifier.OnNotify;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
abstract public class BaseFrag extends FragEx implements OnNotify {

    @Override
    protected void afterInitCompleted() {
        Notifier.inst().add(this);
    }

    @Override
    public void onDestroy() {
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
