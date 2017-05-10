package yy.doctor.activity.me;

import org.json.JSONException;

import lib.ys.network.resp.IListResponse;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.UpdateLogAdapter;

/**
 * 更新日志
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class UpdateLogActivity extends BaseSRListActivity<String ,UpdateLogAdapter> {

    @Override
    public void initData() {

        for (int i = 0; i < 8; ++i) {
            addItem(" " + i);
        }

    }

    @Override
    public void initNavBar(NavBar bar) {

        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "更新日志", this);

    }

    @Override
    public void getDataFromNet() {

    }

    @Override
    public IListResponse<String> parseNetworkResponse(int id, String text) throws JSONException {
        return null;
    }

}
