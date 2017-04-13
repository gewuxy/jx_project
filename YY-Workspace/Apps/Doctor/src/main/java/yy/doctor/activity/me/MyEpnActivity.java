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

    private TextView mTVTopUp;
    private TextView mtVInstruction;

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

        mTVTopUp = findView(R.id.my_epe_top_up_epe_tv);
        mtVInstruction = findView(R.id.my_epe_instruction);

    }

    @Override
    public void setViewsValue() {

        mTVTopUp.setOnClickListener(this);
        mtVInstruction.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.my_epe_top_up_epe_tv: {
                showToast("00");
            }
            break;
            case R.id.my_epe_instruction: {
                showToast("77");
            }
            break;
        }

    }
}
