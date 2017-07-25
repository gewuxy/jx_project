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
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile.TProfile;

/**
 * 学术专长
 *
 * @auther HuoXuYu
 * @since 2017/7/14
 */

public class AcademicActivity extends BaseModifyActivity{

    private EditText mEtAcademic;
    private TextView mTvAcademic;
    private String mTextNum = null;

    public static Intent newIntent(Context context, String title, TProfile t) {
        return new Intent(context, AcademicActivity.class)
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
        mEtAcademic = findView(R.id.et_academic);
        mTvAcademic = findView(R.id.tv_academic);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTextNum = String.format(getString(R.string.academic_unit), 0);
        mTvAcademic.setText(mTextNum);

        addTextChangedListener(mEtAcademic, null);

        mEtAcademic.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtil.isEmpty(s)) {
                    mTextNum = String.format(getString(R.string.academic_unit), 0);
                }else {
                    mTextNum = String.format(getString(R.string.academic_unit), s.length());
                }
                mTvAcademic.setText(mTextNum);
            }
        });
    }

    @Override
    protected void doModify() {
        Result<String> result = new Result<>();
        result.setCode(ErrorCode.KOk);
        onNetworkSuccess(0, result);
    }

    @Override
    protected EditText getEt() {
        return mEtAcademic;
    }

    @Override
    public void onClick(View v) {
    }
}
