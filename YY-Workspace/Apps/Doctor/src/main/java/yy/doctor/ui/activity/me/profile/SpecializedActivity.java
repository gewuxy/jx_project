package yy.doctor.ui.activity.me.profile;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 医院科室页面
 *
 * @author HuoXuYu
 * @since 2017/7/14
 */
public class SpecializedActivity extends BaseActivity{

    private EditText mEtDepartments;
    private ImageView mIvCancel;
    private TextView mTv;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_departments;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.section, this);
        mTv = bar.addTextViewRight(R.string.save, v -> {
            notify(NotifyType.section, mEtDepartments.getText().toString());
            finish();
        });
    }

    @Override
    public void findViews() {
        mEtDepartments = findView(R.id.et_departments);
        mIvCancel = findView(R.id.iv_departments_cancel);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.iv_cancel);

        textChanged();
    }

    public void textChanged() {

        mTv.setEnabled(false);
        mTv.setTextColor(ResLoader.getColor(R.color.text_b3));
        mEtDepartments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtil.isEmpty(mEtDepartments.getText().toString())) {
                    mTv.setEnabled(false);
                    mTv.setTextColor(ResLoader.getColor(R.color.text_b3));
                }else {
                    mTv.setEnabled(true);
                    mTv.setTextColor(ResLoader.getColor(R.color.white));
                }

                if (TextUtil.isEmpty(mEtDepartments.getText())) {
                    hideView(mIvCancel);
                }else {
                    showView(mIvCancel);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.iv_cancel: {
                mEtDepartments.setText("");
            }
            break;
        }
    }
}
