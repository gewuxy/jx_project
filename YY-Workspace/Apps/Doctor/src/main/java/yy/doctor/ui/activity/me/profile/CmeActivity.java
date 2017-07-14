package yy.doctor.ui.activity.me.profile;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * CME卡号
 *
 * @auther Huoxuyu
 * @since 2017/7/13
 */

public class CmeActivity extends BaseActivity{

    private EditText mEtCme;
    private ImageView mIvCancel;
    private TextView mTv;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_cme;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.user_CME_number, this);
        mTv = bar.addTextViewRight(R.string.save,v -> {
                refresh(RefreshWay.dialog);
        });
    }

    @Override
    public void findViews() {
        mEtCme = findView(R.id.et_cme);
        mIvCancel = findView(R.id.iv_cancel);
    }

    @Override
    public void setViews() {

        setOnClickListener(R.id.iv_cancel);

        textChanged();
    }

    public void textChanged() {

        mTv.setEnabled(false);
        mTv.setTextColor(ResLoader.getColor(R.color.text_b3));
        mEtCme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtil.isEmpty(mEtCme.getText().toString())) {
                    mTv.setEnabled(false);
                    mTv.setTextColor(ResLoader.getColor(R.color.text_b3));
                }else {
                    //未完成
                    mTv.setEnabled(true);
                    mTv.setTextColor(ResLoader.getColor(R.color.white));
                }

                if (TextUtil.isEmpty(mEtCme.getText())) {
                    mIvCancel.setVisibility(View.GONE);
                }else {
                    mIvCancel.setVisibility(View.VISIBLE);
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
                mEtCme.setText("");
            }
            break;
        }
    }



}
