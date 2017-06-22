package yy.doctor.activity.me.epn;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.activity.me.CommonWebViewActivity;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.util.Util;

/**
 * 我的象数
 *
 * @author CaiXiang
 * @since 2017/4/13
 */
public class EpnActivity extends BaseActivity {

    //象数使用规则 链接
    private String mUrlEpnUseRule = "http://www.medcn.com:8081/v7/view/article/17061510101320742806";
    private TextView mTvEpn;

    @Override
    public void initData() {
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
                CommonWebViewActivity.nav(this, getString(R.string.epn_use_rule), mUrlEpnUseRule);
            }
            break;
        }
    }

}
