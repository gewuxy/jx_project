package yy.doctor.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;

import yy.doctor.R;

/**
 * 定位失败
 *
 * @auther yuansui
 * @since 2017/8/11
 */
public class LocateErrDialog extends HintDialog {

    public LocateErrDialog(Context context, OnClickListener l) {
        super(context);

        addHintView(LayoutInflater.from(context).inflate(R.layout.dialog_locate_fail, null));
        addBlueButton(R.string.cancel);
        addBlueButton("去设置", l);
    }

}
