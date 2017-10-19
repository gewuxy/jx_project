package jx.csp.ui.activity.me.profile;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import inject.annotation.router.Route;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.view.ViewUtil;

/**
 * 昵称
 *
 * @auther Huoxuyu
 * @since 2017/9/21
 */
@Route
public class NickNameActivity extends BaseMyMessageActivity {

    private EditText mEt;
    private int mLimit;

    @Override
    public void initData() {
        mLimit = getIntent().getIntExtra(Extra.KLimit, 18);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_nick_name;
    }

    @Override
    public void findViews() {
        mEt = findView(R.id.profile_et_name);
    }

    @Override
    public void setViews() {
        super.setViews();

        ViewUtil.limitInputCount(mEt, mLimit);
        mEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    String str1 = "";
                    for (int i = 0; i < str.length; i++) {
                        str1 += str[i];
                    }
                    mEt.setText(str1);
                    mEt.setSelection(start);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    protected EditText getEt() {
        return mEt;
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(UserAPI.modify().nickName(mEt.getText().toString()).build());
    }
}
