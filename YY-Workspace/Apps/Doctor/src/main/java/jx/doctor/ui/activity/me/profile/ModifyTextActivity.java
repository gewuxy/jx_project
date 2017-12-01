package jx.doctor.ui.activity.me.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import inject.annotation.router.Route;
import lib.ys.util.view.ViewUtil;
import jx.doctor.Extra;
import jx.doctor.R;

/**
 * 个人资料通用页面
 *
 * @author HuoXuYu
 * @since 2017/7/14
 */
@Route
public class ModifyTextActivity extends BaseModifyActivity {

    private EditText mEtGeneral;
    private ImageView mIvClean;
    private int mLimit;


    @Override
    public void initData(Bundle state) {
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
//        mEtGeneral.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mLimit)});
        ViewUtil.limitInputCount(mEtGeneral, mLimit);
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
