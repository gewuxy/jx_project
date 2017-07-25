package yy.doctor.ui.activity.me.profile;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.Result;
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

    private EditText mEtDepartments;
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
        mEtDepartments = findView(R.id.et_departments);
        mIvCancel = findView(R.id.iv_departments_cancel);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.iv_cancel);

        addTextChangedListener(mEtDepartments, mIvCancel);
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


    @Override
    protected void doModify() {
        // TODO：伪代码网络请求
        mEtDepartments.getText();

        Result<String> result = new Result<>();
        result.setCode(ErrorCode.KOk);
        onNetworkSuccess(0, result);
    }


    @Override
    protected EditText getEt() {
        return mEtDepartments;
    }
}
