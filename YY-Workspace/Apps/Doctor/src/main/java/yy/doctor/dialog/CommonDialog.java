package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.dialog.DialogEx;
import lib.ys.fitter.LayoutFitter;
import lib.ys.util.view.LayoutUtil;
import yy.doctor.R;

/**
 * @author yuansui
 * @since 2017/4/13
 */
public class CommonDialog extends DialogEx {

    private LinearLayout mLayoutContent;

    public CommonDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void initData() {
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
    public void setViewsValue() {
    }

    public void addItem(CharSequence text, OnClickListener l) {
        View v = getLayoutInflater().inflate(R.layout.layout_dialog_common_item, null);

        TextView tv = (TextView) v.findViewById(R.id.dialog_common_tv);
        tv.setText(text);

        v.setOnClickListener(l);

        LayoutFitter.fit(v);

        mLayoutContent.addView(v, LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
    }
}
