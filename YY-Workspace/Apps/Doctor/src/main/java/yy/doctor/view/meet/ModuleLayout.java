package yy.doctor.view.meet;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import lib.ys.model.MapList;
import lib.ys.util.view.LayoutUtil;
import yy.doctor.R;
import yy.doctor.model.meet.MeetDetail;
import yy.doctor.model.meet.MeetDetail.TMeetDetail;
import yy.doctor.model.meet.module.BaseFunc;
import yy.doctor.model.meet.module.Module;
import yy.doctor.model.meet.module.Module.ModuleType;
import yy.doctor.model.meet.module.Module.TModule;

/**
 * 会议底下的模块
 *
 * @auther : GuoXuan
 * @since : 2017/7/3
 */
public class ModuleLayout extends LinearLayout {

    private MeetDetail mMeetDetail; // 会议数据

    private MapList<Integer, BaseFunc> mFuncs;

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

    public ModuleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ModuleLayout setFuncs(MapList<Integer, BaseFunc> funcs) {
        mFuncs = funcs;
        return this;
    }

    /**
     * 设置模块
     *
     * @param meetDetail 会议详情请求到的数据
     */
    public ModuleLayout setModules(MeetDetail meetDetail) {
        mMeetDetail = meetDetail;
        return this;
    }

    public ModuleLayout intoCourse(ImageView ivPlay, View ivPlayCourse) {
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

    /**
     * 初始化布局
     */
    private void initLayout() {
        LayoutParams params = LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
        params.weight = 1;
        for (BaseFunc func : mFuncs) {
            if (func.getType() != ModuleType.ppt) {
                addView(func.getLayout(), params);
                // 添加divider FIXME:

            }
        }

        params = LayoutUtil.getLinearParams(LayoutUtil.WRAP_CONTENT, LayoutUtil.MATCH_PARENT);
        params.weight = 0;
        addView(mFuncs.getByKey(ModuleType.ppt).getLayout(), params);
    }

    public void load() {
        initLayout();

        List<Module> modules = mMeetDetail.getList(TMeetDetail.modules);

        int type = 0;
        for (Module m : modules) {
            type = m.getInt(TModule.functionId);
            mFuncs.getByKey(type).setEnabled(true);
            mFuncs.getByKey(type).setId(m.getString(TModule.id));
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
    }

}
