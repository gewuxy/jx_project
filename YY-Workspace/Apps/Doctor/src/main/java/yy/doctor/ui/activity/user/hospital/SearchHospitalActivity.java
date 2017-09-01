package yy.doctor.ui.activity.user.hospital;

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
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.ListResult;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.dialog.HintDialog;
import yy.doctor.model.hospital.IHospital;
import yy.doctor.util.Util;

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
        mEtSearch.setHint(R.string.search_hospital);
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
            simulateSuccess();
        }
    }

    @Override
    protected void searchSuccess(List<PoiInfo> info, ListResult<IHospital> r) {
        r.setCode(ErrorCode.KOk);
        for (PoiInfo i : info) {
            r.add(convertToHospital(i));
        }
    }

    @Override
    protected void searchError(ListResult<IHospital> r) {
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
    }

    @Override
    protected void returnResult() {
        notify(NotifyType.hospital_finish, mHospitalName);
    }

}
