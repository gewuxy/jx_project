package jx.csp.model;

import jx.csp.model.BgMusic.TBgMusic;
import jx.csp.model.editor.Music;
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
