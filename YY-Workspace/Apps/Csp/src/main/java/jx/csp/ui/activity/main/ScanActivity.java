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

import jx.csp.R;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Scan;
import jx.csp.model.meeting.Scan.DuplicateType;
import jx.csp.model.meeting.Scan.TScan;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.CommonAPI;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.ui.activity.record.CommonRecordActivityRouter;
import jx.csp.ui.activity.record.LiveRecordActivity;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.network.Result;
import lib.yy.notify.LiveNotifier;
import lib.yy.notify.LiveNotifier.LiveNotifyType;
import lib.yy.notify.LiveNotifier.OnLiveNotify;
import lib.yy.ui.activity.base.BaseActivity;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * 扫一扫
 * @auther WangLan
 * @since 2017/7/24
 */

public class ScanActivity extends BaseActivity implements OnScannerCompletionListener, OnCountDownListener, OnLiveNotify {

    private final int KFrameSize = 248;
    private final int KTopMargin = 96;
    private final int KDelayTime = 30; // 单位是秒

    private ToggleButton mBtn;
    private ScannerView mScannerView;

    private boolean mFlag;//图片更换
    private CountDown mCountDown; // 倒计时
    private Scan mScan;

    @Override
    public void initData() {
        LiveNotifier.inst().add(this);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_scan;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.setBackgroundResource(R.color.black);
        bar.addViewLeft(R.drawable.scan_ic_back, v -> {
            finish();
        });
        bar.addTextViewMid(getString(R.string.scan),R.color.white);
    }

    @Override
    public void findViews() {
        mScannerView = findView(R.id.scan_layout_scanner_view);
        mBtn = findView(R.id.scan_btn_flash);
    }

    @Override
    public void setViews() {
        mScannerView.setOnScannerCompletionListener(this);
        mScannerView.setLaserFrameBoundColor(ResLoader.getColor(R.color.btn_bg_blue));
        mScannerView.setDrawText("请对准二维码，耐心等待", 12, Color.WHITE, true, 20);
        mScannerView.setLaserFrameSize(KFrameSize, KFrameSize);
        mScannerView.setLaserFrameTopMargin(KTopMargin);
        mScannerView.setLaserColor(ResLoader.getColor(R.color.btn_bg_blue));
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
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
             showToast("无法识别此二维码");
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
     * @param result 扫描结果
     * @param parsedResult 结果类型
     * @param bitmap 扫描后的图像
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
                YSLog.d("urloooo",url);
                exeNetworkReq(CommonAPI.scanQrCode(url).build());
                break;
            }
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Scan.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Scan> r = (Result<Scan>) result;
        if (r.isSucceed()) {
            mScan = r.getData();
            // 先判断是否有其他人已经进入会议
            if (mScan.getInt(TScan.duplicate) == DuplicateType.no) {
                if (mScan.getInt(TScan.playType) == PlayType.reb) {
                    //录播
                    CommonRecordActivityRouter.create(mScan.getString(TScan.courseId)).route(this);
                } else {
                    startActivity(LiveRecordActivity.class);
                }
                finish();
            } else {
                String wsUrl = mScan.getString(TScan.wsUrl);
                if (TextUtil.isNotEmpty(wsUrl)) {
                    WebSocketServRouter.create(wsUrl).route(this);
                }
            }
        }
    }

    @Override
    public void onLiveNotify(int type, Object data) {
        switch (type) {
            case LiveNotifyType.accept: {
                if (mScan.getInt(TScan.playType) == PlayType.reb) {
                    //录播
                    CommonRecordActivityRouter.create(mScan.getString(TScan.courseId)).route(this);
                } else {
                    startActivity(LiveRecordActivity.class);
                }
                finish();
            }
            break;
            case LiveNotifyType.reject: {
                showToast(R.string.join_fail);
            }
            break;
        }
    }

}
