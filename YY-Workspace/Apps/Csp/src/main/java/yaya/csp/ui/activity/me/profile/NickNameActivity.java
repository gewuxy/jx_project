package yaya.csp.ui.activity.me.profile;

import android.support.annotation.NonNull;
import android.widget.EditText;

import inject.annotation.router.Route;
import lib.ys.util.view.ViewUtil;
import yaya.csp.Extra;
import yaya.csp.R;

/**
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
        addTextChangeListener(mEt);
        ViewUtil.limitInputCount(mEt, mLimit);
    }

    @Override
    protected EditText getEt() {
        return mEt;
    }
}
