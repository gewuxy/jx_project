package jx.csp.ui.activity.me;

import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import jx.csp.R;
import jx.csp.adapter.PlatformAdapter;
import jx.csp.adapter.PlatformAdapter.OnPlatformCheckedListener;
import jx.csp.dialog.PlatformDialog;
import jx.csp.model.contribute.Platform;
import jx.csp.model.contribute.Platform.TPlatformDetail;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkAPISetter.UserAPI;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseSRListActivity;

/**
 * 投稿平台
 *
 * @auther Huoxuyu
 * @since 2017/9/28
 */

public class ContributePlatformActivity extends
        BaseSRListActivity<Platform, PlatformAdapter>
        implements OnPlatformCheckedListener {

    private final int KCodePlatform = 1;

    private TextView mTv;
    private ArrayList<Platform> mSelectedItem;
    private Platform mPlatform;

    @Override
    public void initData() {
        mSelectedItem = new ArrayList<>();
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.contribute_platform, this);
        bar.addViewRight(R.drawable.ic_default_hint, v -> {
            PlatformDialog dialog = new PlatformDialog(this);
            dialog.show();
        });
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(UserAPI.contribute().build());
    }


    @Override
    public int getContentViewId() {
        return R.layout.activity_contribute_platform_footer;
    }

    @Override
    public void findViews() {
        super.findViews();
        mTv = findView(R.id.contribute_tv_platform);
    }

    @Override
    public void setViews() {
        super.setViews();
        mTv.setEnabled(false);
        setOnClickListener(R.id.contribute_tv_platform);

        getAdapter().setListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contribute_tv_platform: {
                StringBuffer buffer = new StringBuffer();
                int size = mSelectedItem.size();
                for (int i = 0; i < size; ++i) {
                    mPlatform = mSelectedItem.get(i);
                    buffer.append(mPlatform.getString(TPlatformDetail.id));
                    if (i != size - 1) {
                        buffer.append(",");
                    }
                }
                refresh(RefreshWay.dialog);
                exeNetworkReq(KCodePlatform, UserAPI.unitNum(String.valueOf(buffer), mPlatform.getInt(TPlatformDetail.id)).build());
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KCodePlatform) {
            return JsonParser.error(r.getText());
        } else {
            return super.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KCodePlatform) {
            stopRefresh();
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast(R.string.contribute_platform_succeed);
                finish();
            } else {
                showToast(r.getMessage());
            }
        } else {
            super.onNetworkSuccess(id, result);
            showToast("获取单位号成功");
        }
    }

    @Override
    public void onPlatformChecked(int position, boolean isSelected) {
        if (isSelected) {
            mSelectedItem.add(getItem(position));
        } else {
            mSelectedItem.remove(getItem(position));
        }

        // 判断确认按钮
        if (mSelectedItem.isEmpty()) {
            // 不可点
            mTv.setEnabled(false);
        } else {
            // 可投稿
            mTv.setEnabled(true);
        }
    }
}
