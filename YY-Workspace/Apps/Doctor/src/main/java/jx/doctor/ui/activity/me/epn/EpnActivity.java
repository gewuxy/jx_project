package jx.doctor.ui.activity.me.epn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseActivity;
import jx.doctor.R;
import jx.doctor.model.Profile;
import jx.doctor.model.Profile.TProfile;
import jx.doctor.network.UrlUtil;
import jx.doctor.ui.activity.CommonWebViewActivityRouter;
import jx.doctor.ui.activity.me.epc.EpcActivity;
import jx.doctor.util.Util;

/**
 * 我的象数
 *
 * @author CaiXiang
 * @since 2017/4/13
 */
public class EpnActivity extends BaseActivity {

    //象数使用规则 链接
    private String mUrlEpnUseRule = UrlUtil.getHostName() + "/view/article/17061510101320742806";
    private TextView mTvEpn;

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_epn;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.epn, this);
        bar.addTextViewRight(R.string.epn_detail, v -> startActivity(EpnDetailsActivity.class));
    }

    @Override
    public void findViews() {

        mTvEpn = findView(R.id.epn_tv);
    }

    @Override
    public void setViews() {

        mTvEpn.setText(Profile.inst().getString(TProfile.credits));

        setOnClickListener(R.id.epn_instruction);
        setOnClickListener(R.id.epe_tv_btn);
        setOnClickListener(R.id.epc_tv_btn);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.epe_tv_btn: {
                startActivity(EpnRechargeActivity.class);
            }
            break;
            case R.id.epn_instruction: {
                CommonWebViewActivityRouter.create(getString(R.string.epn_use_rule), mUrlEpnUseRule)
                        .route(this);
            }
            break;
            case R.id.epc_tv_btn: {
                startActivity(EpcActivity.class);
            }
            break;
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        //充值象数后，像素值要改变
        if (type == NotifyType.profile_change) {
            mTvEpn.setText(Profile.inst().getString(TProfile.credits));
        }

    }
}
