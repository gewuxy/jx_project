package yy.doctor.ui.activity.me.profile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lib.ys.util.TextUtil;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile.TProfile;

/**
 * 学术专长
 *
 * @auther HuoXuYu
 * @since 2017/7/14
 */

public class SkillActivity extends BaseModifyActivity{

    private EditText mEt;
    private TextView mTv;

    public static Intent newIntent(Context context, String title, TProfile t) {
        return new Intent(context, SkillActivity.class)
                .putExtra(Extra.KData, t)
                .putExtra(Extra.KTitle, title);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_academic;
    }

    @Override
    public void findViews() {
        mEt = findView(R.id.et_academic);
        mTv = findView(R.id.tv_academic);
    }

    @Override
    public void setViews() {
        super.setViews();

        setLength(getVal().length());

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
                }else {
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
    }

    private void setLength(int len) {
        if (len > 500) {
            len = 500;
        }

        mTv.setText(String.format(getString(R.string.academic_unit), len));
    }
}
