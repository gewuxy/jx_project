package yy.doctor.activity.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;

import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 我的象数
 *
 * @author CaiXiang
 * @since 2017/4/13
 */
public class MyEpnActivity extends BaseActivity {

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_my_epn;
    }

    @Override
    public void initNavBar() {

        Util.addBackIcon(getNavBar(), "我的象城", this);

        getNavBar().addTextViewRight("明细", new OnClickListener() {

            @Override
            public void onClick(View v) {
                showToast("85654");
            }
        });

    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViewsValue() {

        setOnClickListener(R.id.my_epn_instruction);
        setOnClickListener(R.id.my_epe_recharge_epn_tv);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.my_epe_recharge_epn_tv: {
                startActivity(RechargeEpnActivity.class);
            }
            break;
            case R.id.my_epn_instruction: {
                startActivity(EpnUseRuleActivity.class);
            }
            break;
        }

    }
}
