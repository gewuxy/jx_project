package yy.doctor.ui.activity.me.profile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile.TProfile;

/**
 * 个人资料通用页面
 *
 * @author HuoXuYu
 * @since 2017/7/14
 */
public class ModifyTextActivity extends BaseModifyActivity {

    private EditText mEtGeneral;
    private ImageView mIvClean;
    private int mLimit;

    public static Intent newIntent(Context context, String title, TProfile t) {
        return new Intent(context, ModifyTextActivity.class)
                .putExtra(Extra.KData, t)
                .putExtra(Extra.KTitle, title);
    }

    @Override
    public void initData() {
        super.initData();
        //获取限制长度的字段并 赋默认值
        mLimit = getIntent().getIntExtra(Extra.KLimit, 30);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_profile_modify_text;
    }

    @Override
    public void findViews() {
        mEtGeneral = findView(R.id.et_general);
        mIvClean = findView(R.id.academic_iv_clean);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.academic_iv_clean);

        addTextChangedListener(mEtGeneral, mIvClean);
        mEtGeneral.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mLimit)});
    }

    @Override
    public void onClick(View v) {
        mEtGeneral.setText("");
    }

    @Override
    protected EditText getEt() {
        return mEtGeneral;
    }
}
