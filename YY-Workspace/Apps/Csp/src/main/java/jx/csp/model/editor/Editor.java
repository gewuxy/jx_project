package jx.csp.model.editor;

import jx.csp.model.editor.Editor.TEditor;
import lib.ys.model.EVal;

/**
 * 编辑主题
 *
 * @auther HuoXuYu
 * @since 2018/2/1
 */

public class Editor extends EVal<TEditor> {

    public enum TEditor {
        /**
         * 主题列表
         */
        @Bind(asList = Theme.class)
        imageList,

        /**
         * 背景音乐列表
         */
        @Bind(asList = Music.class)
        musicList,
    }
}
