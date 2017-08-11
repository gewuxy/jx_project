package yy.doctor.ui.activity.register;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.PcdAdapter;
import yy.doctor.model.Pcd;
import yy.doctor.model.Profile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * @auther Huoxuyu
 * @since 2017/7/4
 */
abstract public class BasePcdActivity extends BaseSRListActivity<Pcd, PcdAdapter> {

    private final int KIdCommit = 1;

    private LinearLayout mLinearLayout;
    private View mLayoutLocation;
    private ImageView mIvLocation;
    private TextView mTvLocation;
    private TextView mTvLocationFailure;

    private String mLocation;

    protected Place mPlace;

    @Override
    public void findViews() {
        super.findViews();

        mLayoutLocation = findView(R.id.layout_province_location_load_layout);
        mIvLocation = findView(R.id.layout_province_location_load_iv);
        mTvLocation = findView(R.id.layout_pcd_header_tv);
        mTvLocationFailure = findView(R.id.layout_pcd_header_tv_failure);
        mLinearLayout = findView(R.id.province_change);
    }

    @Override
    public void setViews() {
        super.setViews();

        setAutoLoadMoreEnabled(false);
        setOnClickListener(R.id.province_change);
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_pcd_header);
    }

    public View getLayoutLocation() {
        return mLayoutLocation;
    }

    public ImageView getIvLocation() {
        return mIvLocation;
    }

    public void setLocation(String s) {
        mLocation = s;

        if (TextUtil.isEmpty(s)) {
            showView(mTvLocationFailure);
        } else {
            goneView(mTvLocationFailure);
            mTvLocation.setText(s);
        }
    }

    public String getLocation() {
        return mLocation;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.province_change:
                    if (Profile.inst().isLogin() && mPlace != null) {
                        // 返回个人中心页面
                        refresh(RefreshWay.dialog);
                        exeNetworkReq(KIdCommit, NetFactory.newModifyBuilder()
                                .province(mPlace.getString(TPlace.province))
                                .city(mPlace.getString(TPlace.city))
                                .area(mPlace.getString(TPlace.district))
                                .build());
                    } else {
                        if (mPlace == null) {
                            // TODO: 处理定位中 或者定位失败情况
                            showToast("定位失敗");
                        } else {
                            // 返回注册页面
                            notify(NotifyType.province_finish, mPlace);

                            Intent intent = new Intent()
                                    .putExtra(yy.doctor.Extra.KData, mPlace);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (KIdCommit == id) {
            return JsonParser.error(r.getText());
        } else {
            return super.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KIdCommit) {
            Result r = (Result) result;
            if (r.isSucceed()) {
                Profile.inst().put(mPlace);
                Profile.inst().saveToSp();

                stopRefresh();

                notify(NotifyType.province_finish, mPlace);

                Intent intent = new Intent()
                        .putExtra(yy.doctor.Extra.KData, mPlace);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            super.onNetworkSuccess(id, result);
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.province_finish) {
            finish();
        }
    }
}
