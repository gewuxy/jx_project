package lib.yy.ui.activity.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.adapter.interfaces.IGroupAdapter;
import lib.ys.ui.activity.list.SRGroupListActivityEx;
import lib.ys.util.GenericUtil;
import lib.yy.R;
import lib.yy.network.BaseJsonParser;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.notify.Notifier.OnNotify;

/**
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
abstract public class BaseSRGroupListActivity<GROUP, CHILD, A extends IGroupAdapter<GROUP, CHILD>>
        extends SRGroupListActivityEx<GROUP, CHILD, A>
        implements OnNotify {

    private TextView mTvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Notifier.inst().add(this);

        // 不想影响子类的findView重写
        mTvEmpty = findView(R.id.empty_footer_tv);
        if (mTvEmpty != null) {
            mTvEmpty.setText(getEmptyText());
        }
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

    @CallSuper
    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return BaseJsonParser.evs(resp.getText(), GenericUtil.getClassType(getClass()));
    }

    @Override
    public View createEmptyFooterView() {
        return inflate(R.layout.layout_empty_footer);
    }

    protected String getEmptyText() {
        return "暂时没有相关内容";
    }

}
