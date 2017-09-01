package yy.doctor.ui.activity.user.hospital;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.ArrayList;
import java.util.List;

import lib.bd.location.Location;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.ListResult;
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

    private boolean mIsFirst = true;

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.hospital, this);
    }

    @Nullable
    @Override
    public View createHeaderView() {
        return inflate(R.layout.activity_hospital_header);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.hospital_search);
    }

    @Override
    public void swipeRefresh() {
        Location.inst().start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hospital_search: {
                startActivity(SearchHospitalActivity.class);
            }
            break;
        }
    }

    /**
     * 下拉刷新的动作
     */
    @Override
    public void onSwipeRefreshAction() {
        mIsFirst = true;
    }

    @Override
    public void getDataFromNet() {
        if (mLatLng == null) {
            // 定位失败(之前无网)
            Location.inst().start();
        } else {
            searchHospital(KHospital);
        }
    }

    @Override
    protected void noLocationPermission() {
        if (!DeviceUtil.isNetworkEnabled()) {
            showToast(R.string.network_disabled);
        } else {
            //有网但是定位失败  显示dialog
            LocateErrDialog dialog = new LocateErrDialog(this);
            dialog.show();
        }
        simulateSuccess(KIdHospital);
    }

    @Override
    protected void returnResult() {
        Intent intent = new Intent()
                .putExtra(Extra.KData, mHospitalName);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void locationSuccess() {
        getDataFromNet();
    }

    @Override
    protected void searchSuccess(List<PoiInfo> info, ListResult<IHospital> r) {
        List<IHospital> data = new ArrayList<>();
        r.setCode(ErrorCode.KOk);
        if (mIsFirst) {
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

            mIsFirst = false;
        } else {
            // 解析需要的数据
            for (PoiInfo poiInfo : info) {
                data.add(convertToHospital(poiInfo));
            }
        }
        r.setData(data);
    }

    @Override
    protected void searchError(ListResult<IHospital> r) {
        r.setCode(ErrorCode.KUnKnow);
        r.setMessage("搜索不到你需要的信息");
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.hospital_finish) {
            finish();
        }
    }

    @Override
    public boolean useErrorView() {
        return false;
    }
}
