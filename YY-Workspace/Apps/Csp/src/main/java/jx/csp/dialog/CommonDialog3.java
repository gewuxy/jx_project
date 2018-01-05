package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;

import jx.csp.R;

/**
 * 对话框
 * 2个提示语
 *
 * @auther ${HuoXuYu}
 * @since 2018/1/5
 */

public class CommonDialog3 extends CommonDialog{

    public CommonDialog3(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_account_freeze;
    }
}
