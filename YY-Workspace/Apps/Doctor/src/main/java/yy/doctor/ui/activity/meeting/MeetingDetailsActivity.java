package yy.doctor.ui.activity.meeting;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.model.MapList;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.TimeUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra.FileFrom;
import yy.doctor.R;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.Meeting.MeetState;
import yy.doctor.model.meet.module.BaseFunc;
import yy.doctor.model.meet.module.BaseFunc.OnFuncListener;
import yy.doctor.model.meet.module.CourseFunc;
import yy.doctor.model.meet.module.ExamFunc;
import yy.doctor.model.meet.module.SignFunc;
import yy.doctor.model.meet.module.SurveyFunc;
import yy.doctor.model.meet.module.VideoFunc;
import yy.doctor.model.unitnum.File;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.CollectionAPI;
import yy.doctor.network.NetworkAPISetter.MeetAPI;
import yy.doctor.network.UrlUtil;
import yy.doctor.network.UrlUtil.UrlMeet;
import yy.doctor.serv.CommonServ.ReqType;
import yy.doctor.serv.CommonServRouter;
import yy.doctor.ui.activity.me.unitnum.UnitNumDetailActivityRouter;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.util.Time;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;
import yy.doctor.view.CircleProgressView;
import yy.doctor.view.meet.MaterialView;
import yy.doctor.view.meet.ModuleLayout;

/**
 * 会议详情界面
 * <p>
 * 日期 : 2017/4/21
 * 创建人 : guoxuan
 */
@Route
public class MeetingDetailsActivity extends BaseActivity implements OnFuncListener {

    private static final int KIdMeetDetail = 0; // 会议详情
    private static final int KIdCollection = 1; // 收藏

    private ImageView mIvCollection; // navBar的收藏

    private NetworkImageView mIvPlay; // 缩略图
    private View mLayout; // 右下角的图标   整个学习进度的layout，默认隐藏
    private CircleProgressView mLayoutProgress; // 右下角的图标
    private TextView mTvProgress; // 右下角的图标   数字默认10%
    private TextView mTvDate; // 会议开始时间
    private TextView mTvDuration; // 会议时长

    // 会议介绍
    private TextView mTvTitle; // 会议名称
    private TextView mTvSection; // 会议科室
    private TextView mTvMeetType; // 会议科室
    private TextView mTvEpn; // 象数相关
    private ImageView mIvEpn;
    private TextView mTvCme;// 学分相关
    private ImageView mIvCme;

    // 单位号介绍
    private NetworkImageView mIvNumber; // 单位号图标
    private TextView mTvUnitNum; // 单位号


    // 主讲者
    private NetworkImageView mIvGP; // 头像
    private TextView mTvGN; // 名字
    private TextView mTvGP; // 职位
    private TextView mTvGH; // 医院

    private TextView mTvIntro; // 会议简介
    private TextView mTvFrom; // 会议简介

    private MapList<Integer, BaseFunc> mFuncs; // 记录模块ID
    private ModuleLayout mModuleLayout; // 模块

    private MeetDetail mMeetDetail; // 会议详情信息
    @Arg
    String mMeetId; // 会议Id
    @Arg
    String mMeetName; //  会议名字(没有请求到数据时也可以分享会议)

    private long mStartModuleTime; // 模块开始时间
    private long mMeetTime; // 统一用通知不用result

    @DataType
    private int mType = DataType.meeting;

    private BaseFunc mCourseFunc;
    //资料
    private MaterialView mMaterialView;

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_detail;
    }

    @Override
    public void initData() {
        mMeetTime = 0;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.meeting_detail, this);
        // 收藏
        ViewGroup group = bar.addViewRight(R.drawable.collection_selector, v -> {
            // 无网
            if (Util.noNetwork()) {
                return;
            }

            boolean storedState = true; // 默认没有关注, 故点击时关注(MeetDetail还没获取到数据时)
            @StringRes int collectHint = R.string.collect_finish;
            if (mMeetDetail != null) {
                // 状态取反
                storedState = !mMeetDetail.getBoolean(TMeetDetail.stored);
                mMeetDetail.put(TMeetDetail.stored, storedState);
                if (MeetState.not_started == mMeetDetail.getInt(TMeetDetail.state)) {
                    collectHint = R.string.collect_no_start;
                }
            }
            mIvCollection.setSelected(storedState);
            showToast(storedState ? collectHint : R.string.cancel_collect);
            exeNetworkReq(KIdCollection, CollectionAPI.collectionStatus(mMeetId, mType).build());
            if (!storedState) {
                // 取消收藏(通知会议收藏列表去除会议)
                notify(NotifyType.collection_cancel_meeting, mMeetId);
            }
        });
        mIvCollection = Util.getBarView(group, ImageView.class);
        // 分享
        bar.addViewRight(R.drawable.nav_bar_ic_share, v -> {
            if (Util.noNetwork()) {
                return;
            }
            ShareDialog dialog = new ShareDialog(MeetingDetailsActivity.this, UrlUtil.getBaseUrl() + UrlMeet.KMeetShare + mMeetId, mMeetName);
            dialog.show();
        });
    }

    @Override
    public void findViews() {
        mIvPlay = findView(R.id.meeting_detail_iv_play);
        mLayout = findView(R.id.meeting_detail_layout_progress);
        mLayoutProgress = findView(R.id.meeting_detail_v_progress);
        mTvProgress = findView(R.id.meeting_detail_tv_progress);
        mTvDate = findView(R.id.meeting_detail_tv_date);
        mTvDuration = findView(R.id.meeting_detail_tv_time);

        // 会议相关
        mTvTitle = findView(R.id.meeting_detail_tv_title);
        mTvSection = findView(R.id.meeting_detail_tv_section);
        mTvMeetType = findView(R.id.meeting_detail_tv_meet_type);
        mTvEpn = findView(R.id.meeting_detail_tv_epn);
        mIvEpn = findView(R.id.meeting_detail_iv_epn);
        mTvCme = findView(R.id.meeting_detail_tv_cmd);
        mIvCme = findView(R.id.meeting_detail_iv_cmd);

        // 单位号相关
        mIvNumber = findView(R.id.meeting_detail_iv_number);
        mTvUnitNum = findView(R.id.meeting_detail_tv_unit_num);

        // 文件
        mMaterialView = findView(R.id.layout_meeting_detail_material);

        // 主讲者相关
        mTvGN = findView(R.id.meeting_tv_guest_name);
        mTvGP = findView(R.id.meeting_tv_guest_post);
        mTvGH = findView(R.id.meeting_tv_guest_hospital);
        mIvGP = findView(R.id.meeting_iv_guest_portrait);

        mTvIntro = findView(R.id.meeting_detail_tv_intro);
        mTvFrom = findView(R.id.meeting_detail_tv_from);

        mModuleLayout = findView(R.id.meeting_detail_modules);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.meeting_detail_player_layout);
        setOnClickListener(R.id.meeting_detail_layout_unit_num);

        getDataFromNet();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_detail_player_layout: {
                mCourseFunc.onClick(v);
            }
            break;
            case R.id.meeting_detail_layout_unit_num: {
                // FIXME: 7.1.6 UnitNumId
                UnitNumDetailActivityRouter.create(mMeetDetail.getInt(TMeetDetail.pubUserId)).route(MeetingDetailsActivity.this);
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KIdMeetDetail) {
            return JsonParser.ev(r.getText(), MeetDetail.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KIdMeetDetail) {
            Result<MeetDetail> r = (Result<MeetDetail>) result;
            if (r.isSucceed()) {
                mMeetDetail = r.getData();
                refreshViews(mMeetDetail);
                setViewState(ViewState.normal);
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        if (id == KIdMeetDetail) {
            setViewState(ViewState.error);
        }
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            getDataFromNet();
        }
        return true;
    }

    private void getDataFromNet() {
        refresh(RefreshWay.embed);
        exeNetworkReq(KIdMeetDetail, MeetAPI.meetInfo(mMeetId).build());
    }

    /**
     * 更新界面数据
     *
     * @param detail
     */
    private void refreshViews(MeetDetail detail) {
        mIvCollection.setSelected(detail.getBoolean(TMeetDetail.stored));

        mIvPlay.placeHolder(R.drawable.ic_default_meeting_content_detail)
                .url(detail.getString(TMeetDetail.coverUrl))
                .load();

        long startTime = detail.getLong(TMeetDetail.startTime); // 开始时间
        mTvDate.setText(TimeUtil.formatMilli(startTime, "MM月dd日 HH:mm"));
        mTvDuration.setText("时长:" + Time.milliFormat(detail.getLong(TMeetDetail.endTime) - startTime));

        // 会议
        mTvTitle.setText(detail.getString(TMeetDetail.meetName));
        int costEpn = detail.getInt(TMeetDetail.xsCredits);
        if (costEpn > 0) {
            showView(mTvEpn);
            showView(mIvEpn);
            String epnStr = null;
            if (detail.getBoolean(TMeetDetail.requiredXs)) {
                // 奖励
                YSLog.d(TAG, "refreshViews:奖励" + costEpn);
                if (detail.getBoolean(TMeetDetail.receiveAwardXs)) {
                    epnStr = String.format(getString(R.string.meeting_epn_award_after), costEpn);
                } else {
                    epnStr = String.format(getString(R.string.meeting_epn_award_before),
                            costEpn, detail.getInt(TMeetDetail.remainAwardXsCount, 0));
                }
                mIvEpn.setSelected(true);
            } else {
                // 支付
                YSLog.d(TAG, "refreshViews:支付" + costEpn);
                if (detail.getBoolean(TMeetDetail.attended)) {
                    epnStr = String.format(getString(R.string.meeting_epn_pay_after), costEpn);
                } else {
                    epnStr = String.format(getString(R.string.meeting_epn_pay_before), costEpn);
                }
                mIvEpn.setSelected(false);
            }
            mTvEpn.setText(epnStr);
        }

        int credit = detail.getInt(TMeetDetail.eduCredits);
        if (credit > 0 && detail.getBoolean(TMeetDetail.rewardCredit)) {
            showView(mTvCme);
            showView(mIvCme);
            if (detail.getBoolean(TMeetDetail.receiveAwardCredit)) {
                mTvCme.setText(String.format(getString(R.string.meeting_cme_award_after), credit));
            } else {
                mTvCme.setText(String.format(getString(R.string.meeting_cme_award_before),
                        credit, detail.getInt(TMeetDetail.remainAwardCreditCount)));
            }
            YSLog.d(TAG, "refreshViews:有学分" + costEpn);
        }
        mTvSection.setText(detail.getString(TMeetDetail.meetType));
        // FIXME: 7.1.6 会议类型
        mTvMeetType.setText(detail.getBroadcastType());

        // 学习进度
        int progress = detail.getInt(TMeetDetail.completeProgress);
        if (progress > 0) {
            showView(mLayout);
            String percent = progress + "%";
            if (progress == 100) {
                percent = "完成";
            }
            mLayoutProgress.setProgress(progress);
            mTvProgress.setText(percent);
        }

        // 单位号
        mIvNumber.placeHolder(R.drawable.ic_default_unit_num_large)
                .url(detail.getString(TMeetDetail.headimg))
                .renderer(new CircleRenderer())
                .load();
        mTvUnitNum.setText(detail.getString(TMeetDetail.organizer));

        // 资料
        int fileNum = detail.getInt(TMeetDetail.materialCount);
        List<File> materials = detail.getList(TMeetDetail.materials);
        mMaterialView.setFileId(mMeetId)
                .setFiles(materials)
                .setFileType(FileFrom.meeting)
                .setNum(fileNum)
                .load();

        // 主讲者
        mTvGN.setText(detail.getString(TMeetDetail.lecturer));
        mIvGP.placeHolder(R.drawable.ic_default_meeting_guest)
                .url(detail.getString(TMeetDetail.lecturerHead))
                .load();
        // 职责和医院没有的话就隐藏
        UISetter.viewVisibility(detail.getString(TMeetDetail.lecturerTitle), mTvGP); // 职责
        UISetter.viewVisibility(detail.getString(TMeetDetail.lecturerHos), mTvGH); // 医院
        UISetter.viewVisibility(formatIntro(Html.fromHtml(detail.getString(TMeetDetail.introduction)).toString()), mTvIntro);
        // FIXME: 转载
        String from = detail.getString(TMeetDetail.reprintFromUnitUser);
        if (TextUtil.isNotEmpty(from)) {
            showView(mTvFrom);
            mTvFrom.setText(String.format("本会议转载自%s", from));
        }

        mFuncs = new MapList<>();
        BaseFunc func = null;

        func = new ExamFunc(this, detail, this);
        mFuncs.add(Integer.valueOf(func.getType()), func);

        func = new SurveyFunc(this, detail, this);
        mFuncs.add(Integer.valueOf(func.getType()), func);

        func = new VideoFunc(this, detail, this);
        mFuncs.add(Integer.valueOf(func.getType()), func);

        func = new SignFunc(this, detail, this);
        mFuncs.add(Integer.valueOf(func.getType()), func);

        mCourseFunc = new CourseFunc(this, detail, this);
        mFuncs.add(Integer.valueOf(mCourseFunc.getType()), mCourseFunc);

        // 模块处理
        mModuleLayout
                .setFuncs(mFuncs)
                .setModules(detail)
                .load();
    }

    /**
     * 去掉头尾的空行
     */
    private CharSequence formatIntro(String text) {
        while (text.startsWith("\n")) {
            text = text.substring(1);
        }
        while (text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    @Override
    public void onFuncLoading() {
        refresh(RefreshWay.dialog);
    }

    @Override
    public void onFuncNormal() {
        stopRefresh();
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.study_end) {
            mMeetTime += System.currentTimeMillis() - mStartModuleTime;
            YSLog.d(TAG, "onNotify:end" + mMeetTime);
        } else if (type == NotifyType.study_start) {
            mStartModuleTime = System.currentTimeMillis();
            YSLog.d(TAG, "onNotify:start" + mStartModuleTime);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 开启服务提交会议学习时间
        if (mMeetTime > 0) {
            CommonServRouter.create()
                    .type(ReqType.meet)
                    .meetId(mMeetId)
                    .meetTime(mMeetTime / TimeUnit.SECONDS.toMillis(1))
                    .route(this);
        }
    }
}
