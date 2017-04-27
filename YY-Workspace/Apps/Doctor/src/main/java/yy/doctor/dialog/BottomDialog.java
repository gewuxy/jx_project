package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.fitter.LayoutFitter;
import lib.ys.util.view.LayoutUtil;
import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/19
 */
public class BottomDialog extends BaseDialog {

    private LinearLayout mLayoutContent;

    public BottomDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_bottom;
    }

    @Override
    public void findViews() {

        mLayoutContent=findView(R.id.dialog_bottom_layout_content);

    }

    @Override
    public void setViews() {

        setGravity(Gravity.BOTTOM);

    }

    public void addItem(CharSequence text, int color, OnClickListener l) {
        View v = getLayoutInflater().inflate(R.layout.layout_dialog_bottom_item, null);

        TextView tv = (TextView) v.findViewById(R.id.dialog_bottom_tv);
        tv.setText(text);
        tv.setTextColor(color);

        v.setOnClickListener(l);

        LayoutFitter.fit(v);

        mLayoutContent.addView(v, LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
    }


}
