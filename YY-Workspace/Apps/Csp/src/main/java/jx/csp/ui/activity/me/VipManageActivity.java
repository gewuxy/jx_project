package jx.csp.ui.activity.me;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import jx.csp.R;
import jx.csp.adapter.VipPermissionAdapter;
import jx.csp.constant.LangType;
import jx.csp.constant.VipType;
import jx.csp.contact.VipManageContract;
import jx.csp.model.VipPermission;
import jx.csp.presenter.VipManagePresenterImpl;
import jx.csp.sp.SpApp;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;

/**
 * 会员管理
 *
 * @auther Huoxuyu
 * @since 2017/12/11
 */

public class VipManageActivity extends BaseActivity {

    private final String KMeetingLimit = "10";
    private final String KMeetingInfinite = "∞";

    private RecyclerView mRvPermission; //使用权限
    private RelativeLayout mLayoutCard;   //会员卡片
    private LinearLayout mLayoutSpell;

    private TextView mTvValidity;
    private TextView mTvMeetCount;
    private TextView mTvLimit;
    private TextView mTvAppName;
    private TextView mTvVersion;

    private int[] mImage;
    private String[] mText;
    private int[] mPermissionId;

    private VipManageContract.P mPresenter;
    private VipManageContract.V mView;

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
        mRvPermission = findView(R.id.vip_rv_permission);
        mLayoutCard = findView(R.id.vip_layout_background);

        mTvValidity = findView(R.id.vip_tv_validity);
        mTvMeetCount = findView(R.id.vip_tv_meet_num);
        mTvLimit = findView(R.id.vip_tv_meet_limit);

        mTvAppName = findView(R.id.vip_tv_app_name);
        mTvVersion = findView(R.id.vip_tv_versions);
        mLayoutSpell = findView(R.id.vip_layout_spell);
    }

    @Override
    public void setViews() {
        mPresenter.checkPackage();
    }


    private class VipManageViewImpl implements VipManageContract.V {

        @Override
        public void setAdapterData(int id, long packageStart, long packageEnd, String meetCount) {
            //设置会员权限布局
            if (LangType.en == SpApp.inst().getLangType()) {
                goneView(mLayoutSpell);
                mRvPermission.setLayoutManager(new GridLayoutManager(VipManageActivity.this, 3));
            } else {
                mRvPermission.setLayoutManager(new GridLayoutManager(VipManageActivity.this, 4));
            }

            VipPermissionAdapter adapter = new VipPermissionAdapter();
            mPermissionId = new int[]{0, 1, 2, 3, 4};

            YSLog.d("TAG", "" + System.currentTimeMillis());
            mTvAppName.setText(R.string.vip_manage_app_name);
            switch (id) {
                case VipType.norm: {
                    mLayoutCard.setBackgroundResource(R.drawable.vip_ic_norm_card);

                    mTvVersion.setText(R.string.vip_manage_norm_version);
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
                case VipType.advanced: {
                    mLayoutCard.setBackgroundResource(R.drawable.vip_ic_advanced_card);

                    mTvValidity.setText(TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + getString(R.string.vip_manage_to) + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                    mTvMeetCount.setText(meetCount);
                    mTvLimit.setText(KMeetingLimit);
                    mTvVersion.setText(R.string.vip_manage_advanced_version);

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
                case VipType.profession: {
                    mLayoutCard.setBackgroundResource(R.drawable.vip_ic_profession_card);

                    mTvValidity.setText(TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + getString(R.string.vip_manage_to) + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                    mTvMeetCount.setText(meetCount);
                    mTvLimit.setText(KMeetingInfinite);
                    mTvVersion.setText(R.string.vip_manage_profession_version);

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
