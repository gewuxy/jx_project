package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lib.ys.YSLog;
import lib.ys.fitter.LayoutFitter;
import lib.ys.network.image.NetworkImageView;
import lib.yy.dialog.BaseDialog;
import yy.doctor.R;
import yy.doctor.model.hospital.HospitalLevel;
import yy.doctor.model.hospital.HospitalLevel.THospitalLevel;
import yy.doctor.model.hospital.HospitalLevelInfo;
import yy.doctor.model.hospital.HospitalLevelInfo.THospitalLevelInfo;
import yy.doctor.sp.SpApp;
import yy.doctor.sp.SpApp.SpAppKey;

/**
 * @auther WangLan
 * @since 2017/7/22
 */

public class LevelDialog extends BaseDialog {

    private LinearLayout mLayout;
    private List<HospitalLevel> mLevels;
    private OnLevelCheckListener mListener;

    public interface OnLevelCheckListener {
        void onLevelChecked(HospitalLevel h);
    }

    public void setListener(OnLevelCheckListener l) {
        mListener = l;
    }

    public LevelDialog(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        HospitalLevelInfo info = SpApp.inst().getEV(SpAppKey.KHosLVs, HospitalLevelInfo.class);
        mLevels = info.getList(THospitalLevelInfo.propList);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_hospital_level;
    }

    @Override
    public void findViews() {
        mLayout = findView(R.id.dialog_hospital_layout_levels);
    }

    @Override
    public void setViews() {
        View v;
        TextView tvLevel;
        NetworkImageView ivLevel;
        for (HospitalLevel level : mLevels) {
            v = View.inflate(getContext(), R.layout.layout_hospital_level, null);
            tvLevel = (TextView) v.findViewById(R.id.layout_hospital_tv_level);
            tvLevel.setText(level.getString(THospitalLevel.propValue));
            ivLevel = (NetworkImageView) v.findViewById(R.id.layout_hospital_iv_level);
            ivLevel.url(level.getString(THospitalLevel.picture)).load();
            v.setTag(level);
            LayoutFitter.fit(v);
            setOnClickListener(v);
            mLayout.addView(v);
        }

    }

    @Override
    public void onClick(View v) {
        HospitalLevel h = (HospitalLevel) v.getTag();
        if (mListener != null) {
            mListener.onLevelChecked(h);
        }
    }
}
