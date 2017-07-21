package yy.doctor.ui.activity.me.profile;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

import static yy.doctor.R.string.save;

/**
 * 学术专长
 *
 * @auther HuoXuYu
 * @since 2017/7/14
 */

public class AcademicActivity extends BaseActivity{

    private EditText mEtAcademic;
    private TextView mTvAcademic;
    private TextView mTv;
    private String mTextNum = null;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_academic;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.academic, this);
        mTv = bar.addTextViewRight(save, v -> {
            notify(NotifyType.academic, mEtAcademic.getText().toString());
            finish();
        });
    }

    @Override
    public void findViews() {
        mEtAcademic = findView(R.id.et_academic);
        mTvAcademic = findView(R.id.tv_academic);
    }

    @Override
    public void setViews() {
        mTv.setEnabled(false);
        mTv.setTextColor(ResLoader.getColor(R.color.text_b3));
        mTextNum = String.format(getString(R.string.academic_unit), 0);
        mTvAcademic.setText(mTextNum);
        mEtAcademic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtil.isEmpty(mEtAcademic.getText().toString())) {
                    mTv.setEnabled(false);
                    mTv.setTextColor(ResLoader.getColor(R.color.text_b3));
                }else {
                    mTv.setEnabled(true);
                    mTv.setTextColor(ResLoader.getColor(R.color.white));
                }

                s.length();
                if (TextUtil.isEmpty(s)) {
                    mTextNum = String.format(getString(R.string.academic_unit), 0);
                }else {
                    mTextNum = String.format(getString(R.string.academic_unit), s.length());
                }
                mTvAcademic.setText(mTextNum);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.academic) {

        }
    }

    @Override
    public void onClick(View v) {
    }
}
