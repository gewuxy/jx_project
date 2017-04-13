package yy.doctor.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/13
 */
public class MyElephantNumActivity extends BaseActivity {

    private Button mTopUp_btn;
    private TextView mInstruction_tx;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_my_elephant_num;
    }

    @Override
    public void initTitleBar() {

    }

    @Override
    public void findViews() {

        mTopUp_btn=findView(R.id.top_up_elephant);
        mInstruction_tx=findView(R.id.my_elephant_instruction);

    }

    @Override
    public void setViewsValue() {

        mTopUp_btn.setOnClickListener(this);
        mInstruction_tx.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id=v.getId();
        switch (id){
            case R.id.top_up_elephant: {
                showToast("00");
            }
            break;
            case R.id.my_elephant_instruction: {
                showToast("77");
            }
            break;
        }

    }
}
