package yy.doctor.activity.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 我的象数
 *
 * @author CaiXiang
 * @since 2017/4/13
 */
public class EpnActivity extends BaseActivity {

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
        Util.addBackIcon(bar, "我的象数", this);
        bar.addTextViewRight("明细", new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(EpnDetailsActivity.class);
            }
        });
    }

    @Override
    public void findViews() {

        mTvEpn = findView(R.id.epn_tv);
    }

    @Override
    public void setViews() {

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
                startActivity(EpnUseRuleActivity.class);
            }
            break;
        }

    }
}
