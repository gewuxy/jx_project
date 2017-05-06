package lib.ys.util;

import lib.ys.ConstantsEx;
import lib.ys.fitter.DpFitter;

/**
 * 处理xml里带有属性的自定义view
 *
 * @author yuansui
 */
public class XmlAttrUtil {

    public static int convert(int px, int defaultDp) {
        if (px == ConstantsEx.KInvalidValue) {
            return DpFitter.dp(defaultDp);
        }

        if (px == 0) {
            return px;
        }
        return DpFitter.densityPx(px);
    }

    public static float convert(float px, int defaultDp) {
        if (px == ConstantsEx.KInvalidValue) {
            return DpFitter.dp(defaultDp);
        }

        if (px == 0) {
            return px;
        }
        return DpFitter.densityPx(px);
    }
}
