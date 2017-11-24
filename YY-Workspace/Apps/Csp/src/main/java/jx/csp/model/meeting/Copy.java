package jx.csp.model.meeting;

import jx.csp.model.meeting.Copy.TCopy;
import lib.ys.model.EVal;

/**
 * @auther WangLan
 * @since 2017/9/26
 */
public class Copy extends EVal<TCopy> {

    public enum TCopy {
        oldId, // 原来会议的id
        id,  // 复制成功后返回的会议id
        title, // 复制成功后的会议标题
    }
}
