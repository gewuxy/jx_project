package jx.csp.model.editor;

import jx.csp.model.editor.Music.TMusic;
import lib.ys.model.EVal;

/**
 * @auther HuoXuYu
 * @since 2018/2/1
 */

public class Music extends EVal<TMusic>{

    public enum TMusic {
        id,
        name,
        duration,
        size,
        url
    }
}
