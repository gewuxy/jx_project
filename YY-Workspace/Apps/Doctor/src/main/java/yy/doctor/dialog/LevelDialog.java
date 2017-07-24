package yy.doctor.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.yy.dialog.BaseDialog;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/22
 */

public class LevelDialog extends BaseDialog {
    private TextView mLevel_Three;
    private TextView mLevel_Two;
    private TextView mLevel_One;
    private TextView mLevel_Community;
    private TextView mLevel_Village;
    private TextView mLevel_Clinic;
    private TextView mLevel_Other;

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
        super.onClick(v);
        switch (v.getId()){
            case R.id.level_three:
                Drawable three = getContext().getResources().getDrawable(R.mipmap.hospital_level_three);
                showToast("你好1");
                Notifier.inst().notify(NotifyType.dialog_miss,three);
                dismiss();
                break;
            case R.id.level_two:
                Drawable two = getContext().getResources().getDrawable(R.mipmap.hospital_level_two);
                showToast("你好2");
                dismiss();
                break;
            case R.id.level_one:
                Drawable one = getContext().getResources().getDrawable(R.mipmap.hospital_level_one);
                showToast("你好3");
                dismiss();
                break;
            case R.id.level_community:
                Drawable society = getContext().getResources().getDrawable(R.mipmap.hospital_level_community);
                showToast("你好4");
                dismiss();
                break;
            case R.id.level_village:
                Drawable village = getContext().getResources().getDrawable(R.mipmap.hospital_level_village);
                showToast("你好5");
                dismiss();
                break;
            case R.id.level_clinic:
                Drawable clinic = getContext().getResources().getDrawable(R.mipmap.hospital_level_clinic);
                showToast("你好6");
                dismiss();
                break;
            case R.id.level_other:
                Drawable other = getContext().getResources().getDrawable(R.mipmap.hospital_level_other);
                showToast("你好7");
                dismiss();
                break;

        }
    }
}
