package lib.ys.ui.interfaces.opts;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * @auther yuansui
 * @since 2017/5/8
 */

public interface ClickOpt {
    void setOnClickListener(@IdRes int resId);

    void setOnClickListener(@NonNull View v);

    void clearOnClickListener(@NonNull View v);
}
