package yy.doctor.ui.activity.meeting;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import inject.annotation.router.Arg;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.TimeUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.R;
import yy.doctor.network.JsonParser;

/**
 * 提交问卷结果和签到结果公共的界面
 *
 * @auther : GuoXuan
 * @since : 2017/6/10
 */
public abstract class BaseResultActivity extends BaseActivity implements OnCountDownListener {

    private static final int KReturnTime = 3; // 关闭倒计时

    @Arg
    public String mMeetId; // 会议id
    @Arg
    public String mModuleId; // 模块id

    protected TextView mTvWelcome; // 欢迎
    protected TextView mTvResult; // 结果
    protected TextView mTvResultMsg; // 成功的时间 / 失败的原因

    private CountDown mCountDown; // 倒计时
    private TextView mTvReturn; // 返回

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_result;
    }

    @CallSuper
    @Override
    public void initData() {
        mCountDown = new CountDown();
        mCountDown.setListener(this);
    }

    @CallSuper
    @Override
    public void findViews() {
        mTvWelcome = findView(R.id.result_iv_welcome);
        mTvResultMsg = findView(R.id.result_tv_result_msg);
        mTvResult = findView(R.id.result_tv_result);
        mTvReturn = findView(R.id.result_tv_return);
    }

    @CallSuper
    @Override
    public void setViews() {
        setOnClickListener(mTvReturn);
        getDataFromNet();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        Result r = (Result) result;
        if (r.isSucceed()) {
            // 成功
            successResult();
            long signTime = System.currentTimeMillis();
            mTvResultMsg.setText("时间 " + TimeUtil.formatMilli(signTime, TimeUtil.TimeFormat.from_h_to_m_24));
        } else {
            // 失败
            errorResult(r.getMessage());
        }
        mCountDown.start(KReturnTime);
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            getDataFromNet();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.result_tv_return:
                finish();
                break;
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    public void onCountDown(long remainCount) {
        mTvReturn.setText(remainCount + "秒后返回");
        if (remainCount == 0) {
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();

        // 退出
        notify(NotifyType.study_end);
        mCountDown.recycle();
    }

    protected abstract void getDataFromNet();

    protected abstract void successResult();

    protected abstract void errorResult(String error);
}