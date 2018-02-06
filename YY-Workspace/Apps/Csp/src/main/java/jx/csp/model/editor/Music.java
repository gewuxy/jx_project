package jx.csp.model.editor;

import jx.csp.model.editor.Music.TMusic;
import lib.ys.model.EVal;

/**
 * @auther HuoXuYu
 * @since 2018/2/1
 */

public class Music extends EVal<TMusic> {

    public enum TMusic {
        id, // 背景音乐id
        name, // 背景音乐名称
        duration, // 背景音乐时长
        size, // 背景音乐大小
        url,  // 背景音乐地址

        play, // 播放暂停
        select, // 本地字段,播放按钮的选择
    }
}
