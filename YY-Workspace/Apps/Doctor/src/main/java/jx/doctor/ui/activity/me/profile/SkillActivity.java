package jx.doctor.ui.activity.me.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import inject.annotation.router.Route;
import lib.ys.util.TextUtil;
import jx.doctor.R;

/**
 * 学术专长
 *
 * @auther HuoXuYu
 * @since 2017/7/14
 */
@Route
public class SkillActivity extends BaseModifyActivity {

    private EditText mEt;
    private TextView mTv;


    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_academic;
    }

    @Override
    public void findViews() {
        mEt = findView(R.id.academic_et);
        mTv = findView(R.id.academic_tv);
    }

    @Override
    public void setViews() {
        super.setViews();

        setLength(getVal().length());

        setOnClickListener(R.id.academic_iv_clean);
        addTextChangedListener(mEt, null);

        mEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtil.isEmpty(s)) {
                    setLength(0);
                } else {
                    setLength(s.length());
                }
            }
        });
    }

    @Override
    protected EditText getEt() {
        return mEt;
    }

    @Override
    public void onClick(View v) {
        mEt.setText("");
    }

    private void setLength(int len) {
        if (len > 500) {
            len = 500;
        }

        mTv.setText(String.format(getString(R.string.academic_unit), len));
    }
}
