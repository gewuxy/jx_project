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
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 职业资格证号
 *
 * @auther HuoXuYu
 * @since 2017/7/14
 */

public class CertificationActivity extends BaseActivity{

    private EditText mEtCertification;
    private ImageView mIvCancel;
    private TextView mTv;
    private String mData;

    @Override
    public void initData() {
        mData = getIntent().getStringExtra(Extra.KData);

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_certification;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.user_certification_number, this);
        mTv = bar.addTextViewRight(R.string.save, v -> {
            notify(NotifyType.certification, mEtCertification.getText().toString());
            finish();
        });
    }


    @Override
    public void findViews() {
        mEtCertification = findView(R.id.et_certification);
        mIvCancel = findView(R.id.iv_cancel);
    }

    @Override
    public void setViews() {
        mEtCertification.setText(mData);
        setOnClickListener(R.id.iv_cancel);

        textChanged();
    }

    public void textChanged() {

        mTv.setEnabled(false);
        mTv.setTextColor(ResLoader.getColor(R.color.text_b3));
        mEtCertification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtil.isEmpty(mEtCertification.getText().toString())) {
                    mTv.setEnabled(false);
                    mTv.setTextColor(ResLoader.getColor(R.color.text_b3));
                }else {
                    mTv.setEnabled(true);
                    mTv.setTextColor(ResLoader.getColor(R.color.white));
                }

                if (TextUtil.isEmpty(mEtCertification.getText())) {
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
                mEtCertification.setText("");
            }
            break;
        }
    }

}
