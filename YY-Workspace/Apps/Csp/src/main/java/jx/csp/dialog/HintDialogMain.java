package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.TextView;

import jx.csp.R;


/**
 * 对话框
 * 提示语只有一种颜色的
 *
 * @author : GuoXuan
 * @since : 2017/5/23
 */
public class HintDialogMain extends HintDialog {

    private TextView mTvHint; // 提示语

    public HintDialogMain(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_hint_main;
    }

    @Override
    public void findViews() {
        super.findViews();
        mTvHint = findView(R.id.dialog_main_tv_hint);
    }

    public void setHint(CharSequence hint) {
        mTvHint.setText(hint);
    }

    public void setHint(@StringRes int id) {
        mTvHint.setText(id);
    }

}
