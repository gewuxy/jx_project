package lib.yy.ui.activity.base;

import android.os.Bundle;

import lib.ys.ui.activity.form.FormActivityEx;
import lib.yy.adapter.VH.FormVH;
import lib.yy.model.form.BaseForm;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.notify.Notifier.OnNotify;

/**
 * @author CaiXiang
 * @since 2017/4/12
 */
abstract public class BaseFormActivity extends FormActivityEx<BaseForm, FormVH> implements OnNotify {

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
