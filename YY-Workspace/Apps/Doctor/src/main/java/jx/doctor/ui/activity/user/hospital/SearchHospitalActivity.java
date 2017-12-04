package jx.doctor.ui.activity.user.hospital;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.ArrayList;
import java.util.List;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;
import lib.jx.network.BaseJsonParser.ErrorCode;
import lib.jx.network.Result;
import lib.jx.notify.Notifier.NotifyType;
import jx.doctor.R;
import jx.doctor.dialog.HintDialog;
import jx.doctor.model.hospital.IHospital;
import jx.doctor.util.Util;

/**
 * 医院搜索
 *
 * @auther WangLan
 * @since 2017/7/20
 */
public class SearchHospitalActivity extends BaseHospitalActivity {

    private EditText mEtSearch;
    private OnClickListener mSearchListener;

    @Override
    public void initData() {
        super.initData();

        mSearchListener = v -> {
            if (TextUtil.isEmpty(Util.getEtString(mEtSearch))) {
                showToast(getString(R.string.please_input_search_content));
                return;
            }
            if (KeyboardUtil.isActive()) {
                // 键盘显示就隐藏
                KeyboardUtil.hideFromView(mEtSearch);
            }
            if (Util.noNetwork()) {
                return;
            }
            if (mLatLng == null) {
                // 定位失败
                startLocation();
            } else {
                refresh(RefreshWay.embed);
            }
        };
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);

        View view = inflate(R.layout.layout_meeting_nav_bar_search);
        mEtSearch = (EditText) view.findViewById(R.id.meeting_search_nav_bar_et);
        mEtSearch.setHint(R.string.please_input_hospital_name);
        bar.addViewMid(view, null);

        bar.addTextViewRight(R.string.search, mSearchListener);
    }

    @Override
    public void setViews() {
        super.setViews();

        setRefreshEnabled(false);
        setViewState(ViewState.normal);

        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mEtSearch.requestFocus();
                KeyboardUtil.showFromView(mEtSearch);
                removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void getDataFromNet() {
        searchHospital(Util.getEtString(mEtSearch).concat(KHospital));
    }

    @Override
    protected void locationSuccess() {
        mEtSearch.performClick();
    }

    @Override
    protected void noLocationPermission() {
        if (mFirstAction) {
            mFirstAction = false;
        } else {
            // 模拟搜索不到内容
            onGetPoiResult(null);
        }
    }

    @Override
    protected int getDistance() {
        return 10000;
       // return Integer.MAX_VALUE;
    }

    @Override
    protected void searchSuccess(List<PoiInfo> info, Result<IHospital> r) {
        r.setCode(ErrorCode.KOk);
        for (PoiInfo i : info) {
            r.add(convertToHospital(i));
        }
    }

    @Override
    protected void searchError(Result<IHospital> r) {
        //找不到医院，弹出对话框变为默认医院
        HintDialog dialog = new HintDialog(this);
        dialog.addHintView(inflate(R.layout.dialog_find_hospital_fail));
        dialog.addBlueButton(R.string.cancel);
        dialog.addBlueButton(R.string.confirm, v -> chooseLevel(Util.getEtString(mEtSearch)));
        dialog.show();

        // 模拟成功无数据， 显示empty footer view
        r.setCode(ErrorCode.KOk);
        List<IHospital> hospitals = new ArrayList<>();
        r.setData(hospitals);
        goneView(mTvEmpty);

    }

    @Override
    protected void returnResult() {
        notify(NotifyType.hospital_finish, mHospitalName);
    }

}
