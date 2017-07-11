package yy.doctor.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import lib.ys.AppEx;
import lib.ys.model.MapList;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.Module;
import yy.doctor.model.meet.Module.TModule;

/**
 * 会议底下的模块
 *
 * @auther : GuoXuan
 * @since : 2017/7/3
 */
public class MeetBottomView extends LinearLayout implements OnClickListener {

    private final int KTextColor = ResLoader.getColor(R.color.text_333); // 有模块时的颜色
    private final int KExamResId = R.mipmap.meeting_ic_exam_have; // 考试
    private final int KQueResId = R.mipmap.meeting_ic_que_have; // 问卷
    private final int KVideoResId = R.mipmap.meeting_ic_video_have; // 视频
    private final int KSignResId = R.mipmap.meeting_ic_sign_have; // 签到

    private ModuleView mLayoutExam; // 考试模块
    private ModuleView mLayoutQue; // 问卷模块
    private ModuleView mLayoutVideo; // 视频模块
    private ModuleView mLayoutSign; // 签到模块
    private TextView mLayoutCourse; // 微课(PPT)模块

    private MeetDetail mMeetDetail; // 会议数据

    private MapList<Integer,String> mMapList;
    private OnModulesListener mOnModulesListener;

    @IntDef({
            HintType.attention,
            HintType.pay,
            HintType.recharge,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface HintType {
        int attention = 0;
        int pay = 1;
        int recharge = 2;
    }

    @IntDef({
            ModuleType.ppt,
            ModuleType.video,
            ModuleType.exam,
            ModuleType.que,
            ModuleType.sign,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ModuleType {
        int ppt = 1; // 微课
        int video = 2; // 视频
        int exam = 3; // 考试
        int que = 4; // 问卷
        int sign = 5; // 签到
    }

    @IntDef({
            EpnType.need,
            EpnType.award,
    })
    @Retention(RetentionPolicy.SOURCE)
    public  @interface EpnType {
        int need = 0; // 需要象数
        int award = 1; // 奖励象数
    }

    public interface OnModulesListener {
        void toExam(String moduleId);
        void toQue(String moduleId);
        void toVideo(String moduleId);
        void toSign(String moduleId);
        void toCourse(String moduleId);
        void toHint(int type);
    }
    public void setOnModulesListener(OnModulesListener l){
        mOnModulesListener = l;
    }


    private boolean needPay(@EpnType int type) {
        if (type == EpnType.need) {
            return true;
        } else {
            return false;
        }
    }

    public MeetBottomView(Context context) {
        this(context, null);
    }

    public MeetBottomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeetBottomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MeetBottomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    /**
     * 初始化布局
     */
    private void init() {
        View v = inflate(getContext(), R.layout.meet_bottom, null);
        mLayoutExam = (ModuleView) v.findViewById(R.id.meet_module_layout_exam);
        mLayoutQue = (ModuleView) v.findViewById(R.id.meet_module_layout_que);
        mLayoutVideo = (ModuleView) v.findViewById(R.id.meet_module_layout_video);
        mLayoutSign = (ModuleView) v.findViewById(R.id.meet_module_layout_sign);
        mLayoutCourse = (TextView) v.findViewById(R.id.meet_module_layout_course);

        addView(v, LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
    }

    /**
     * 设置模块
     *
     * @param meetDetail 会议详情请求到的数据
     */
    public void setModules(MeetDetail meetDetail) {
        mMeetDetail = meetDetail;
        List<Module> modules = meetDetail.getList(TMeetDetail.modules);
        Module module;
        int type;
        mMapList = new MapList<>();
        for (int i = 0; i < modules.size(); i++) {
            module = modules.get(i);
            type = module.getInt(TModule.functionId);
            mMapList.add(Integer.valueOf(type), module.getString(TModule.id));
            switch (type) {
                // 有考试模块
                case ModuleType.exam: {
                    setOnClickListener(mLayoutExam);
                    mLayoutExam.setImage(KExamResId).setTextColor(KTextColor);
                }
                break;
                // 有问卷模块
                case ModuleType.que: {
                    setOnClickListener(mLayoutQue);
                    mLayoutQue.setImage(KQueResId).setTextColor(KTextColor);
                }
                break;
                // 有视频模块
                case ModuleType.video: {
                    setOnClickListener(mLayoutVideo);
                    mLayoutVideo.setImage(KVideoResId).setTextColor(KTextColor);
                }
                break;
                // 有签到模块
                case ModuleType.sign: {
                    setOnClickListener(mLayoutSign);
                    mLayoutSign.setImage(KSignResId).setTextColor(KTextColor);

                }
                break;
                // 有微课模块
                case ModuleType.ppt: {
                    setOnClickListener(mLayoutCourse);
                    mLayoutCourse.setBackgroundResource(R.drawable.meet_detail_see_bg_selector);
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mMeetDetail.getBoolean(TMeetDetail.attention)) {
            // 关注了
            if (mMeetDetail.getBoolean(TMeetDetail.attendAble)) {
                // 可以参加
                if (mMeetDetail.getBoolean(TMeetDetail.attended)) {
                    // 参加过(奖励过和支付过)
                    toClickModule(v);
                } else {
                    // 没有参加过
                    int epn = mMeetDetail.getInt(TMeetDetail.xsCredits); // 象数
                    if (epn > 0) {
                        int surplus = Profile.inst().getInt(TProfile.credits); // 剩余象数
                        @EpnType int epnType = mMeetDetail.getInt(TMeetDetail.eduCredits); // 需要还是奖励
                        if (needPay(epnType)) {
                            // 需要象数的
                            if (surplus < epn) {
                                // 象数不足
                                if (mOnModulesListener != null) {
                                    mOnModulesListener.toHint(HintType.recharge);
                                }
                            } else {
                                // 支付
                                if (mOnModulesListener != null) {
                                    mOnModulesListener.toHint(HintType.pay);
                                }
                            }
                        } else {
                            // FIXME: 2017/6/30 应该后台返回正确的,多人操作的时候
                            if (mMeetDetail.getInt(TMeetDetail.remainAward) > 0) {
                                // 奖励人数大于0奖励象数的
                                Profile.inst().put(TProfile.credits, surplus + epn);
                                Profile.inst().saveToSp();
                                Notifier.inst().notify(NotifyType.profile_change);
                            }
                            mMeetDetail.put(TMeetDetail.attended, true); // 奖励象数 (参加过会议)
                            toClickModule(v);
                        }
                    } else {
                        // 免费直接参加
                        toClickModule(v);
                    }
                }
            } else {
                // 不能参加的原因
                AppEx.showToast(mMeetDetail.getString(TMeetDetail.reason));
            }
        } else {
            if (mOnModulesListener != null) {
                mOnModulesListener.toHint(HintType.attention);
            }
        }
    }

    private void toClickModule(View v) {
        switch (v.getId()) {
            case R.id.meet_module_layout_exam:{
                if (mOnModulesListener != null) {
                    mOnModulesListener.toExam(mMapList.getByKey(ModuleType.exam));
                }
            }
                break;
            case R.id.meet_module_layout_que:{
                if (mOnModulesListener != null) {
                    mOnModulesListener.toQue(mMapList.getByKey(ModuleType.que));
                }
            }
            break;
            case R.id.meet_module_layout_video:{
                if (mOnModulesListener != null) {
                    mOnModulesListener.toVideo(mMapList.getByKey(ModuleType.video));
                }
            }
            break;
            case R.id.meet_module_layout_sign:{
                if (mOnModulesListener != null) {
                    mOnModulesListener.toSign(mMapList.getByKey(ModuleType.sign));
                }
            }
            break;
            case R.id.meet_module_layout_course:{
                if (mOnModulesListener != null) {
                    mOnModulesListener.toCourse(mMapList.getByKey(ModuleType.ppt));
                }
            }
            break;
        }

    }

    /* 保持用法一致 */

    private void setOnClickListener(@NonNull View v) {
        if (v != null) {
            v.setOnClickListener(this);
        }
    }


}
