package yy.doctor.ui.activity;

import android.support.annotation.NonNull;

import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.ScannerView;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/24
 */

public class ScanActivity extends BaseActivity {

    private ScannerView mScannerView;

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
        mScannerView.setOnScannerCompletionListener((result, parsedResult, bitmap) -> {
            ParsedResultType type = parsedResult.getType();
            switch (type) {
                case ADDRESSBOOK:
                    break;
                case URI:
                    break;
                case TEXT:
                    break;
            }
        });
    }

    @Override
    protected void onResume() {
        mScannerView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mScannerView.onPause();
        super.onPause();
    }
}
