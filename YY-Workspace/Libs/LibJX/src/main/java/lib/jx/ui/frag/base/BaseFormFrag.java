package lib.jx.ui.frag.base;

import lib.ys.ui.frag.form.FormFragEx;
import lib.jx.adapter.VH.FormVH;
import lib.jx.model.form.BaseForm;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.notify.Notifier.OnNotify;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
abstract public class BaseFormFrag extends FormFragEx<BaseForm, FormVH> implements OnNotify {

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
