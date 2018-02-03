package jx.csp.model.editor;

import jx.csp.model.editor.Theme.TTheme;
import lib.ys.model.EVal;

/**
 * @auther HuoXuYu
 * @since 2018/2/1
 */

public class Theme extends EVal<TTheme>{

    public enum TTheme {
        id,         // 主题id
        imgName,    //主题名称
        imgSize,    //主题大小
        imgUrl,     //主题地址

        select,
    }
}
