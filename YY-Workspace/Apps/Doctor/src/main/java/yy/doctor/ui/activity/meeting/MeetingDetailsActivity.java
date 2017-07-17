package yy.doctor.ui.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.bd.location.Gps;
import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.model.MapList;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TimeUtil;
import lib.ys.util.TimeUtil.TimeFormat;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.HintDialogMain;
import yy.doctor.dialog.LocationDialog;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.home.RecUnitNum.Attention;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.MeetDetail.CollectType;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.model.meet.Sign;
import yy.doctor.model.meet.Sign.TSign;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.meet.module.BaseFunc;
import yy.doctor.model.meet.module.BaseFunc.OnFuncListener;
import yy.doctor.model.meet.module.CourseFunc;
import yy.doctor.model.meet.module.ExamFunc;
import yy.doctor.model.meet.module.Module.ModuleType;
import yy.doctor.model.meet.module.QueFunc;
import yy.doctor.model.meet.module.SignFunc;
import yy.doctor.model.meet.module.VideoFunc;
import yy.doctor.model.unitnum.FileData;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.network.UrlUtil;
import yy.doctor.network.UrlUtil.UrlMeet;
import yy.doctor.serv.CommonServ;
import yy.doctor.serv.CommonServ.ReqType;
import yy.doctor.ui.activity.me.unitnum.FileDataActivity;
import yy.doctor.ui.activity.me.unitnum.UnitNumDetailActivity.AttentionUnitNum;
import yy.doctor.util.Time;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;
import yy.doctor.view.meet.ModuleLayout;
import yy.doctor.view.meet.ModuleLayout.EpnType;

/**
 * 会议详情界面
 * <p>
 * 日期 : 2017/4/21
 * 创建人 : guoxuan
 */
public class MeetingDetailsActivity extends BaseActivity implements OnFuncListener {

    private static final int KIdMeetDetail = 0; // 会议详情
    private static final int KIdCollection = 1; // 收藏
    private static final int KIdAttention = 2; // 关注

    private static final int KIdExam = 3; // 考试
    private static final int KIdQue = 4; // 问卷
    private static final int KIdVideo = 5; // 视频
    private static final int KIdSign = 6; // 签到
    private static final int KIdCourse = 7; // PPT

    private ImageView mIvCollection; // navBar的收藏

    private NetworkImageView mIvPlay; // 缩略图
    private View mIvPlayCourse; // 右下角的图标
    private TextView mTvDate; // 会议开始时间
    private TextView mTvDuration; // 会议时长

    // 会议介绍
    private TextView mTvTitle; // 会议名称
    private TextView mTvEpn; // 象数相关
    private TextView mTvSection; // 会议科室

    // 单位号介绍
    private NetworkImageView mIvNumber; // 单位号图标
    private TextView mTvUnitNum; // 单位号

    //资料
    private View mLayoutData; // 查看资料
    private TextView mTvFileNum; // 文件个数
    private ImageView mIvFileArrow; // 箭头
    private LinearLayout mLayoutFile; // 文件布局
    private View mDividerFile; // 分割线(大)
    private View mDivider; // 分割线

    // 主讲者
    private NetworkImageView mIvGP; // 头像
    private TextView mTvGN; // 名字
    private TextView mTvGP; // 职位
    private TextView mTvGH; // 医院

    private TextView mTvIntro; // 会议简介

    private MapList<Integer, BaseFunc> mFuncs;
    private ModuleLayout mModuleLayout; // 模块

    private MeetDetail mMeetDetail; // 会议详情信息
    private boolean mNeedPay; // 是否需要支付
    private int mCostEpn; // 象数
    private String mMeetId; // 会议Id
    private String mMeetName; //  会议名字
    private Gps mGps; // 定位信息
    private OnLocationNotify mObserver; // 定位通知

    private long mStartModuleTime; // 模块开始时间
    private long mMeetTime; // 统一用通知不用result

    private MapList<Integer, String> mModules; // 记录模块ID

    private LocationDialog mLocationDialog; // 定位失败
    private HintDialogMain mDialogPayEpn; // 支付象数
    private HintDialogMain mDialogEpnNotEnough; // 象数不足
    private HintDialogMain mDialogAttention; // 关注
    private ShareDialog mShareDialog; // 分享

    public static void nav(Context context, String meetId, String name) {
        Intent i = new Intent(context, MeetingDetailsActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KName, name);
        LaunchUtil.startActivity(context, i);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_detail;
    }

    @Override
    public void initData() {
        mMeetTime = 0;
        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mMeetName = getIntent().getStringExtra(Extra.KName); // 没有请求到数据时也可以分享会议
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.meeting_detail, this);
        // 收藏
        ViewGroup group = bar.addViewRight(R.drawable.collection_selector, v -> {
            boolean storedState = true; // 默认没有关注, 故点击时关注(MeetDetail还没获取到数据时)
            if (mMeetDetail != null) {
                // 状态取反
                storedState = !mMeetDetail.getBoolean(TMeetDetail.stored);
                mMeetDetail.put(TMeetDetail.stored, storedState);
            }
            mIvCollection.setSelected(storedState);
            showToast(storedState ? R.string.collect : R.string.cancel_collect);
            exeNetworkReq(KIdCollection, NetFactory.collectMeeting(mMeetId, storedState ? CollectType.collect : CollectType.cancel));
            if (storedState) {
                // no thing
            } else {
                // 取消收藏(通知会议收藏列表去除会议)
                notify(NotifyType.cancel_collection_meeting, mMeetId);
            }
        });
        mIvCollection = Util.getBarView(group, ImageView.class);
        // 分享
        bar.addViewRight(R.mipmap.nav_bar_ic_share, v -> {
            mShareDialog = new ShareDialog(MeetingDetailsActivity.this, UrlUtil.getBaseUrl() + UrlMeet.KMeetShare + mMeetId, mMeetName);
            mShareDialog.show();
        });
    }

    @Override
    public void findViews() {
        mIvPlay = findView(R.id.meeting_detail_iv_play);
        mIvPlayCourse = findView(R.id.meeting_detail_iv_play_course);
        mTvDate = findView(R.id.meeting_detail_tv_date);
        mTvDuration = findView(R.id.meeting_detail_tv_time);

        // 会议相关
        mTvTitle = findView(R.id.meeting_detail_tv_title);
        mTvEpn = findView(R.id.meeting_detail_tv_award);
        mTvSection = findView(R.id.meeting_detail_tv_section);

        // 单位号相关
        mIvNumber = findView(R.id.meeting_detail_iv_number);
        mTvUnitNum = findView(R.id.meeting_detail_tv_unit_num);

        // 文件
        mLayoutData = findView(R.id.meeting_detail_layout_data);
        mLayoutFile = findView(R.id.meeting_detail_layout_file);
        mTvFileNum = findView(R.id.meeting_detail_tv_data);
        mIvFileArrow = findView(R.id.meeting_detail_iv_data);
        mDividerFile = findView(R.id.meeting_detail_view_divider);
        mDivider = findView(R.id.meeting_detail_divider);

        // 主讲者相关
        mTvGN = findView(R.id.meeting_tv_guest_name);
        mTvGP = findView(R.id.meeting_tv_guest_post);
        mTvGH = findView(R.id.meeting_tv_guest_hospital);
        mIvGP = findView(R.id.meeting_iv_guest_portrait);

        mTvIntro = findView(R.id.meeting_detail_tv_intro);

        mModuleLayout = findView(R.id.meeting_detail_modules);
    }

    @Override
    public void setViews() {
        refresh(RefreshWay.embed);
        exeNetworkReq(KIdMeetDetail, NetFactory.meetInfo(mMeetId));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KIdMeetDetail) {
            return JsonParser.ev(r.getText(), MeetDetail.class);
        } else if (id == KIdSign) {
            return JsonParser.ev(r.getText(), Sign.class);
        } else if (id == KIdQue) {
            return JsonParser.ev(r.getText(), Intro.class);
        } else if (id == KIdCourse) {
            return JsonParser.ev(r.getText(), PPT.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        switch (id) {
            case KIdMeetDetail: {
                Result<MeetDetail> r = (Result<MeetDetail>) result;
                if (r.isSucceed()) {
                    mMeetDetail = r.getData();
                    refreshViews(mMeetDetail);
                    setViewState(ViewState.normal);
                } else {
                    onNetworkError(id, new NetError(id, r.getError()));
                }
            }
            break;
            case KIdExam: {
                stopRefresh();
                Result r = (Result) result;
                if (r.isSucceed()) {
                    notifyEpn();
                    mStartModuleTime = System.currentTimeMillis();
                    ExamIntroActivity.nav(MeetingDetailsActivity.this, mMeetId, mModules.getByKey(ModuleType.exam),
                            mMeetDetail.getString(TMeetDetail.organizer));
                } else {
                    showToast(r.getError());
                }
            }
            break;
            case KIdQue: {
                stopRefresh();
                Result r = (Result) result;
                if (r.isSucceed()) {
                    notifyEpn();
                    mStartModuleTime = System.currentTimeMillis();
                    QueTopicActivity.nav(MeetingDetailsActivity.this, mMeetId, mModules.getByKey(ModuleType.que));
                } else {
                    showToast(r.getError());
                }
            }
            break;
            case KIdSign: {
                stopRefresh();
                Result<Sign> r = (Result<Sign>) result;
                if (r.isSucceed()) {
                    notifyEpn();
                    Sign signData = r.getData();
                    if (signData.getBoolean(TSign.finished)) {
                        // 提示已签到
                        showToast(R.string.signed);
                    } else {
                        // 未签到去签到
                        mStartModuleTime = System.currentTimeMillis();
                        SignActivity.nav(MeetingDetailsActivity.this,
                                mMeetId,
                                mModules.getByKey(ModuleType.sign),
                                signData.getString(TSign.id),
                                mGps.getString(TGps.latitude),
                                mGps.getString(TGps.longitude));
                    }
                } else {
                    showToast(r.getError());
                }
            }
            break;
            case KIdCourse: {
                stopRefresh();
                Result<PPT> r = (Result<PPT>) result;
                if (r.isSucceed()) {
                    notifyEpn();
                    PPT ppt = r.getData();
                    CourseInfo courseInfo = ppt.getEv(TPPT.course);
                    if (courseInfo != null) {
                        mStartModuleTime = System.currentTimeMillis();
                        MeetingCourseActivity.nav(MeetingDetailsActivity.this, mMeetId, mModules.getByKey(ModuleType.ppt));
                    } else {
                        showToast("没有PPT");
                    }
                } else {
                    showToast(r.getError());
                }
            }
            break;
            case KIdAttention: {
                stopRefresh();
                Result r = (Result) result;
                if (r.isSucceed()) {
                    showToast("关注成功");
                    mMeetDetail.put(TMeetDetail.attention, true);
                    notify(NotifyType.unit_num_attention_change,
                            new AttentionUnitNum(mMeetDetail.getInt(TMeetDetail.pubUserId), Attention.yes));
                } else {
                    showToast("关注失败");
                }
            }
            break;
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

        if (id == KIdMeetDetail) {
            setViewState(ViewState.error);
        }
    }

    /**
     * 更新界面数据
     *
     * @param detail
     */
    private void refreshViews(MeetDetail detail) {
        mIvCollection.setSelected(detail.getBoolean(TMeetDetail.stored));

        mIvPlay.placeHolder(R.mipmap.ic_default_meeting_content_detail)
                .url(detail.getString(TMeetDetail.coverUrl))
                .load();

        long startTime = detail.getLong(TMeetDetail.startTime); // 开始时间
        mTvDate.setText(TimeUtil.formatMilli(startTime, TimeFormat.from_y_to_m_24));
        mTvDuration.setText("时长:" + Time.milliFormat(detail.getLong(TMeetDetail.endTime) - startTime));

        // 会议
        mTvTitle.setText(detail.getString(TMeetDetail.meetName));
        @EpnType int epnType = detail.getInt(TMeetDetail.eduCredits); // 支付 / 奖励
        mNeedPay = epnType == EpnType.need; // 是否需要支付象数
        mCostEpn = detail.getInt(TMeetDetail.xsCredits); // 需要(支付 / 奖励)的象数
        if (mCostEpn == 0) {
            // 不奖励也不支付算免费, 隐藏
            goneView(mTvEpn);
        } else {
            if (mNeedPay) {
                mTvEpn.setText(String.format(getString(R.string.meeting_epn_pay), mCostEpn));
            } else {
                mTvEpn.setText(String.format(getString(R.string.meeting_epn_award),
                        mCostEpn, detail.getInt(TMeetDetail.remainAward, 0)));
            }
        }
        mTvSection.setText(detail.getString(TMeetDetail.meetType));

        // 单位号
        mIvNumber.placeHolder(R.mipmap.ic_default_unit_num_large)
                .url(detail.getString(TMeetDetail.headimg))
                .renderer(new CircleRenderer())
                .load();
        mTvUnitNum.setText(detail.getString(TMeetDetail.organizer));

        // 资料
        int fileNum = detail.getInt(TMeetDetail.materialCount);
        if (fileNum > 3) {
            showView(mIvFileArrow);
            mTvFileNum.setText(String.format(getString(R.string.meeting_file_more), fileNum));
            mLayoutData.setOnClickListener(v -> FileDataActivity.nav(MeetingDetailsActivity.this, mMeetId, Extra.KMeetingType));
        }
        List<FileData> materials = detail.getList(TMeetDetail.materials);
        if (materials == null || materials.size() == 0) {
            goneView(mDivider);
            goneView(mLayoutData);
            goneView(mDividerFile);
        } else {
            UISetter.setFileData(mLayoutFile, materials, detail.getInt(TMeetDetail.pubUserId));
        }

        // 主讲者
        mTvGN.setText(detail.getString(TMeetDetail.lecturer));
        mIvGP.placeHolder(R.mipmap.ic_default_meeting_guest)
                .url(detail.getString(TMeetDetail.lecturerHead))
                .load();
        // 职责和医院没有的话就隐藏
        UISetter.viewVisibility(detail.getString(TMeetDetail.lecturerTitle), mTvGP); // 职责
        UISetter.viewVisibility(detail.getString(TMeetDetail.lecturerHos), mTvGH); // 医院

        mTvIntro.setText(Html.fromHtml(detail.getString(TMeetDetail.introduction)));

        mFuncs = new MapList<>();

        BaseFunc func = new ExamFunc(this, detail, this);
        mFuncs.add(Integer.valueOf(func.getType()), func);

        func = new QueFunc(this, detail, this);
        mFuncs.add(Integer.valueOf(func.getType()), func);

        func = new SignFunc(this, detail, this);
        mFuncs.add(Integer.valueOf(func.getType()), func);

        func = new VideoFunc(this, detail, this);
        mFuncs.add(Integer.valueOf(func.getType()), func);

        func = new CourseFunc(this, detail, this);
        mFuncs.add(Integer.valueOf(func.getType()), func);

        // 模块处理
        mModuleLayout
                .setFuncs(mFuncs)
                .setModules(detail)
                .intoCourse(mIvPlay, mIvPlayCourse)
                .load();
    }

//    @Override
//    public void onModuleClick(int type, MapList<Integer, String> modules) {
//        if (mModules == null) {
//            mModules = modules;
//        }
//        String moduleId = mModules.getByKey(type);
//        switch (type) {
//            case ModuleType.exam: {
//                refresh(RefreshWay.dialog);
//                exeNetworkReq(KIdExam, NetFactory.toExam(mMeetId, moduleId));
//            }
//            break;
//            case ModuleType.que: {
//                refresh(RefreshWay.dialog);
//                exeNetworkReq(KIdQue, NetFactory.toSurvey(mMeetId, moduleId));
//            }
//            break;
//            case ModuleType.video: {
//                mStartModuleTime = System.currentTimeMillis();
//                Submit submit = new Submit()
//                        .put(TSubmit.meetId, mMeetId)
//                        .put(TSubmit.moduleId, moduleId);
//                VideoCategoryActivity.nav(MeetingDetailsActivity.this, submit, null);
//            }
//            break;
//            case ModuleType.sign: {
//                getSignInfo();
//            }
//            break;
//            case ModuleType.ppt: {
//                refresh(RefreshWay.dialog);
//                exeNetworkReq(KIdCourse, NetFactory.toPPT(mMeetId, moduleId));
//            }
//            break;
//        }
//    }

//    @Override
//    public void toShowDialog(int type, View v) {
//        switch (type) {
//            case DialogType.attention: {
//                // 提示关注
//                mDialogAttention = new HintDialogMain(MeetingDetailsActivity.this);
//                mDialogAttention.setHint("请先关注会议");
//                mDialogAttention.addButton("确认关注", v1 -> {
//                    mDialogAttention.dismiss();
//                    exeNetworkReq(KIdAttention, NetFactory.attention(mMeetDetail.getInt(TMeetDetail.pubUserId), 1));
//                });
//                mDialogAttention.addButton(R.string.cancel, R.color.text_666, v1 -> mDialogAttention.dismiss());
//                mDialogAttention.show();
//            }
//            break;
//            case DialogType.recharge: {
//                // 提示充值
//                mDialogEpnNotEnough = new HintDialogMain(MeetingDetailsActivity.this);
//                mDialogEpnNotEnough.setHint("您的剩余象数不足所需象数值, 请充值象数后继续");
//                mDialogEpnNotEnough.addButton("充值象数", v1 -> {
//                    mDialogEpnNotEnough.dismiss();
//                    startActivity(EpnRechargeActivity.class);
//                });
//                mDialogEpnNotEnough.addButton(R.string.cancel, R.color.text_666, v1 -> mDialogEpnNotEnough.dismiss());
//                mDialogEpnNotEnough.show();
//            }
//            break;
//            case DialogType.pay: {
//                // 提示支付
//                mDialogPayEpn = new HintDialogMain(MeetingDetailsActivity.this);
//                mDialogPayEpn.setHint(String.format(getString(R.string.need_pay), mCostEpn));
//                mDialogPayEpn.addButton("确认支付", v1 -> {
//                    mDialogPayEpn.dismiss();
//                    mModuleLayout.toClickModule(v);
//                });
//                mDialogPayEpn.addButton(R.string.cancel, R.color.text_666, v1 -> mDialogPayEpn.dismiss());
//                mDialogPayEpn.show();
//            }
//            break;
//        }
//    }

    /**
     * 签到
     */
    private void getSignInfo() {
        refresh(RefreshWay.dialog);
        mObserver = (isSuccess, gps) -> {
            if (isSuccess) {
                //定位成功
                mGps = gps;
                exeNetworkReq(KIdSign, NetFactory.toSign(mMeetId, mModules.getByKey(ModuleType.sign)));
            } else {
                //定位失败
                runOnUIThread(() -> {
                    stopRefresh();
                    showLocationDialog();
                });
            }
            LocationNotifier.inst().remove(mObserver);
            Location.inst().onDestroy();
        };
        Location.inst().start();
        LocationNotifier.inst().add(mObserver);
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                getSignInfo();
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {
                showLocationDialog();
            }
            break;
        }
    }

    /**
     * 初始化Dialog
     */
    private void showLocationDialog() {
        mLocationDialog = new LocationDialog(MeetingDetailsActivity.this);
        mLocationDialog.setLocationListener(v -> {
            if (checkPermission(0, Permission.location, Permission.phone, Permission.storage)) {
                getSignInfo();
            }
        });
        mLocationDialog.show();
    }

    /**
     * 改变象数        // FIXME: 后台返回
     */
    private void notifyEpn() {
        if (mCostEpn == 0 || mMeetDetail.getBoolean(TMeetDetail.attended)) {
            return;
        }

        int remainEpn = Profile.inst().getInt(TProfile.credits); // 剩余象数
        if (mNeedPay) {
            Profile.inst().put(TProfile.credits, remainEpn - mCostEpn);
        } else {
            if (mMeetDetail.getInt(TMeetDetail.remainAward) > 0) {
                // 奖励人数大于0奖励象数的
                Profile.inst().put(TProfile.credits, remainEpn + mCostEpn);
            }
        }
        Profile.inst().saveToSp();
        notify(NotifyType.profile_change);
        mMeetDetail.put(TMeetDetail.attended, true); // 参加过会议
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 防止Dialog泄露
        if (mLocationDialog != null) {
            mLocationDialog.dismiss();
        }
        if (mDialogPayEpn != null) {
            mDialogPayEpn.dismiss();
        }
        if (mShareDialog != null) {
            mShareDialog.dismiss();
        }
        if (mDialogAttention != null) {
            mDialogAttention.dismiss();
        }
        // 开启服务提交会议学习时间
        if (mMeetTime > 0) {
            Intent intent = new Intent(this, CommonServ.class)
                    .putExtra(Extra.KType, ReqType.meet)
                    .putExtra(Extra.KMeetId, mMeetId)
                    .putExtra(Extra.KData, mMeetTime / TimeUnit.SECONDS.toMillis(1));
            startService(intent);
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.study) {
            mMeetTime += System.currentTimeMillis() - mStartModuleTime;
            YSLog.d(TAG, "onNotify:" + mMeetTime);
        }
    }

    @Override
    public void onFuncLoading(int type, String moduleId) {
        refresh(RefreshWay.dialog);
    }

    @Override
    public void onFuncNormal(int type, String moduleId) {
        stopRefresh();
        notifyEpn();
    }
}
