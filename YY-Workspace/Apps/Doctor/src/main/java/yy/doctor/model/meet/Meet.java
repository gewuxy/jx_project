package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.Meet.TMeet;

/**
 * @auther : GuoXuan
 * @since : 2017/8/1
 */

public class Meet extends EVal<TMeet> {

    public enum TMeet {
        @Bind(asList = MeetFolder.class)
        infinityTreeList, // 文件夹

        @Bind(asList = Meeting.class)
        meetInfoDTOList, // 会议
    }

}
