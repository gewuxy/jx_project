package yy.doctor.ui.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xys.libzxing.zxing.activity.CaptureActivity;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/11
 */

public class ScanActivity extends BaseActivity {
    TextView mTvResult;
    Button mBtn;


    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_zxing;   //占位，扫一扫界面
    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    @Override
    public void findViews() {
        mTvResult = findView(R.id.tv_result);
        mBtn = findView(R.id.scan_btn);
    }

    @Override
    public void setViews() {
        setOnClickListener(mBtn);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        startActivityForResult(new Intent(ScanActivity.this, CaptureActivity.class), 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String str_result = bundle.getString("result");
            mTvResult.setText(str_result);

        }
    }
}
