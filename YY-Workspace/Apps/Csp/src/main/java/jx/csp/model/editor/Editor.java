package jx.csp.model.editor;

import jx.csp.model.editor.Editor.TEditor;
import jx.csp.model.main.Meet;
import lib.ys.model.EVal;

/**
 * 编辑主题
 *
 * @auther HuoXuYu
 * @since 2018/2/1
 */

public class Editor extends EVal<TEditor> {

    public enum TEditor {

        @Bind(value = Meet.class)
        course,

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

        @Bind(value = Theme.class)
        theme,
    }
}
