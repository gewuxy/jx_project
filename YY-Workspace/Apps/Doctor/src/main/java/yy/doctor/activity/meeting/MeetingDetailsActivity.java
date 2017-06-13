package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.fitter.LayoutFitter;
import lib.ys.model.MapList;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.TimeUtil;
import lib.ys.util.TimeUtil.TimeFormat;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.HintDialogMain;
import yy.doctor.dialog.LocationDialog;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.Module;
import yy.doctor.model.meet.Module.TModule;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.model.meet.Sign;
import yy.doctor.model.meet.Sign.TSign;
import yy.doctor.model.meet.exam.Intro;
import yy.doctor.model.unitnum.UnitNumDetailData;
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

    private static final int KIdMeetDetail = 0; // 会议详情
    private static final int KIdCollection = 1; // 收藏
    private static final int KIdAttention = 7; // 关注

    private static final int KIdExam = 2; // 考试
    private static final int KIdQue = 3; // 问卷
    private static final int KIdVideo = 4; // 视频
    private static final int KIdSign = 5; // 签到
    private static final int KIdCourse = 6; // PPT

    private static final int KTextColor = ResLoader.getColor(R.color.text_333);

    private final int KExamResId = R.mipmap.meeting_ic_exam_have; // 考试
    private final int KQueResId = R.mipmap.meeting_ic_que_have; // 问卷
    private final int KVideoResId = R.mipmap.meeting_ic_video_have; // 视频
    private final int KSignResId = R.mipmap.meeting_ic_sign_have; // 签到

    private String mHost; // 主办方
    private String mMeetId; // 会议Id
    private TextView mTvAward;
    private TextView mTvTitle; // 会议名称
    private TextView mTvSection; // 会议科室
    private TextView mTvUnitNum; // 单位号

    private NetworkImageView mIvPlay; // 播放图片
    private NetworkImageView mIvNumber; // 公众号图标
    private NetworkImageView mIvGP; // 头像

    // 主讲者
    private TextView mTvGN; // 名字
    private TextView mTvGP; // 职位
    private TextView mTvGH; // 医院

    //资料
    private View mLayoutData; // 查看资料
    private TextView mTvFileNum; // 文件个数
    private ImageView mIvFileArrow; // 箭头
    private LinearLayout mLayoutFile; // 文件布局
    private View mDividerFile; // 分割线
    private View mDivider;

    private LocationDialog mLocationDialog; // 定位框
    private OnLocationNotify mObserver; // 定位通知
    private MapList<Integer, String> mMapList;
    private TextView mTvDate; // 时间
    private TextView mTvDuration; // 时长
    private String mLatitude; // 经度
    private String mLongitude; // 维度
    private TextView mTvIntro; // 会议简介
    private ImageView mIvCollection; // navBar的收藏

    @CollectType
    private int mCollectType; // 收藏的标志

    private boolean mAttention; // 是否关注了单位号
    private boolean mEpnType; // 需要还是奖励
    private boolean mAttendAble; // 能否参加会议
    private String mReason; // 不能参加会议的原因
    private boolean mNoPPT;

    // FIXME: View TextView ImageView -> 自定义view

    // 底部按钮
    private View mLayoutExam; // 考试模块
    private TextView mTvExam;
    private ImageView mIvExam;

    private View mLayoutQue; // 问卷模块
    private TextView mTvQue;
    private ImageView mIvQue;

    private View mLayoutVideo; // 视频模块
    private TextView mTvVideo;
    private ImageView mIvVideo;

    private View mLayoutSign; // 签到模块
    private TextView mTvSign;
    private ImageView mIvSign;

    private TextView mTvSee; // 会议模块
    private List<UnitNumDetailData> mMaterials;
    private int mEpn; // 象数
    private HintDialogMain mEpnDialog; // 要支付象数的对话框
    private HintDialogMain mAttentionDialog; // 关注的对话框
    private int mNumberId; // 公众号id

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

    @IntDef({
            CollectType.no,
            CollectType.yes,
    })
    private @interface CollectType {
        int no = 0; // 收藏
        int yes = 1; // 没有收藏
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
        mNoPPT = false;
        mMeetId = getIntent().getStringExtra(Extra.KData);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "会议详情", this);
        ViewGroup layout = (ViewGroup) bar.addViewRight(R.drawable.meeting_ppt_collection_selector, v -> {
            switch (mCollectType) {
                case CollectType.yes:
                    mIvCollection.setSelected(false);
                    mCollectType = CollectType.no;
                    showToast("取消收藏成功");
                    break;
                case CollectType.no:
                    mIvCollection.setSelected(true);
                    mCollectType = CollectType.yes;
                    showToast("收藏成功");
                    break;
            }
            exeNetworkReq(KIdCollection, NetFactory.collectMeeting(mMeetId, mCollectType));
        });
        if (mIvCollection == null) {
            getCollection(layout);
        }
        bar.addViewRight(R.mipmap.nav_bar_ic_share, v -> new ShareDialog(MeetingDetailsActivity.this).show());
    }

    @Override
    public void findViews() {
        mIvPlay = findView(R.id.meeting_detail_iv_play);
        mTvDate = findView(R.id.meeting_detail_tv_date);
        mTvDuration = findView(R.id.meeting_detail_tv_time);
        mIvNumber = findView(R.id.meeting_detail_iv_number);
        mTvAward = findView(R.id.meeting_detail_tv_award);
        mTvTitle = findView(R.id.meeting_detail_tv_title);
        mTvSection = findView(R.id.meeting_detail_tv_section);
        mTvUnitNum = findView(R.id.meeting_detail_tv_unit_num);

        // 嘉宾相关
        mTvGN = findView(R.id.meeting_tv_guest_name);
        mTvGP = findView(R.id.meeting_tv_guest_post);
        mTvGH = findView(R.id.meeting_tv_guest_hospital);
        mIvGP = findView(R.id.meeting_iv_guest_portrait);

        mTvIntro = findView(R.id.meeting_detail_tv_intro);

        //文件
        mLayoutData = findView(R.id.meeting_detail_layout_data);
        mLayoutFile = findView(R.id.meeting_detail_layout_file);
        mTvFileNum = findView(R.id.meeting_detail_tv_data);
        mIvFileArrow = findView(R.id.meeting_detail_iv_data);
        mDividerFile = findView(R.id.meeting_detail_view_divider);
        mDivider = findView(R.id.meeting_detail_divider);

        // 模块
        mLayoutExam = findView(R.id.meeting_detail_layout_exam);
        mTvExam = findView(R.id.meeting_detail_tv_exam);
        mIvExam = findView(R.id.meeting_detail_iv_exam);

        mLayoutQue = findView(R.id.meeting_detail_layout_que);
        mTvQue = findView(R.id.meeting_detail_tv_que);
        mIvQue = findView(R.id.meeting_detail_iv_que);

        mLayoutVideo = findView(R.id.meeting_detail_layout_video);
        mTvVideo = findView(R.id.meeting_detail_tv_video);
        mIvVideo = findView(R.id.meeting_detail_iv_video);

        mLayoutSign = findView(R.id.meeting_detail_layout_sign);
        mTvSign = findView(R.id.meeting_detail_tv_sign);
        mIvSign = findView(R.id.meeting_detail_iv_sign);

        mTvSee = findView(R.id.meeting_detail_video_see);
    }

    @Override
    public void setViews() {
        refresh(RefreshWay.embed);
        exeNetworkReq(KIdMeetDetail, NetFactory.meetInfo(mMeetId));
    }

    @Override
    public void onClick(View v) {
        if (mAttention) { // 关注
            if (mAttendAble) { // 参加
                if (!mEpnType ) {
                    if (mEpn > 0) {
                        mEpnDialog = new HintDialogMain(MeetingDetailsActivity.this);
                        mEpnDialog.setHint("本会议需要支付" + mEpn + "象数");
                        mEpnDialog.addButton("确认支付", "#0682e6", v1 -> {
                            mEpnDialog.dismiss();
                            toModule(v);
                        });
                        mEpnDialog.addButton("取消", "#666666", v1 -> mEpnDialog.dismiss());
                        mEpnDialog.show();
                    } else {
                        toModule(v);
                    }
                } else {
                    toModule(v);
                }
            } else {
                showToast(mReason);
            }
        } else {
            if (mAttentionDialog == null) {
                mAttentionDialog = new HintDialogMain(MeetingDetailsActivity.this);
                mAttentionDialog.setHint("请先关注会议");
                mAttentionDialog.addButton("确认关注", "#0682e6", v1 -> exeNetworkReq(KIdAttention, NetFactory.attention(mNumberId, 1)));
                mAttentionDialog.addButton("取消", "#666666", v1 -> mAttentionDialog.dismiss());
            }
            mAttentionDialog.show();
        }
    }

    /**
     * 模块的功能
     * @param v
     */
    private void toModule(View v) {
        switch (v.getId()) {
            case R.id.meeting_detail_iv_play: {
                if (!mNoPPT) {
                    showToast("会议没有设置PPT");
                }
            }
            case R.id.meeting_detail_video_see: {
                getCourseInfo();
            }
            break;
            case R.id.meeting_detail_layout_exam: {
                getExamInfo();
            }
            break;
            case R.id.meeting_detail_layout_que: {
                getQueInfo();
            }
            break;
            case R.id.meeting_detail_layout_video: {
                getVideoInfo();
            }
            break;
            case R.id.meeting_detail_layout_sign: {
                if (checkPermission(0, Permission.location, Permission.phone, Permission.storage)) {
                    getSignInfo();
                }
            }
            break;
        }
    }

    // FIXME: 2017/6/12 暂时只有PPT和问卷进入要先请求

    /**
     * PPT
     */
    private void getCourseInfo() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(KIdCourse, NetFactory.toPPT(mMeetId, mMapList.getByKey(FunctionType.ppt)));
    }

    /**
     * 考试
     */
    private void getExamInfo() {
        ExamIntroActivity.nav(MeetingDetailsActivity.this, mMeetId, mMapList.getByKey(FunctionType.exam), mHost);
    }

    /**
     * 问卷
     */
    private void getQueInfo() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(KIdQue, NetFactory.toSurvey(mMeetId, mMapList.getByKey(FunctionType.que)));
    }

    /**
     * 视频
     */
    private void getVideoInfo() {
        VideoCategoryActivity.nav(MeetingDetailsActivity.this, mMeetId, mMapList.getByKey(FunctionType.video));
    }

    /**
     * 签到
     */
    private void getSignInfo() {
        refresh(RefreshWay.dialog);
        Location.inst().start();
        mObserver = (isSuccess, gps) -> {
            if (isSuccess) {
                //定位成功
                mLatitude = gps.getString(TGps.latitude);
                mLongitude = gps.getString(TGps.longitude);
                LogMgr.d(TAG, "Gps-Latitude:" + mLatitude);
                LogMgr.d(TAG, "Gps-Longitude:" + mLongitude);
                exeNetworkReq(KIdSign, NetFactory.toSign(mMeetId, mMapList.getByKey(FunctionType.sign)));
            } else {
                runOnUIThread(() -> stopRefresh());
                //定位失败
                LogMgr.d(TAG, "Gps:失败");
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
                    refreshViews(r.getData());
                    setViewState(ViewState.normal);
                } else {
                    onNetworkError(id, new NetError(id, r.getError()));
                }
            }
            break;

            case KIdExam: {
            }
            break;

            case KIdQue: {
                stopRefresh();
                Result r = (Result) result;
                if (r.isSucceed()) {
                    QueTopicActivity.nav(MeetingDetailsActivity.this, mMeetId, mMapList.getByKey(FunctionType.que));
                } else {
                    showToast(r.getError());
                }
            }
            break;

            case KIdVideo: {
            }
            break;

            case KIdSign: {
                // 判断是否已签到
                stopRefresh();
                Result<Sign> r = (Result<Sign>) result;
                if (r.isSucceed()) {
                    Sign signData = r.getData();
                    boolean finished = signData.getBoolean(TSign.finished);
                    if (finished) {
                        // 已签到直接显示结果
                        showToast("已签到");
                    } else {
                        // 未签到跳转再请求签到
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
            break;

            case KIdCourse: {
                stopRefresh();
                Result<PPT> r = (Result<PPT>) result;
                if (r.isSucceed()) {
                    PPT ppt = r.getData();
                    CourseInfo courseInfo = ppt.getEv(TPPT.course);
                    if (courseInfo != null) {
                        MeetingCourseActivity.nav(MeetingDetailsActivity.this, mMeetId, mMapList.getByKey(FunctionType.ppt));
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
     * @param info
     */
    private void refreshViews(MeetDetail info) {
        @CollectType int stored = info.getInt(TMeetDetail.stored);
        mCollectType = stored; // 收藏
        mAttention = info.getBoolean(TMeetDetail.attention); // 关注
        mAttendAble = info.getBoolean(TMeetDetail.attendAble); // 能否参加
        mReason = info.getString(TMeetDetail.reason); // 不能参加的原因
        mEpnType = info.getBoolean(TMeetDetail.eduCredits); // 需要还是奖励

        switch (mCollectType) {
            case CollectType.yes:
                mIvCollection.setSelected(true);
                break;
            case CollectType.no:
                mIvCollection.setSelected(false);
                break;
        }

        mIvPlay.placeHolder(R.mipmap.ic_default_meeting_content_detail)
                .url(info.getString(TMeetDetail.coverUrl))
                .load();
        long startTime = info.getLong(TMeetDetail.startTime); // 开始时间
        mTvDate.setText(TimeUtil.formatMilli(startTime, TimeFormat.from_y_to_m_24));
        mTvDuration.setText("时长:" + Util.timeParse(info.getLong(TMeetDetail.endTime) - startTime));

        // 会议
        mTvTitle.setText(info.getString(TMeetDetail.meetName));
        mEpn = info.getInt(TMeetDetail.xsCredits);
        // 不奖励也不支付未免费, 隐藏
        if (mEpn == 0) {
            goneView(mTvAward);
        } else {
            if (mEpnType) {
                mTvAward.setText("本次会议奖励象数 : " + mEpn + "个,还有" + info.getString(TMeetDetail.remainAward) + "人可领取");
            } else {
                mTvAward.setText("本次会议需支付象数 : " + mEpn + "个");
            }
        }
        mTvSection.setText(info.getString(TMeetDetail.meetType));

        // 单位号
        mNumberId = info.getInt(TMeetDetail.pubUserId); // 公众号
        mIvNumber.placeHolder(R.mipmap.ic_default_unit_num_large)
                .url(info.getString(TMeetDetail.headimg))
                .renderer(new CircleRenderer())
                .load();
        mHost = info.getString(TMeetDetail.organizer);
        mTvUnitNum.setText(mHost);

        // 资料
        goneView(mDivider);
        goneView(mLayoutData);
        goneView(mDividerFile);

        // FIXME: 2017/6/13  caixiang
//        mMaterials = info.getList(TMeetDetail.materials);
//        if (mMaterials == null || mMaterials.size() == 0) {
//            goneView(mDivider);
//            goneView(mLayoutData);
//            goneView(mDividerFile);
//        } else {
//            UISetter.setFileData(mLayoutFile, mMaterials, mNumberId);
//        }

        // 主讲者
        mTvGN.setText(info.getString(TMeetDetail.lecturer));
        mIvGP.placeHolder(R.mipmap.ic_default_meeting_guest)
                .url(info.getString(TMeetDetail.lecturerHead))
                .load();
        // 职责和医院没有的话就隐藏
        String obligation = info.getString(TMeetDetail.lecturerTitle); // 职责
        if (TextUtil.isEmpty(obligation)) {
            goneView(mTvGP);
        } else {
            mTvGP.setText(obligation);
        }
        String hospital = info.getString(TMeetDetail.lecturerHos); // 医院
        if (TextUtil.isEmpty(hospital)) {
            goneView(mTvGH);
        } else {
            mTvGH.setText(hospital);
        }
        mTvIntro.setText(Html.fromHtml(info.getString(TMeetDetail.introduction)));

        // 模块处理
        modules(info.getList(TMeetDetail.modules));
    }

    /**
     * 处理模块
     *
     * @param modules
     */
    private void modules(List<Module> modules) {
        int type;
        Module module;
        mMapList = new MapList<>();

        for (int i = 0; i < modules.size(); i++) {
            module = modules.get(i);
            type = module.getInt(TModule.functionId);
            mMapList.add(Integer.valueOf(type), module.getString(TModule.id));
            switch (type) {
                case FunctionType.exam: {
                    // 有考试模块
                    setOnClickListener(mLayoutExam);
                    mTvExam.setTextColor(KTextColor);
                    mIvExam.setImageResource(KExamResId);
                }
                break;

                case FunctionType.que: {
                    // 有问卷模块
                    setOnClickListener(mLayoutQue);
                    mTvQue.setTextColor(KTextColor);
                    mIvQue.setImageResource(KQueResId);
                }
                break;

                case FunctionType.video: {
                    // 有视频模块
                    setOnClickListener(mLayoutVideo);
                    mTvVideo.setTextColor(KTextColor);
                    mIvVideo.setImageResource(KVideoResId);
                }
                break;

                case FunctionType.sign: {
                    // 有签到模块
                    setOnClickListener(mLayoutSign);
                    mTvSign.setTextColor(KTextColor);
                    mIvSign.setImageResource(KSignResId);
                }
                break;

                case FunctionType.ppt: {
                    // 有微课模块
                    mNoPPT = true;
                    setOnClickListener(mTvSee);
                    setOnClickListener(mIvPlay);
                    mTvSee.setBackgroundResource(R.drawable.meet_detail_see_bg_selector);
                }
                break;
            }
        }
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
                if (mLocationDialog == null) {
                    initDialog();
                }
                mLocationDialog.show();
            }
            break;
        }
    }

    /**
     * 初始化Dialog
     */
    private void initDialog() {
        mLocationDialog = new LocationDialog(MeetingDetailsActivity.this);
        mLocationDialog.setLocationListener(v -> {
            if (checkPermission(0, Permission.location, Permission.phone, Permission.storage)) {
                getSignInfo();
            }
        });
    }

    /**
     * 获取收藏按钮
     *
     * @param layout
     */
    private void getCollection(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View childView = layout.getChildAt(i);
            if (childView instanceof ViewGroup) {
                getCollection((ViewGroup) childView);
            } else if (childView instanceof ImageView) {
                mIvCollection = (ImageView) childView;
            }
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

        if (mEpnDialog != null) {
            if (mEpnDialog.isShowing()) {
                mEpnDialog.dismiss();
            }
            mEpnDialog = null;
        }
    }

    /**
     * 添加文件item
     *
     * @param text
     * @param l
     */
    public void addFileItem(CharSequence text, OnClickListener l) {

        View v = getLayoutInflater().inflate(R.layout.layout_unit_num_detail_file_item, null);
        TextView tv = (TextView) v.findViewById(R.id.unit_num_detail_file_item_tv_name);
        tv.setText(text);
        v.setOnClickListener(l);

        LayoutFitter.fit(v);
        mLayoutFile.addView(v, LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
    }

}
