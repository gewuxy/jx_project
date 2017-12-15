package jx.csp.ui.activity.me;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jx.csp.R;
import jx.csp.adapter.VipPermissionAdapter;
import jx.csp.constant.LangType;
import jx.csp.constant.VipPermission;
import jx.csp.constant.VipType;
import jx.csp.contact.VipManageContract;
import jx.csp.presenter.VipManagePresenterImpl;
import jx.csp.sp.SpApp;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;
import lib.ys.util.res.ResLoader;

/**
 * 会员管理
 *
 * @auther Huoxuyu
 * @since 2017/12/11
 */

public class VipManageActivity extends BaseActivity {

    private final int KMeetingNormLimit = 3;
    private final int KMeetingAdvancedLimit = 10;
    private final String KMeetingInfinite = "∞";

    private RecyclerView mRvPermission; //使用权限
    private RelativeLayout mLayoutCard;   //会员卡片
    private LinearLayout mLayoutSpell;

    private TextView mTvValidity;   //有效期
    private TextView mTvMeetCount;  //会议已使用数量
    private TextView mTvLimit;      //会议最大上限
    private TextView mTvAppName;    //app名字
    private TextView mTvVersion;    //会员套餐版本

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
        public void setAdapterData(int id, long packageStart, long packageEnd, int meetCount) {
            //设置会员权限布局
            if (LangType.en == SpApp.inst().getLangType()) {
                goneView(mLayoutSpell);
                mRvPermission.setLayoutManager(new GridLayoutManager(VipManageActivity.this, 3));
            } else {
                mRvPermission.setLayoutManager(new GridLayoutManager(VipManageActivity.this, 4));
            }

            VipPermissionAdapter adapter = new VipPermissionAdapter();
            List<VipPermission> list = new ArrayList<>();

            mTvAppName.setText(R.string.vip_manage_app_name);
            switch (id) {
                case VipType.norm: {
                    mLayoutCard.setBackgroundResource(R.drawable.vip_ic_norm_card);
                    mTvVersion.setText(R.string.vip_manage_norm_version);
                    if (meetCount > KMeetingNormLimit) {
                        mTvMeetCount.setTextColor(ResLoader.getColor(R.color.text_e43939));
                    }
                    mTvMeetCount.setText(meetCount);

                    list.add(VipPermission.norm_record);
                    list.add(VipPermission.norm_live);
                    list.add(VipPermission.norm_meeting);
                    list.add(VipPermission.norm_advertising);
                    list.add(VipPermission.norm_watermark);
                }
                break;
                case VipType.advanced: {
                    mLayoutCard.setBackgroundResource(R.drawable.vip_ic_advanced_card);

                    mTvValidity.setText(TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + getString(R.string.vip_manage_to) + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                    mTvVersion.setText(R.string.vip_manage_advanced_version);
                    if (meetCount > KMeetingAdvancedLimit) {
                        mTvMeetCount.setTextColor(ResLoader.getColor(R.color.text_e43939));
                    }
                    mTvMeetCount.setText(meetCount);
                    mTvLimit.setText(KMeetingAdvancedLimit);

                    list.add(VipPermission.advanced_record);
                    list.add(VipPermission.advanced_live);
                    list.add(VipPermission.advanced_meeting);
                    list.add(VipPermission.advanced_advertising);
                    list.add(VipPermission.advanced_watermark);
                }
                break;
                case VipType.profession: {
                    mLayoutCard.setBackgroundResource(R.drawable.vip_ic_profession_card);

                    mTvValidity.setText(TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + getString(R.string.vip_manage_to) + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                    mTvMeetCount.setText(meetCount);
                    mTvLimit.setText(KMeetingInfinite);
                    mTvVersion.setText(R.string.vip_manage_profession_version);

                    list.add(VipPermission.profession_record);
                    list.add(VipPermission.profession_live);
                    list.add(VipPermission.profession_meeting);
                    list.add(VipPermission.profession_advertising);
                    list.add(VipPermission.profession_watermark);
                }
                break;
            }
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
