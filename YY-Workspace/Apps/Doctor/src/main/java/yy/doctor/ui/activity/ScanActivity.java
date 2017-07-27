package yy.doctor.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerView;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Scan;
import yy.doctor.model.Scan.TScan;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * @auther WangLan
 * @since 2017/7/24
 */

public class ScanActivity extends BaseActivity implements OnScannerCompletionListener {

    private ScannerView mScannerView;
    private final int KScanId = 0;
    private final int KScan = 1;


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
    }

    @Override
    public void findViews() {
        mScannerView = findView(R.id.scan_layout_scanner_view);
    }

    @Override
    public void setViews() {
        mScannerView.setOnScannerCompletionListener(this);
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
                    showToast("有id");
                    String[] s = url.split("=");
                    String masterId = s[1];
                    exeNetworkReq(KScanId, NetFactory.scan(masterId));
                } else {
                    //http://10.0.0.234/api/api/register/scan_register
                    exeNetworkReq(NetFactory.scan());
                    showToast("没有Id");
                }
                // exeNetworkReq(NetFactory.scan(mPhone.replace(" ", ""), CaptchaType.fetch));
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
                //List<Integer> masterId = data.getList(TScan.masterId);
               /* for (int i = 0; i < masterId.size(); i++) {
                    YSLog.d(TAG, masterId.get(i).toString());
                }*/
                // YSLog.d(TAG, masterId.toString());

                List<String> name = data.getList(TScan.name);
                StringBuilder sb = new StringBuilder();
                for (String s : name) {
                    sb.append(s + " ");
                }
                YSLog.d(TAG, sb.toString());
                Intent i = new Intent().putExtra(Extra.KData, sb.toString());
                setResult(RESULT_OK, i);
            } else {
                showToast(r.getError());
            }
        } else {
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast("成功");
                setResult(RESULT_OK);
            } else {
                showToast(r.getError());
            }
        }
        finish();
    }
}
