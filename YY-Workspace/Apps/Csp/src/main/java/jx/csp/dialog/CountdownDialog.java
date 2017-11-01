package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.view.CircleProgressView;
import lib.yy.dialog.BaseDialog;

/**
 * @auther yuansui
 * @since 2017/11/1
 */

public class CountdownDialog extends BaseDialog {

    private CircleProgressView mProgressBar;
    private TextView mTvFiveSecond;

    public CountdownDialog(Context context) {
        super(context);
    }

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_count_down;
    }

    @Override
    public void findViews() {
        mProgressBar = findView(R.id.v_meeting_detail_progress);
        mTvFiveSecond = findView(R.id.tv_five_second);
    }

    @Override
    public void setViews() {
        mProgressBar.setProgress(0);
        // FIXME: 2017/10/26 当另外一个设备收到提示框后，才显示TV五秒倒计时，websocket,如果a拒绝，则提示进入失败
//                  showView(mTvFiveSecond);
    }
}
