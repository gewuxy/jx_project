package yy.doctor.activity.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/13
 */
public class MyEpnActivity extends BaseActivity {

    private TextView mTvTopUp;
    private TextView mTvInstruction;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_my_epn;
    }

    @Override
    public void initTitleBar() {

    }

    @Override
    public void findViews() {

        mTvTopUp = findView(R.id.my_epe_top_up_epn_tv);
        mTvInstruction = findView(R.id.my_epn_instruction);

    }

    @Override
    public void setViewsValue() {

        mTvTopUp.setOnClickListener(this);
        mTvInstruction.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.my_epe_top_up_epn_tv: {
                startActivity(RechargeEpnActivity.class);
            }
            break;
            case R.id.my_epn_instruction: {
                showToast("77");
            }
            break;
        }

    }
}
