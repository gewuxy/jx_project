package yy.doctor.ui.activity.user.register;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.ToggleButton;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerView;
import com.mylhyl.zxing.scanner.common.Scanner;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Scan;
import yy.doctor.model.Scan.TScan;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.RegisterAPI;
import yy.doctor.util.Util;

/**
 * @auther WangLan
 * @since 2017/7/24
 */

public class ScanActivity extends BaseActivity implements OnScannerCompletionListener {

    private final int KScanId = 0;
    private final int KScan = 1;

    private ToggleButton mBtn;
    private ScannerView mScannerView;

    private boolean mFlag;//图片更换

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_scan;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.setBackgroundResource(R.color.black);
        Util.addBackIcon(bar, "扫一扫", this);
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
        mScannerView.setLaserFrameSize(248, 248);
        mScannerView.setLaserFrameTopMargin(96);
        mScannerView.setLaserColor(ResLoader.getColor(R.color.btn_bg_blue));
        mScannerView.setLaserLineResId(R.mipmap.scan_ic_laser_line);

        int i = Scanner.dp2px(this, 288);
     //   MarginLayoutParams p = new MarginLayoutParams();
//        mBtn.ma
       // LayoutFitter.
      /*  mBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mFlag = !mFlag;
//            mBtn.setSelected(mFlag);
            mScannerView.toggleLight(isChecked);
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.onPause();
    }

    @Override
    public void OnScannerCompletion(com.google.zxing.Result result, ParsedResult parsedResult, Bitmap bitmap) {
        ParsedResultType type = parsedResult.getType();
        switch (type) {
            case ADDRESSBOOK:
                break;
            case URI: {
                String url = parsedResult.toString();
                if (url.contains("masterId")) {
                    //http://10.0.0.234/api/api/register/scan_register?masterId=8,14
                    // showToast("有id");
                    String[] s = url.split("=");
                    String masterId = s[1];
                    exeNetworkReq(KScanId, RegisterAPI.scan().masterId(masterId).build());
                } else {
                    //http://10.0.0.234/api/api/register/scan_register
                    exeNetworkReq(KScanId, RegisterAPI.scan().build());
                    // showToast("没有Id");
                }
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
        if (id == KScanId) {
            Result<Scan> r = (Result<Scan>) result;
            if (r.isSucceed()) {
                Scan data = r.getData();

                List<Integer> masterId = data.getList(TScan.masterId);
                StringBuilder sbId = new StringBuilder();
                for (Integer mas : masterId) {
                    sbId.append(mas + ",");
                }
                sbId.deleteCharAt(sbId.length() - 1);
                YSLog.d(TAG, sbId + "");
                List<String> name = data.getList(TScan.name);
                StringBuilder sb = new StringBuilder();
                for (String s : name) {
                    sb.append(s + " ");
                }
                YSLog.d(TAG, sb.toString());
                Intent i = new Intent().putExtra(Extra.KData, sb.toString())
                        .putExtra(Extra.KId, sbId.toString());
                setResult(RESULT_FIRST_USER, i);
            } else {
                showToast(r.getMessage());
            }
        } else if (id == KScan) {
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast("成功");
                setResult(RESULT_FIRST_USER);
            } else {
                showToast(r.getMessage());
            }
        }
        finish();
    }

}
