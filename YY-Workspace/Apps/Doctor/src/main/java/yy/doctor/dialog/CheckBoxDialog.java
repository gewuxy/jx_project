package yy.doctor.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.ys.fitter.Fitter;
import lib.ys.util.view.LayoutUtil;
import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * @author yuansui
 * @since 2017/4/13
 */
public class CheckBoxDialog extends BaseDialog {

    private LinearLayout mLayoutContent;


    public CheckBoxDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_check_box;
    }

    @Override
    public void findViews() {
        mLayoutContent = findView(R.id.dialog_check_box_layout_content);
    }

    @Override
    public void setViews() {
        setGravity(Gravity.BOTTOM);
    }

    public void addItem(CharSequence text, final OnClickListener l) {
        View v = getLayoutInflater().inflate(R.layout.layout_dialog_check_box_item, null);

        TextView tv = v.findViewById(R.id.dialog_check_box_item_tv);

        tv.setText(text);

        Fitter.view(v);

        mLayoutContent.addView(v, LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
    }

}
