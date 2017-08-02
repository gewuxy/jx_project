package yy.doctor.view.meet;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import lib.ys.fitter.LayoutFitter;
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

    /**
     * 初始化布局
     */
    private void initLayout() {
        LayoutParams params = LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
        params.weight = 1;
        LayoutParams dividerParams = LayoutUtil.getLinearParams(1, 16);
        dividerParams.gravity = Gravity.CENTER_VERTICAL;
        View divider;

        for (BaseFunc func : mFuncs) {
            if (func.getType() != ModuleType.ppt) {
                // 添加分割线
                if (getChildCount() != 0) {
                    divider = new View(getContext());
                    divider.setBackgroundResource(R.color.divider);
                    addView(divider, dividerParams);
                }
                // 添加模块
                addView(func.getLayout(), params);
            }
        }

        params = LayoutUtil.getLinearParams(LayoutUtil.WRAP_CONTENT, LayoutUtil.MATCH_PARENT);
        params.weight = 0;

        addView(mFuncs.getByKey(ModuleType.ppt).getLayout(), params);

        LayoutFitter.fit(this);
    }

    public void load() {
        initLayout();

        int type = 0;
        List<Module> modules = mMeetDetail.getList(TMeetDetail.modules);

        for (Module m : modules) {
            type = m.getInt(TModule.functionId);
            mFuncs.getByKey(type).setEnabled(true);
            mFuncs.getByKey(type).setId(m.getString(TModule.id));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mFuncs != null) {
            for (BaseFunc func : mFuncs) {
                func.onDestroy();
            }
        }
    }
}
