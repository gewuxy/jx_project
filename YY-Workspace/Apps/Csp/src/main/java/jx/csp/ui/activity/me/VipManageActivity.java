package jx.csp.ui.activity.me;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import jx.csp.R;
import jx.csp.adapter.VipPermissionAdapter;
import jx.csp.constant.LangType;
import jx.csp.constant.VipType;
import jx.csp.contact.VipManageContract;
import jx.csp.model.VipPackage;
import jx.csp.model.VipPermission;
import jx.csp.presenter.VipManagePresenterImpl;
import jx.csp.sp.SpApp;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseRecyclerActivity;
import lib.ys.ConstantsEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;
import lib.ys.util.res.ResLoader;

/**
 * 会员管理
 *
 * @auther HuoXuYu
 * @since 2017/12/11
 */

public class VipManageActivity extends BaseRecyclerActivity<VipPermission, VipPermissionAdapter> {

    private final String KMeetingAdvancedLimit = "10";
    private final String KMeetingInfinite = "∞";

    private LinearLayout mLayoutSpell;
    private ImageView mIvCard;   //会员卡片

    private TextView mTvValidity;   //有效期
    private TextView mTvMeetCount;  //会议已使用数量
    private TextView mTvLimit;      //会议最大上限
    private TextView mTvVersion;    //会员套餐版本

    private VipManageContract.P mPresenter;
    private VipManageContract.V mView;

    @Override
    public void initData() {
        mView = new VipManageViewImpl();
        mPresenter = new VipManagePresenterImpl(mView);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.person_center_vip_manage, this);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_vip_manage;
    }

    @Override
    public void findViews() {
        super.findViews();
        mIvCard = findView(R.id.vip_iv_background);

        mTvValidity = findView(R.id.vip_tv_validity);
        mTvMeetCount = findView(R.id.vip_tv_meet_num);
        mTvLimit = findView(R.id.vip_tv_meet_limit);

        mTvVersion = findView(R.id.vip_tv_versions);
        mLayoutSpell = findView(R.id.vip_layout_spell);
    }

    @Override
    public void setViews() {
        super.setViews();
        refresh(RefreshWay.embed);
        mPresenter.checkPackage();

        getScrollableView().setOverScrollMode(View.OVER_SCROLL_NEVER);

        if (LangType.en == SpApp.inst().getLangType()) {
            goneView(mLayoutSpell);
        }
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            mPresenter.checkPackage();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        invalidate();
        if (LangType.en == SpApp.inst().getLangType() && mLayoutSpell != null) {
            goneView(mLayoutSpell);
        }
    }

    @Override
    protected LayoutManager initLayoutManager() {
        if (LangType.en == SpApp.inst().getLangType()) {
            return new GridLayoutManager(this, 3);
        } else {
            return new GridLayoutManager(this, 4);
        }
    }

    private class VipManageViewImpl implements VipManageContract.V {

        @Override
        public void setPackageData(VipPackage p) {
            // 设置会议总数
            int meetTotalCount = p.getInt(VipPackage.TPackage.meetTotalCount);
            mTvMeetCount.setText(String.valueOf(meetTotalCount));

            // 设置有效期,false为有限期，true为无限期
            boolean unlimited = p.getBoolean(VipPackage.TPackage.unlimited);
            String validity = ConstantsEx.KEmpty;
            if (unlimited) {
                validity = getString(R.string.vip_manage_time);
            } else {
                long startTime = p.getLong(VipPackage.TPackage.packageStart);
                long endTime = p.getLong(VipPackage.TPackage.packageEnd);
                if (startTime == ConstantsEx.KInvalidValue || endTime == ConstantsEx.KInvalidValue) {
                    // 不返回当不限期
                    validity = getString(R.string.vip_manage_time);
                } else {
                    String start = TimeFormatter.milli(startTime, TimeFormat.simple_ymd);
                    String end = TimeFormatter.milli(endTime, TimeFormat.simple_ymd);
                    if (LangType.en == SpApp.inst().getLangType()) {
                        validity = getString(R.string.vip_manage_form) + start + getString(R.string.vip_manage_to) + end;
                    } else {
                        validity = start + getString(R.string.vip_manage_to) + end;
                    }
                }
            }
            mTvValidity.setText(validity);

            int id = p.getInt(VipPackage.TPackage.id);
            switch (id) {
                case VipType.norm: {
                    //获取会议大于上限值,显示红色字体
                    if (meetTotalCount > 3) {
                        mTvMeetCount.setTextColor(ResLoader.getColor(R.color.text_e43939));
                    }
                }
                break;
                case VipType.advanced: {
                    mIvCard.setBackgroundResource(R.drawable.vip_ic_advanced_card);

                    //获取会议大于上限值,显示红色字体
                    if (meetTotalCount > 10) {
                        mTvMeetCount.setTextColor(ResLoader.getColor(R.color.text_e43939));
                    }
                    //设置会议上限值
                    mTvLimit.setText(KMeetingAdvancedLimit);
                    mTvVersion.setText(getString(R.string.vip_manage_advanced_version));
                }
                break;
                case VipType.profession: {
                    mIvCard.setBackgroundResource(R.drawable.vip_ic_profession_card);

                    //设置会议上限值
                    mTvLimit.setText(KMeetingInfinite);
                    mTvVersion.setText(getString(R.string.vip_manage_profession_version));
                }
                break;
            }
        }

        @Override
        public void setPermission(List<VipPermission> list) {
            setData(list);
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
