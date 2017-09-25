package yaya.csp.ui.activity.me.profile;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import inject.annotation.router.Route;
import lib.ys.util.TextUtil;
import yaya.csp.R;

/**
 * 个人简介
 *
 * @auther Huoxuyu
 * @since 2017/9/21
 */
@Route
public class IntroActivity extends BaseMyMessageActivity{

    private final int KTextLength = 600;

    private EditText mEt;
    private TextView mTv;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_intro;
    }

    @Override
    public void findViews() {
        mEt = findView(R.id.intro_et);
        mTv = findView(R.id.intro_tv_num);
    }

    @Override
    public void setViews() {
        super.setViews();

        setLength(KTextLength - getVal().length());
        addTextChangeListener(mEt);

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
                    setLength(KTextLength);
                } else {
                    setLength(KTextLength - s.length());
                }
            }
        });
    }

    private void setLength(int length) {
        if (length > KTextLength) {
            length = 0;
        }
        mTv.setText(String.format(getString(R.string.my_message_intro_unit), length));
    }

    @Override
    protected EditText getEt() {
        return mEt;
    }
}
