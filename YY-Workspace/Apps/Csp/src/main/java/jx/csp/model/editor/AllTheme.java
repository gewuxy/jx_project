package jx.csp.model.editor;

import jx.csp.model.editor.AllTheme.TAllTheme;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/2/7
 */

public class AllTheme extends EVal<TAllTheme> {

    public enum TAllTheme {

        @Bind(asList = Theme.class)
        list,
    }
}
