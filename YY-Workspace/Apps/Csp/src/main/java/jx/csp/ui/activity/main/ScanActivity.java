package jx.csp.ui.activity.main;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ToggleButton;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerView;
import com.mylhyl.zxing.scanner.common.Scanner;

import jx.csp.App.NavBarVal;
import jx.csp.R;
import jx.csp.model.meeting.Course.CourseType;
import jx.csp.model.meeting.Scan;
import jx.csp.model.meeting.Scan.DuplicateType;
import jx.csp.model.meeting.Scan.TScan;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.CommonAPI;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.ui.activity.record.LiveAudioActivityRouter;
import jx.csp.ui.activity.record.RecordActivityRouter;
import lib.jx.notify.LiveNotifier;
import lib.jx.notify.LiveNotifier.LiveNotifyType;
import lib.jx.notify.LiveNotifier.OnLiveNotify;
import lib.jx.ui.activity.base.BaseActivity;
import lib.jx.util.CountDown;
import lib.jx.util.CountDown.OnCountDownListener;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.config.AppConfig;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;

/**
 * 扫一扫
 *
 * @auther WangLan
 * @since 2017/7/24
 */

public class ScanActivity extends BaseActivity implements OnScannerCompletionListener, OnCountDownListener, OnLiveNotify {

    private final int KFrameSize = 248;
    private final int KTopMargin = 96 + NavBarVal.KHeightDp; // ps: 在界面上层故要加上NavBarVal的高度
    private final int KDelayTime = 30; // 单位是秒

    private ToggleButton mBtn;
    private ScannerView mScannerView;

    private boolean mFlag;//图片更换
    private CountDown mCountDown; // 倒计时
    private Scan mScan;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_scan;
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.setBackgroundResource(R.color.transparent);
        bar.addViewLeft(R.drawable.scan_ic_back, v -> finish());
        bar.addTextViewMid(getString(R.string.scan), R.color.white);
    }

    @Override
    public void findViews() {
        mScannerView = findView(R.id.scan_layout_scanner_view);
        mBtn = findView(R.id.scan_btn_flash);
    }

    @Override
    public void setViews() {
        mScannerView.setOnScannerCompletionListener(this);
        mScannerView.setLaserFrameBoundColor(ResLoader.getColor(R.color.text_ace400));
        mScannerView.setDrawText(getString(R.string.aim_at_the_code), 12, Color.WHITE, true, 20);
        mScannerView.setLaserFrameSize(KFrameSize, KFrameSize);
        mScannerView.setLaserFrameTopMargin(KTopMargin);
        mScannerView.setLaserColor(ResLoader.getColor(R.color.text_ace400));
        mScannerView.setLaserLineResId(R.drawable.scan_ic_laser_line);

        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int top = Scanner.dp2px(ScanActivity.this, KFrameSize + KTopMargin) - mBtn.getHeight();
                LayoutParams params = (LayoutParams) mBtn.getLayoutParams();
                params.topMargin = top;
                mBtn.setLayoutParams(params);
                removeOnGlobalLayoutListener(this);
            }
        });

        mBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mFlag = !mFlag;
            mBtn.setSelected(mFlag);
            mScannerView.toggleLight(isChecked);
        });
        LiveNotifier.inst().add(this);
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            showToast(R.string.fail_to_identify);
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCountDown = new CountDown();
        mCountDown.setListener(this);
        mCountDown.start(KDelayTime);
        mScannerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCountDown != null) {
            mCountDown.stop();
        }
        mScannerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LiveNotifier.inst().remove(this);
    }

    /**
     * 扫描成功后回调
     *
     * @param result       扫描结果
     * @param parsedResult 结果类型
     * @param bitmap       扫描后的图像
     */
    @Override
    public void OnScannerCompletion(com.google.zxing.Result result, ParsedResult parsedResult, Bitmap bitmap) {
        if (mCountDown != null) {
            mCountDown.stop();
        }

        ParsedResultType type = parsedResult.getType();
        switch (type) {
            case ADDRESSBOOK:
                break;
            case URI: {
                String url = parsedResult.toString();
                exeNetworkReq(CommonAPI.scanQrCode(url).build());
                break;
            }
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Scan.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            mScan = (Scan) r.getData();
            // 先判断是否有其他人已经进入会议
            if (mScan.getInt(TScan.duplicate) == DuplicateType.no) {
                joinRecord();
            } else {
                refresh(AppConfig.RefreshWay.dialog);
                String wsUrl = mScan.getString(TScan.wsUrl);
                if (TextUtil.isNotEmpty(wsUrl)) {
                    WebSocketServRouter.create(wsUrl).route(this);
                }
            }
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void onLiveNotify(int type, Object data) {
        switch (type) {
            case LiveNotifyType.accept: {
                stopRefresh();
                WebSocketServRouter.stop(this);
                YSLog.d(TAG, "scan activity accept WebSocketServRouter.stop");
                joinRecord();
            }
            break;
            case LiveNotifyType.reject: {
                stopRefresh();
                WebSocketServRouter.stop(this);
                YSLog.d(TAG, "scan activity reject WebSocketServRouter.stop");
                finish();
                showToast(R.string.join_fail);
            }
            break;
        }
    }

    protected void joinRecord() {
        if (mScan.getInt(TScan.playType) == CourseType.reb) {
            //录播
            RecordActivityRouter.create(mScan.getString(TScan.courseId)).route(this);
        } else {
            LiveAudioActivityRouter.create(mScan.getString(TScan.courseId)).route(this);
        }
        finish();
    }
}
