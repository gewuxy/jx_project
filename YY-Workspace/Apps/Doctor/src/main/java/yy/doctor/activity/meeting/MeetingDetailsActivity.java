package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.List;

import lib.bd.location.Location;
import lib.network.error.NetError;
import lib.network.model.NetworkResp;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Resp;
import yy.doctor.BuildConfig;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.LocationDialog;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.model.meet.InfoModule;
import yy.doctor.model.meet.InfoModule.TInfoModule;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

import static lib.ys.util.permission.Permission.location;

/**
 * 会议详情界面
 *
 * 日期 : 2017/4/21
 * 创建人 : guoxuan
 */

public class MeetingDetailsActivity extends BaseActivity {

    private static final int KModuleCount = 4;//模块数
    private static final int KDividerWDp = 1;//分割线的宽
    private static final int KDividerHDp = 16;//分割线的高

    private String mMeetId;
    private TextView mTvAward;
    private TextView mTvTitle;//会议名称
    private TextView mTvSection;//会议科室

    //播放图片
    private NetworkImageView mIvPlay;
    //公众号图标
    private NetworkImageView mIvNumber;

    //主讲者
    private TextView mTvGN;//名字
    private TextView mTvGP;//职位
    private TextView mTvGH;//医院
    private NetworkImageView mIvGP;//头像

    //底部按钮
    private Drawable mExam;//考试
    private Drawable mQue;//问卷
    private Drawable mVideo;//视频
    private Drawable mSign;//签到
    private View mDivider;//分割线
    private LinearLayout mLlModules;//模块的容器
    private TextView mTvSee;//观看会议
    private int mModuleCount;//底部添加的模块数
    private LayoutParams mModuleParams;//模块的参数
    private LayoutParams mDividerParams;//分割线的参数

    private LocationDialog mLocationDialog;

    @IntDef({
            ModuleType.ppt,
            ModuleType.video,
            ModuleType.exam,
            ModuleType.que,
            ModuleType.sign,
    })
    private @interface ModuleType {
        int ppt = 1;//微课
        int video = 2;//视频
        int exam = 3;//考试
        int que = 4;//问卷
        int sign = 5;//签到
    }

    public static void nav(Context context, String meetId) {
        Intent i = new Intent(context, MeetingDetailsActivity.class);
        i.putExtra(Extra.KData, meetId);
        LaunchUtil.startActivity(context, i);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_details;
    }

    @Override
    public void initData() {
        mDividerParams = LayoutUtil.getLinearParams(fitDp(KDividerWDp), fitDp(KDividerHDp));
        mModuleParams = LayoutUtil.getLinearParams(MATCH_PARENT, MATCH_PARENT);
        mModuleParams.weight = 1;

        mModuleCount = 0;

        //TODO:加载再初始化


        Intent i = getIntent();
        if (i != null) {
            mMeetId = i.getStringExtra(Extra.KData);
        }

        mLocationDialog = new LocationDialog(MeetingDetailsActivity.this);
    }

    @Override
    public void initNavBar(NavBar bar) {
        //TODO:右边图标的事件
        Util.addBackIcon(bar, "会议详情", this);
        bar.addViewRight(R.mipmap.nar_bar_ic_collection, v -> showToast("收藏"));
        bar.addViewRight(R.mipmap.nav_bar_ic_share, v -> {
            new ShareDialog(MeetingDetailsActivity.this).show();
            LogMgr.d(TAG, "分享");
        });
    }

    @Override
    public void findViews() {
        mIvPlay = findView(R.id.meeting_details_play_niv);
        mIvNumber = findView(R.id.meeting_niv_icon_number);
        mTvAward = findView(R.id.meeting_details_tv_award);
        mTvTitle = findView(R.id.meeting_details_tv_title);
        mTvSection = findView(R.id.meeting_details_tv_section);

        mTvGN = findView(R.id.meeting_tv_guest_name);
        mTvGP = findView(R.id.meeting_tv_guest_post);
        mTvGH = findView(R.id.meeting_tv_guest_hospital);
        mIvGP = findView(R.id.meeting_niv_guest_portrait);

        mLlModules = findView(R.id.meeting_detail_layout_modules);
        mTvSee = findView(R.id.meeting_detail_video_see);
    }

    @Override
    public void setViews() {
        mIvPlay.placeHolder(R.mipmap.ic_default_meeting_content_detail).load();
        mIvNumber.placeHolder(R.mipmap.ic_default_unit_num_large).load();
        mIvGP.placeHolder(R.mipmap.ic_default_meeting_guest).load();

        if (BuildConfig.TEST) {
            mMeetId = "17042512131640894904";
        }

        mLocationDialog.setOnAgainListener(v -> sign());

        refresh(RefreshWay.embed);
        exeNetworkReq(0, NetFactory.meetInfo(mMeetId));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), MeetDetail.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        Resp<MeetDetail> r = (Resp<MeetDetail>) result;
        if (r.isSucceed()) {
            refreshViews(r.getData());
        } else {
            showToast(r.getError());
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    /**
     * 更新界面数据
     *
     * @param info
     */
    private void refreshViews(MeetDetail info) {
        mTvAward.setText("本次会议奖励象数:" + info.getString(TMeetDetail.xsCredits) + ",还有260人能够获得奖励.");
        mTvGN.setText(info.getString(TMeetDetail.lecturer));
        mTvTitle.setText(info.getString(TMeetDetail.meetName));
        mTvSection.setText(info.getString(TMeetDetail.meetType));

        //添加包含的模块
        modules(info.getList(TMeetDetail.modules));
    }

    /**
     * 添加整个模块
     *
     * @param infoModules
     */
    private void modules(List<InfoModule> infoModules) {
        InfoModule infoModule;
        int type;
        for (int i = 0; i < infoModules.size(); i++) {
            infoModule = infoModules.get(i);
            type = infoModule.getInt(TInfoModule.functionId);
            switch (type) {
                case ModuleType.exam:
                    mExam = ResLoader.getDrawable(R.mipmap.meeting_ic_exam);
                    //TODO
                    addModule(mExam, "考试", v -> startActivity(ExamIntroActivity.class));
                    break;

                case ModuleType.que:
                    mQue = ResLoader.getDrawable(R.mipmap.meeting_ic_questionnaire);
                    addModule(mQue, "问卷", v -> startActivity(ExamIntroActivity.class));
                    break;

                case ModuleType.video:
                    mVideo = ResLoader.getDrawable(R.mipmap.meeting_ic_video);
                    addModule(mVideo, "视频", v -> startActivity(ExamIntroActivity.class));
                    break;

                case ModuleType.sign:
                    mSign = ResLoader.getDrawable(R.mipmap.meeting_ic_sign);
                    addModule(mSign, "签到", v -> sign());
                    break;
            }
        }

        //添加空白的View占位
        int emptyCount = KModuleCount - mModuleCount;
        if (emptyCount > 0) {
            for (int i = 0; i < emptyCount; i++) {
                View v  = new View(MeetingDetailsActivity.this);
                fit(v);
                mLlModules.addView(v, mModuleParams);
            }
        }
    }

    /**
     * 添加单个模块
     *
     * @param drawable
     * @param content
     */
    private void addModule(Drawable drawable, String content, OnClickListener l) {
        //添加模块间的分割线
        if (mModuleCount != 0) {
            mDivider = new View(MeetingDetailsActivity.this);
            mDivider.setBackgroundResource(R.drawable.divider);

            fit(mDivider);
            mLlModules.addView(mDivider, mDividerParams);
        }
        //添加具体的模块
        View v = inflate(R.layout.layout_meeting_detail_bot_item);
        ImageView iv = (ImageView) v.findViewById(R.id.meeting_detail_iv_module);
        TextView tv = (TextView) v.findViewById(R.id.meeting_detail_tv_module);
        iv.setImageDrawable(drawable);
        tv.setText(content);
        fit(v);
        mLlModules.addView(v, mModuleParams);
        v.setOnClickListener(l);
        mModuleCount++;
    }

    /**
     * 签到
     */
    private void sign() {
        if (checkPermission(0, location)) {
            //TODO:获取经纬度，看百度更新
            Location.inst();
            //SignActivity.nav(MeetingDetailsActivity.this, mMeetId);
        }

    }


    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                SignActivity.nav(MeetingDetailsActivity.this, mMeetId);
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {
                mLocationDialog.show();
            }
            break;
        }
    }

}
