package yy.doctor.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import lib.ys.AppEx;
import lib.ys.model.MapList;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.Module;
import yy.doctor.model.meet.Module.ModuleType;
import yy.doctor.model.meet.Module.TModule;

/**
 * 会议底下的模块
 *
 * @auther : GuoXuan
 * @since : 2017/7/3
 */
public class MeetModuleView extends LinearLayout implements OnClickListener {

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

    private MapList<Integer, String> mModules;
    private OnModulesListener mOnModulesListener;
    private ImageView mCourseView;
    private View mCourseLogo;

    @IntDef({
            DialogType.attention,
            DialogType.pay,
            DialogType.recharge,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogType {
        int attention = 0;
        int pay = 1;
        int recharge = 2;
    }

    @IntDef({
            EpnType.need,
            EpnType.award,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface EpnType {
        int need = 0; // 需要象数
        int award = 1; // 奖励象数
    }

    public interface OnModulesListener {
        void toClickModule(int type, MapList<Integer, String> modules);

        void toShowDialog(int type, View v);
    }

    public MeetModuleView setOnModulesListener(OnModulesListener l) {
        mOnModulesListener = l;
        return this;
    }

    /**
     * 设置模块
     *
     * @param meetDetail 会议详情请求到的数据
     */
    public MeetModuleView setModules(MeetDetail meetDetail) {
        mMeetDetail = meetDetail;
        return this;
    }

    public MeetModuleView intoCourse(ImageView ivPlay, View ivPlayCourse) {
        mCourseView = ivPlay;
        mCourseLogo = ivPlayCourse;
        return this;
    }

    private boolean needPay(@EpnType int type) {
        if (type == EpnType.need) {
            return true;
        } else {
            return false;
        }
    }

    public MeetModuleView(Context context) {
        this(context, null);
    }

    public MeetModuleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeetModuleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MeetModuleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

    public void load() {
        List<Module> modules = mMeetDetail.getList(TMeetDetail.modules);
        Module module;
        int type = 0;
        mModules = new MapList<>();
        for (int i = 0; i < modules.size(); i++) {
            module = modules.get(i);
            switch (module.getInt(TModule.functionId)) {
                case ModuleType.exam: {
                    // 有考试模块
                    mLayoutExam.setImage(KExamResId)
                            .setTextColor(KTextColor)
                            .setOnClickListener(this);
                    type = ModuleType.exam;
                }
                break;
                case ModuleType.que: {
                    // 有问卷模块
                    mLayoutQue.setImage(KQueResId).
                            setTextColor(KTextColor)
                            .setOnClickListener(this);
                    type = ModuleType.que;
                }
                break;
                case ModuleType.video: {
                    // 有视频模块
                    mLayoutVideo.setImage(KVideoResId)
                            .setTextColor(KTextColor)
                            .setOnClickListener(this);
                    type = ModuleType.video;
                }
                break;
                case ModuleType.sign: {
                    // 有签到模块
                    mLayoutSign.setImage(KSignResId)
                            .setTextColor(KTextColor)
                            .setOnClickListener(this);
                    type = ModuleType.sign;
                }
                break;
                case ModuleType.ppt: {
                    // 有微课模块
                    if (mCourseView != null) {
                        mCourseView.setOnClickListener(this);
                    }
                    if (mCourseLogo != null) {
                        mCourseLogo.setVisibility(VISIBLE);
                    }
                    mLayoutCourse.setOnClickListener(this);
                    mLayoutCourse.setBackgroundResource(R.drawable.meet_detail_see_bg_selector);
                    type = ModuleType.ppt;
                }
                break;
            }
            mModules.add(Integer.valueOf(type), module.getString(TModule.id));
        }
    }

    @Override
    public void onClick(View v) {
        if (mMeetDetail.getBoolean(TMeetDetail.attention)) {
            // 关注了
            if (mMeetDetail.getBoolean(TMeetDetail.attendAble)) {
                // 可以参加
                int costEpn = mMeetDetail.getInt(TMeetDetail.xsCredits); // 象数
                @EpnType int epnType = mMeetDetail.getInt(TMeetDetail.eduCredits); // 需要还是奖励
                if (!mMeetDetail.getBoolean(TMeetDetail.attended) && costEpn > 0 && needPay(epnType)) {
                    // 没有参加过且需要象数(需要的象数大于0)
                    if (Profile.inst().getInt(TProfile.credits) < costEpn) {
                        // 剩余象数对于需要象数(象数不足)
                        if (mOnModulesListener != null) {
                            mOnModulesListener.toShowDialog(DialogType.recharge, v);
                        }
                    } else {
                        // 象数足够, 支付
                        if (mOnModulesListener != null) {
                            mOnModulesListener.toShowDialog(DialogType.pay, v);
                        }
                    }
                } else {
                    // 参加过(奖励过和支付过), 不需支付(奖励)象数, 直接参加
                    toClickModule(v);
                }
            } else {
                // 不能参加的原因
                AppEx.showToast(mMeetDetail.getString(TMeetDetail.reason));
            }
        } else {
            // 提示关注
            if (mOnModulesListener != null) {
                mOnModulesListener.toShowDialog(DialogType.attention, v);
            }
        }
    }

    public void toClickModule(View v) {
        int type = 0;
        switch (v.getId()) {
            case R.id.meet_module_layout_exam: {
                type = ModuleType.exam;
            }
            break;
            case R.id.meet_module_layout_que: {
                type = ModuleType.que;
            }
            break;
            case R.id.meet_module_layout_video: {
                type = ModuleType.video;
            }
            break;
            case R.id.meet_module_layout_sign: {
                type = ModuleType.sign;
            }
            break;
            case R.id.meet_module_layout_course: {
                type = ModuleType.ppt;
            }
            break;
        }
        if (mCourseView != null && mCourseView.getId() == v.getId()) {
            type = ModuleType.ppt;
        }
        if (mOnModulesListener != null) {
            mOnModulesListener.toClickModule(type, mModules);
        }
    }

}
