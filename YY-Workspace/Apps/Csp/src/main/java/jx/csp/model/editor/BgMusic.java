package jx.csp.model.editor;

import jx.csp.model.editor.BgMusic.TBgMusic;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/2/6
 */

public class BgMusic extends EVal<TBgMusic> {

    public enum TBgMusic {

        @Bind(asList = Music.class)
        list,
    }

}
