package yy.doctor.ui.activity.register;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.PcdAdapter;
import yy.doctor.model.Pcd;

/**
 * @auther Huoxuyu
 * @since 2017/7/4
 */
abstract public class BasePcdActivity extends BaseSRListActivity<Pcd, PcdAdapter> {

    private View mLayoutLocation;
    private ImageView mIvLocation;
    private TextView mTvLocation;
    private TextView mTvLocationFailure;

    private String mLocation;

    protected String mPcdDesc;
    protected String mProvinceId;
    protected String mCityId;
    protected String mProvince;
    protected String mCity;

    @Override
    public void findViews() {
        super.findViews();

        mLayoutLocation = findView(R.id.layout_province_location_load_layout);
        mIvLocation = findView(R.id.layout_province_location_load_iv);
        mTvLocation = findView(R.id.layout_pcd_header_tv);
        mTvLocationFailure = findView(R.id.layout_pcd_header_tv_failure);
    }

    @Override
    public void setViews() {
        super.setViews();

        setAutoLoadMoreEnabled(false);
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
}
