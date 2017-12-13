package jx.csp.ui.activity.me.vip;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jx.csp.R;
import jx.csp.adapter.VipPermissionAdapter;
import jx.csp.contact.VipManageContract;
import jx.csp.model.VipPermission;
import jx.csp.presenter.VipManagePresenterImpl;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;

/**
 * @auther Huoxuyu
 * @since 2017/12/11
 */

abstract public class BaseVipManageActivity extends BaseActivity {

    public final int KNorm = 1;    //标准
    public final int KAdvanced = 2;//高级
    public final int KProfession = 3;//专业

    private final String KMeetingLimit = "10";
    private final String KMeetingInfinite = "∞";

    public RecyclerView mRvPermission; //使用权限

    public ImageView mIvCard;   //会员卡片
    private TextView mTvValidity;
    private TextView mTvMeetCount;
    private TextView mTvLimit;

    private int[] mImage;
    private String[] mText;
    private int[] mPermissionId;

    public VipManageContract.P mPresenter;
    public VipManageContract.V mView;

    @Override
    public void initData() {
        mView = new VipManageViewImpl();
        mPresenter = new VipManagePresenterImpl(mView);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_vip_manage;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.person_center_vip_manage, this);
    }

    @Override
    public void findViews() {
        mRvPermission = findView(R.id.rv_vip_permission);
        mIvCard = findView(R.id.iv_vip_manage_card);

        mTvValidity = findView(R.id.tv_vip_validity);
        mTvMeetCount = findView(R.id.tv_vip_meet_num);
        mTvLimit = findView(R.id.tv_vip_meet_limit);
    }

    @Override
    public void setViews() {
        mPresenter.checkPackage();
    }

    /**
     * 设置权限布局
     */
    abstract protected void setLayout();

    /**
     * 设置会员卡片
     * @param id
     */
    abstract protected void setVipCard(int id);

    private class VipManageViewImpl implements VipManageContract.V {

        @Override
        public void setAdapterData(int id, long packageStart, long packageEnd, String meetCount) {
            setLayout();
            VipPermissionAdapter adapter = new VipPermissionAdapter();
            mPermissionId = new int[]{0, 1, 2, 3, 4};

            setVipCard(id);
            switch (id) {
                case KNorm: {
                    mTvMeetCount.setText(meetCount);
                    mImage = new int[]{
                            R.drawable.vip_ic_record,
                            R.drawable.vip_ic_live,
                            R.drawable.vip_ic_meet_num,
                            R.drawable.vip_ic_un_advertising,
                            R.drawable.vip_ic_un_watermark,
                    };

                    mText = new String[]{
                            getString(R.string.vip_manage_record),
                            getString(R.string.vip_manage_live),
                            getString(R.string.vip_manage_three_meeting),
                            getString(R.string.vip_manage_advertising),
                            getString(R.string.vip_manage_close_watermark),
                    };
                }
                break;
                case KAdvanced: {
                    mTvValidity.setText(TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + getString(R.string.vip_manage_to) + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                    YSLog.d("TAG", "" + System.currentTimeMillis());
                    mTvMeetCount.setText(meetCount);
                    mTvLimit.setText(KMeetingLimit);
                    mImage = new int[]{
                            R.drawable.vip_ic_record,
                            R.drawable.vip_ic_live,
                            R.drawable.vip_ic_meet_num,
                            R.drawable.vip_ic_advertising,
                            R.drawable.vip_ic_watermark,
                    };

                    mText = new String[]{
                            getString(R.string.vip_manage_record),
                            getString(R.string.vip_manage_live),
                            getString(R.string.vip_manage_ten_meeting),
                            getString(R.string.vip_manage_advertising),
                            getString(R.string.vip_manage_close_watermark),
                    };
                }
                break;
                case KProfession: {
                    mTvValidity.setText(TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + getString(R.string.vip_manage_to) + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                    mTvMeetCount.setText(meetCount);
                    mTvLimit.setText(KMeetingInfinite);
                    mImage = new int[]{
                            R.drawable.vip_ic_record,
                            R.drawable.vip_ic_live,
                            R.drawable.vip_ic_meet_num,
                            R.drawable.vip_ic_advertising,
                            R.drawable.vip_ic_watermark,
                    };

                    mText = new String[]{
                            getString(R.string.vip_manage_record),
                            getString(R.string.vip_manage_live),
                            getString(R.string.vip_manage_infinite_meeting),
                            getString(R.string.vip_manage_advertising),
                            getString(R.string.vip_manage_custom_watermark),
                    };
                }
                break;
            }

            List<VipPermission> list = mPresenter.addPermission(mPermissionId, mImage, mText);
            adapter.setData(list);
            mRvPermission.setAdapter(adapter);
        }

        @Override
        public void onStopRefresh() {

        }

        @Override
        public void setViewState(int state) {

        }
    }
}
