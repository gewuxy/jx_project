package lib.yy.activity.base;

import android.os.Bundle;

import lib.ys.activity.form.FormActivityEx;
import lib.yy.Notifier;
import lib.yy.adapter.VH.FormItemVH;
import lib.yy.model.form.FormItem;
import lib.yy.Notifier.NotifyType;
import lib.yy.Notifier.OnNotify;

/**
 * @author CaiXiang
 * @since 2017/4/12
 */
abstract public class BaseFormActivity extends FormActivityEx<FormItem, FormItemVH> implements OnNotify {

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
