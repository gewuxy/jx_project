package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.model.MapList;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TimeUtil;
import lib.ys.util.TimeUtil.TimeFormat;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.view.LayoutUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.LocationDialog;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.model.meet.Module;
import yy.doctor.model.meet.Module.TModule;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.Sign;
import yy.doctor.model.meet.Sign.TSign;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 会议详情界面
 * <p>
 * 日期 : 2017/4/21
 * 创建人 : guoxuan
 */

public class MeetingDetailsActivity extends BaseActivity {

    private static final int KModuleCount = 4;//模块数
    private static final int KMeetDetail = 0;//会议详情
    private static final int KSign = 1;//签到

    private final int KDividerWDp = 1;//分割线的宽
    private final int KDividerHDp = 16;//分割线的高
    private final int KExamResId = R.mipmap.meeting_ic_exam;//考试
    private final int KQueResId = R.mipmap.meeting_ic_questionnaire;//问卷
    private final int KVideoResId = R.mipmap.meeting_ic_video;//视频
    private final int KSignResId = R.mipmap.meeting_ic_sign;//签到

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
    private View mDivider;//分割线
    private LinearLayout mLlModules;//模块的容器
    private TextView mTvSee;//观看会议
    private int mModuleCount;//底部添加的模块数
    private LayoutParams mModuleParams;//模块的参数
    private LayoutParams mDividerParams;//分割线的参数

    private LocationDialog mLocationDialog;//定位框
    private OnLocationNotify mObserver;
    private MapList<Integer, String> mMapList;
    private TextView mTvDate;//时间
    private TextView mTvTime;//时长
    private String mLatitude;//经度
    private String mLongitude;//维度

    /**
     * functionId,模块功能ID
     */
    @IntDef({
            FunctionType.ppt,
            FunctionType.video,
            FunctionType.exam,
            FunctionType.que,
            FunctionType.sign,
    })
    private @interface FunctionType {
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
        mModuleParams = LayoutUtil.getLinearParams(MATCH_PARENT, MATCH_PARENT);
        mModuleParams.weight = 1;

        mModuleCount = 0;

        mMeetId = getIntent().getStringExtra(Extra.KData);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "会议详情", this);
        //TODO:右边图标的事件
        bar.addViewRight(R.mipmap.nar_bar_ic_collection, v -> showToast("收藏"));
        bar.addViewRight(R.mipmap.nav_bar_ic_share, v -> new ShareDialog(MeetingDetailsActivity.this).show());
    }

    @Override
    public void findViews() {
        mIvPlay = findView(R.id.meeting_detail_iv_play);
        mTvDate = findView(R.id.meeting_detail_tv_date);
        mTvTime = findView(R.id.meeting_detail_tv_time);
        mIvNumber = findView(R.id.meeting_detail_iv_number);
        mTvAward = findView(R.id.meeting_detail_tv_award);
        mTvTitle = findView(R.id.meeting_detail_tv_title);
        mTvSection = findView(R.id.meeting_detail_tv_section);

        //嘉宾相关
        mTvGN = findView(R.id.meeting_tv_guest_name);
        mTvGP = findView(R.id.meeting_tv_guest_post);
        mTvGH = findView(R.id.meeting_tv_guest_hospital);
        mIvGP = findView(R.id.meeting_niv_guest_portrait);

        //模块相关
        mLlModules = findView(R.id.meeting_detail_layout_modules);
        mTvSee = findView(R.id.meeting_detail_video_see);
    }

    @Override
    public void setViews() {
        mIvPlay.placeHolder(R.mipmap.ic_default_meeting_content_detail).load();
        mIvNumber.placeHolder(R.mipmap.ic_default_unit_num_large).load();
        mIvGP.placeHolder(R.mipmap.ic_default_meeting_guest).load();

        setOnClickListener(mTvSee);

        refresh(RefreshWay.embed);
        exeNetworkReq(KMeetDetail, NetFactory.meetInfo(mMeetId));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_detail_video_see:
                MeetingPPTActivity.nav(MeetingDetailsActivity.this, mMeetId, mMapList.getByKey(FunctionType.ppt));
                break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KMeetDetail) {
            return JsonParser.ev(r.getText(), MeetDetail.class);
        } else if (id == KSign) {
            return JsonParser.ev(r.getText(), Sign.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KMeetDetail) {
            setViewState(ViewState.normal);
            Result<MeetDetail> r = (Result<MeetDetail>) result;
            if (r.isSucceed()) {
                refreshViews(r.getData());
            } else {
                showToast(r.getError());
            }
        } else if (id == KSign) {
            //是否已签到
            stopRefresh();
            Result<Sign> r = (Result<Sign>) result;
            if (r.isSucceed()) {
                Sign signData = r.getData();
                boolean finished = signData.getBoolean(TSign.finished);
                if (finished) {//已签到直接显示结果
                    setViewState(ViewState.normal);
                    showToast("已签到");
                } else {//未签到跳转再请求签到
                    Intent i = new Intent(MeetingDetailsActivity.this, SignActivity.class)
                            .putExtra(Extra.KMeetId, mMeetId)
                            .putExtra(Extra.KModuleId, mMapList.getByKey(FunctionType.sign))
                            .putExtra(Extra.KData, signData.getString(TSign.id))
                            .putExtra(Extra.KLatitude, mLatitude)
                            .putExtra(Extra.KLongitude, mLongitude);
                    startActivity(i);
                }
            } else {
                showToast(r.getError());
            }
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
        long startTime = info.getLong(TMeetDetail.startTime);
        mTvDate.setText(TimeUtil.formatMilli(startTime, TimeFormat.from_y_to_m_24));
        mTvTime.setText("时长:" + timeParse(info.getLong(TMeetDetail.endTime) - startTime));

        mTvAward.setText("本次会议奖励象数:" + info.getString(TMeetDetail.xsCredits) + ",还有260人能够获得奖励.");
        mTvTitle.setText(info.getString(TMeetDetail.meetName));
        mTvSection.setText(info.getString(TMeetDetail.meetType));

        mTvGN.setText(info.getString(TMeetDetail.lecturer));

        //添加包含的模块
        modules(info.getList(TMeetDetail.modules));
    }

    /**
     * 添加整个模块
     *
     * @param modules
     */
    private void modules(List<Module> modules) {
        Module module;
        int type;
        mMapList = new MapList<>();
        for (int i = 0; i < modules.size(); i++) {
            module = modules.get(i);
            type = module.getInt(TModule.functionId);
            String moduleId = module.getString(TModule.id);
            mMapList.add(Integer.valueOf(type), moduleId);
            String moduleIdName = module.getString(TModule.moduleName);
            switch (type) {
                case FunctionType.exam:
                    addModule(KExamResId, moduleIdName, v ->
                            ExamIntroActivity.nav(MeetingDetailsActivity.this, mMeetId, mMapList.getByKey(FunctionType.exam)));
                    break;

                case FunctionType.que:
                    addModule(KQueResId, moduleIdName, null);
                    break;

                case FunctionType.video:
                    addModule(KVideoResId, moduleIdName, v ->
                            VideoCategoryActivity.nav(MeetingDetailsActivity.this, mMeetId, mMapList.getByKey(FunctionType.video)));
                    break;

                case FunctionType.sign:
                    addModule(KSignResId, moduleIdName, v -> {
                        if (checkPermission(0, Permission.location, Permission.phone, Permission.storage)) {
                            sign();
                        }
                    });
                    break;
            }
        }

        //添加空白的View占位
        int emptyCount = KModuleCount - mModuleCount;
        if (emptyCount > 0) {
            for (int i = 0; i < emptyCount; i++) {
                View v = new View(MeetingDetailsActivity.this);
                fit(v);
                mLlModules.addView(v, mModuleParams);
            }
        }
    }

    /**
     * 添加单个模块
     *
     * @param resId
     * @param content
     */
    private void addModule(@DrawableRes int resId, String content, OnClickListener l) {
        //添加模块间的分割线
        if (mModuleCount != 0) {
            mDivider = new View(MeetingDetailsActivity.this);
            mDivider.setBackgroundResource(R.drawable.divider);

            mDividerParams = LayoutUtil.getLinearParams(fitDp(KDividerWDp), fitDp(KDividerHDp));

            fit(mDivider);
            mLlModules.addView(mDivider, mDividerParams);
        }
        //添加具体的模块
        View v = inflate(R.layout.layout_meeting_detail_bot_item);
        ImageView iv = (ImageView) v.findViewById(R.id.meeting_detail_iv_module);
        TextView tv = (TextView) v.findViewById(R.id.meeting_detail_tv_module);
        iv.setImageResource(resId);
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
        refresh(RefreshWay.dialog);
        Location.inst().start();
        mObserver = (isSuccess, gps) -> {
            if (isSuccess) {
                //定位成功
                mLatitude = gps.getString(TGps.latitude);
                mLongitude = gps.getString(TGps.longitude);
                LogMgr.d("Gps", mLatitude);
                LogMgr.d("Gps", mLongitude);
                exeNetworkReq(KSign, NetFactory.toSign(mMeetId, mMapList.getByKey(FunctionType.sign)));
            } else {
                runOnUIThread(() -> stopRefresh());
                //定位失败
                LogMgr.d("Gps", "失败");
                if (mLocationDialog == null) {
                    initDialog();
                }
                mLocationDialog.show();
            }
            LocationNotifier.inst().remove(mObserver);
            Location.inst().stop();
            Location.inst().onDestroy();
        };
        LocationNotifier.inst().add(mObserver);

    }

    /**
     * 初始化Dialog
     */
    private void initDialog() {
        mLocationDialog = new LocationDialog(MeetingDetailsActivity.this);
        mLocationDialog.setLocationListener(v -> {
            if (checkPermission(0, Permission.location, Permission.phone, Permission.storage)) {
                sign();
            }
        });
    }

    /**
     * 按X.X小时的格式格式化毫秒值
     *
     * @param time
     * @return
     */
    public String timeParse(long time) {
        StringBuffer parse = new StringBuffer();
        float f = time / 3600000.0f;
        //超过一天
        if (f > 24) {
            parse.append((int) f / 24).append("天");
            f /= 24;
        }
        BigDecimal b = new BigDecimal(f);
        //保留1位小数,四舍五入
        float result = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        return parse
                .append(result)
                .append("小时")
                .toString();
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                sign();
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {
                if (mLocationDialog == null) {
                    initDialog();
                }
                mLocationDialog.show();
            }
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationDialog != null) {
            if (mLocationDialog.isShowing()) {
                mLocationDialog.dismiss();
            }
            mLocationDialog = null;
        }
    }
}
