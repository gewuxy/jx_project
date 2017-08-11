package yy.doctor.dialog;

import android.content.Context;
import android.view.LayoutInflater;

import lib.ys.action.IntentAction;
import yy.doctor.R;

/**
 * 定位失败
 *
 * @auther yuansui
 * @since 2017/8/11
 */
public class LocateErrDialog extends HintDialog {

    public LocateErrDialog(Context context) {
        super(context);

        addHintView(LayoutInflater.from(context).inflate(R.layout.dialog_locate_fail, null));
        addButton("取消", v -> dismiss());
        addButton("去设置", v -> {
            IntentAction.appSetup().launch();
            dismiss();
        });
    }
}
