package yy.doctor.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.fitter.Fitter;
import lib.ys.util.view.LayoutUtil;
import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * 普通的dialog
 *
 * @author yuansui
 * @since 2017/4/13
 */
public class CommonDialog extends BaseDialog {

    private LinearLayout mLayoutContent;

    public CommonDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_common;
    }

    @Override
    public void findViews() {
        mLayoutContent = findView(R.id.dialog_common_layout_content);
    }

    @Override
    public void setViews() {
    }

    public void addItem(CharSequence text, OnClickListener l) {
        View v = getLayoutInflater().inflate(R.layout.layout_dialog_common_item, null);

        TextView tv = v.findViewById(R.id.dialog_common_tv);
        tv.setText(text);

        v.setOnClickListener(l);

        Fitter.view(v);

        mLayoutContent.addView(v, LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
    }

}
