package lib.ys.spannable;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

/**
 * 去掉下划线的点击span
 *
 * @author yuansui
 */
abstract public class NoLineClickSpan extends ClickableSpan {

    @Override
    public void updateDrawState(TextPaint ds) {
        /**set textColor**/
        ds.setColor(ds.linkColor);
        /**Remove the underline**/
        ds.setUnderlineText(false);
    }
}
