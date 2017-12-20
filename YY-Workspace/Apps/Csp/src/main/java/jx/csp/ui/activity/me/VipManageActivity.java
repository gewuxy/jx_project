package jx.csp.ui.activity.me;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
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
import lib.ys.config.AppConfig.RefreshWay;
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

    private final String KMeetingAdvancedLimit = "10";
    private final String KMeetingInfinite = "∞";

    private RecyclerView mRvPermission; //使用权限
    private RelativeLayout mLayoutCard;   //会员卡片
    private View mViewSpell;

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
        mViewSpell = findView(R.id.vip_layout_spell);
    }

    @Override
    public void setViews() {
        refresh(RefreshWay.embed);
        mPresenter.checkPackage();
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            mPresenter.checkPackage();
        }
        return true;
    }

    private class VipManageViewImpl implements VipManageContract.V {

        @Override
        public void setAdapterData(int id, long packageStart, long packageEnd, int meetCount) {
            //设置会员权限布局
            if (LangType.en == SpApp.inst().getLangType()) {
                goneView(mViewSpell);
                mRvPermission.setLayoutManager(new GridLayoutManager(VipManageActivity.this, 3));
            } else {
                mRvPermission.setLayoutManager(new GridLayoutManager(VipManageActivity.this, 4));
            }

            VipPermissionAdapter adapter = new VipPermissionAdapter();
            List<VipPermission> list = new ArrayList<>();

            String count = String.valueOf(meetCount);
            mTvAppName.setText(getString(R.string.vip_manage_app_name));
            switch (id) {
                case VipType.norm: {
                    mLayoutCard.setBackgroundResource(R.drawable.vip_ic_norm_card);
                    mTvVersion.setText(getString(R.string.vip_manage_norm_version));
                    //获取会议大于上限值,显示红色字体
                    if (meetCount > 3) {
                        mTvMeetCount.setTextColor(ResLoader.getColor(R.color.text_e43939));
                    }
                    //设置已使用会议数量
                    mTvMeetCount.setText(count);

                    list.add(new VipPermission(getString(R.string.vip_manage_record), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_record));
                    list.add(new VipPermission(getString(R.string.vip_manage_live), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_live));
                    list.add(new VipPermission(getString(R.string.vip_manage_three_meeting), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_meet_num));
                    list.add(new VipPermission(getString(R.string.vip_manage_advertising), ResLoader.getColor(R.color.text_507a8b9f), R.drawable.vip_ic_un_advertising));
                    list.add(new VipPermission(getString(R.string.vip_manage_close_watermark), ResLoader.getColor(R.color.text_507a8b9f), R.drawable.vip_ic_un_watermark));
                }
                break;
                case VipType.advanced: {
                    mLayoutCard.setBackgroundResource(R.drawable.vip_ic_advanced_card);
                    if (LangType.en == SpApp.inst().getLangType()) {
                        mTvValidity.setText(getString(R.string.vip_manage_form) + TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + getString(R.string.vip_manage_to) + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                    } else {
                        mTvValidity.setText(TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + getString(R.string.vip_manage_to) + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                    }

                    mTvVersion.setText(getString(R.string.vip_manage_advanced_version));
                    //获取会议大于上限值,显示红色字体
                    if (meetCount > 10) {
                        mTvMeetCount.setTextColor(ResLoader.getColor(R.color.text_e43939));
                    }
                    //设置已使用会议数量
                    mTvMeetCount.setText(count);
                    //设置会议上限值
                    mTvLimit.setText(KMeetingAdvancedLimit);

                    list.add(new VipPermission(getString(R.string.vip_manage_record), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_record));
                    list.add(new VipPermission(getString(R.string.vip_manage_live), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_live));
                    list.add(new VipPermission(getString(R.string.vip_manage_ten_meeting), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_meet_num));
                    list.add(new VipPermission(getString(R.string.vip_manage_advertising), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_advertising));
                    list.add(new VipPermission(getString(R.string.vip_manage_close_watermark), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_watermark));
                }
                break;
                case VipType.profession: {
                    mLayoutCard.setBackgroundResource(R.drawable.vip_ic_profession_card);
                    if (LangType.en == SpApp.inst().getLangType()) {
                        mTvValidity.setText(getString(R.string.vip_manage_form) + TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + getString(R.string.vip_manage_to) + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                    } else {
                        mTvValidity.setText(TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + getString(R.string.vip_manage_to) + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                    }

                    //设置已使用会议数量
                    mTvMeetCount.setText(count);
                    //设置会议上限值
                    mTvLimit.setText(KMeetingInfinite);
                    mTvVersion.setText(getString(R.string.vip_manage_profession_version));

                    list.add(new VipPermission(getString(R.string.vip_manage_record), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_record));
                    list.add(new VipPermission(getString(R.string.vip_manage_live), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_live));
                    list.add(new VipPermission(getString(R.string.vip_manage_infinite_meeting), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_meet_num));
                    list.add(new VipPermission(getString(R.string.vip_manage_advertising), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_advertising));
                    list.add(new VipPermission(getString(R.string.vip_manage_custom_watermark), ResLoader.getColor(R.color.text_7a8b9f), R.drawable.vip_ic_watermark));
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
            VipManageActivity.this.setViewState(state);
        }
    }
}
