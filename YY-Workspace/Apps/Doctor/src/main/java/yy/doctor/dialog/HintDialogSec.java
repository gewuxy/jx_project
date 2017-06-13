package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import yy.doctor.R;

/**
 *
 *
 * @author : GuoXuan
 * @since : 2017/5/22
 */
public class HintDialogSec extends BaseHintDialog {

    private TextView mTvMainHint; // 主提示
    private TextView mTvSecHint; // 副提示

    public HintDialogSec(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_hint_sec;
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvMainHint = findView(R.id.dialog_sec_tv_main);
        mTvSecHint = findView(R.id.dialog_sec_tv_sec);
    }

    public void setMainHint(String hintMain) {
        mTvMainHint.setText(hintMain);
    }

    public void setSecHint(String hintSec) {
        mTvSecHint.setText(hintSec);
    }
}
