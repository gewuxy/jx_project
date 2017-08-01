package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/22
 */

public class LevelDialog extends BaseDialog {

    public interface OnLevelCheckListener {
        void onLevelChecked(@DrawableRes int resId);
    }

    private TextView mLevel_Three;
    private TextView mLevel_Two;
    private TextView mLevel_One;
    private TextView mLevel_Community;
    private TextView mLevel_Village;
    private TextView mLevel_Clinic;
    private TextView mLevel_Other;

    private OnLevelCheckListener mListener;

    public void setListener(OnLevelCheckListener l) {
        mListener = l;
    }

    public LevelDialog(Context context) {
        super(context);
    }

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_hospital_level;
    }

    @Override
    public void findViews() {
        mLevel_Three = findView(R.id.level_three);
        mLevel_Two = findView(R.id.level_two);
        mLevel_One = findView(R.id.level_one);
        mLevel_Community = findView(R.id.level_community);
        mLevel_Village = findView(R.id.level_village);
        mLevel_Clinic = findView(R.id.level_clinic);
        mLevel_Other = findView(R.id.level_other);
    }

    @Override
    public void setViews() {
        setOnClickListener(mLevel_Three);
        setOnClickListener(mLevel_Two);
        setOnClickListener(mLevel_One);
        setOnClickListener(mLevel_Community);
        setOnClickListener(mLevel_Village);
        setOnClickListener(mLevel_Clinic);
        setOnClickListener(mLevel_Other);
    }

    @Override
    public void onClick(View v) {
        @DrawableRes int resId = R.mipmap.hospital_level_three;
        switch (v.getId()){
            case R.id.level_three:
                resId = R.mipmap.hospital_level_three;
               dismiss();
                break;
            case R.id.level_two:
                resId = R.mipmap.hospital_level_two;
                dismiss();
                break;
            case R.id.level_one:
                resId = R.mipmap.hospital_level_one;
                dismiss();
                break;
            case R.id.level_community:
                resId = R.mipmap.hospital_level_community;
                dismiss();
                break;
            case R.id.level_village:
                resId = R.mipmap.hospital_level_village;
                dismiss();
                break;
            case R.id.level_clinic:
                resId = R.mipmap.hospital_level_clinic;
                dismiss();
                break;
            case R.id.level_other:
                resId = R.mipmap.hospital_level_other;
                dismiss();
                break;

        }
        if (mListener!= null) {
            mListener.onLevelChecked(resId);
        }
    }
}
