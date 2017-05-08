package lib.ys.ui.dialog;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;

import lib.ys.model.Screen;

/**
 * 自定义与屏幕同宽的dialog
 *
 * @author yuansui
 */
abstract public class MatchScreenDialog extends DialogEx {

    public MatchScreenDialog(Context context) {
        super(context);
    }

    @Override
    protected LayoutParams getParams() {
        return new LayoutParams(Screen.getWidth(), LayoutParams.WRAP_CONTENT);
    }
}
