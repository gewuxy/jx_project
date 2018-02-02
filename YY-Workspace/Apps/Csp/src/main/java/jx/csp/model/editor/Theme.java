package jx.csp.model.editor;

import jx.csp.model.editor.Theme.TTheme;
import lib.ys.model.EVal;

/**
 * @auther HuoXuYu
 * @since 2018/2/1
 */

public class Theme extends EVal<TTheme>{

    public enum TTheme {
        id,
        imgName,
        imgSize,
        imgUrl,

        select,
    }
}
