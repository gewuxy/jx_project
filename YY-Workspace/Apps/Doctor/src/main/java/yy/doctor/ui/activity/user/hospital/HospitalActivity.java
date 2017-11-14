package yy.doctor.ui.activity.user.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.ArrayList;
import java.util.List;

import lib.ys.action.IntentAction;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.LocateErrDialog;
import yy.doctor.model.hospital.HospitalTitle;
import yy.doctor.model.hospital.HospitalTitle.TText;
import yy.doctor.model.hospital.IHospital;
import yy.doctor.util.Util;

/**
 * @auther WangLan
 * @since 2017/7/19
 */
public class HospitalActivity extends BaseHospitalActivity {

    private boolean mIsFirstLoad; // 是否下拉或第一次加载
    private boolean mIsFromSet;  // 是否从设置页面返回的

    @Override
    public void initData(Bundle state) {
        super.initData(state);

        mIsFirstLoad = true;
        mIsFromSet = false;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_hospital;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.hospital, this);
    }

    @Override
    public void setViews() {
        super.setViews();

        if (!DeviceUtil.isNetworkEnabled()) {
            simulateSuccess();
        }
        setOnClickListener(R.id.hospital_search);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mIsFromSet) {
            mIsFromSet = false;
            refresh(RefreshWay.embed);
            startLocation();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hospital_search: {
                startActivity(new Intent(this, SearchHospitalActivity.class).putExtra(Extra.KData, mFromType));
            }
            break;
        }
    }

    @Override
    public boolean useErrorView() {
        return false;
    }

    /**
     * 下拉刷新的动作
     */
    @Override
    public void onSwipeRefreshAction() {
        mIsFirstLoad = true;
        if (mLatLng == null) {
            startLocation();
        }
    }

    @Override
    public void getDataFromNet() {
        if (mLatLng == null) {
            // 定位失败
            onGetPoiResult(null);
        } else {
            searchHospital(KHospital);
        }
    }

    @Override
    protected void locationSuccess() {
        getDataFromNet();
    }

    @Override
    protected void noLocationPermission() {
        if (!DeviceUtil.isNetworkEnabled()) {
            showToast(R.string.network_disabled);
        } else {
            //有网但是定位失败  显示dialog  只显示一次
            if (mFirstAction) {
                LocateErrDialog dialog = new LocateErrDialog(this, v -> {
                    mIsFromSet = true;
                    IntentAction.appSetup().launch();
                });
                dialog.show();
                mFirstAction = false;
            }
        }
        simulateSuccess();
    }

    @Override
    protected int getDistance() {
        return 10000;
    }

    @Override
    protected void searchSuccess(List<PoiInfo> info, Result<IHospital> r) {
        List<IHospital> data = new ArrayList<>();
        r.setCode(ErrorCode.KOk);
        if (mIsFirstLoad) {
            // 数据拼接
            HospitalTitle title = new HospitalTitle();
            title.put(TText.name, getString(R.string.recommend_hospital));
            data.add(title);// position = 0

            data.add(convertToHospital(info.get(0))); //第一条数据, position = 1

            title = new HospitalTitle();
            title.put(TText.name, getString(R.string.nearby_hospital));
            data.add(title);// position = 2

            for (int i = 1; i < info.size(); i++) {
                data.add(convertToHospital(info.get(i)));
            }
            mIsFirstLoad = false;
        } else {
            // 解析需要的数据
            for (PoiInfo poiInfo : info) {
                data.add(convertToHospital(poiInfo));
            }
        }
        r.setData(data);
    }

    @Override
    protected void searchError(Result<IHospital> r) {
        r.setCode(ErrorCode.KUnknown);
        r.setMessage("搜索不到你需要的信息");
    }

    @Override
    protected void returnResult() {
        Intent intent = new Intent()
                .putExtra(Extra.KData, mHospitalName);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.hospital_finish) {
            finish();
        }
    }
}
