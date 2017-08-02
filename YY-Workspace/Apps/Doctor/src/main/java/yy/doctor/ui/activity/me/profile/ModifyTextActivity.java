package yy.doctor.ui.activity.me.profile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
    private ImageView mIvCancel;

    public static Intent newIntent(Context context, String title, TProfile t) {
        return new Intent(context, ModifyTextActivity.class)
                .putExtra(Extra.KData, t)
                .putExtra(Extra.KTitle, title);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_profile_modify_text;
    }

    @Override
    public void findViews() {
        mEtGeneral = findView(R.id.et_general);
        mIvCancel = findView(R.id.iv_general_cancel);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.iv_cancel);

        addTextChangedListener(mEtGeneral, mIvCancel);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.iv_cancel: {
                mEtGeneral.setText("");
            }
            break;
        }
    }

    @Override
    protected EditText getEt() {
        return mEtGeneral;
    }
}
