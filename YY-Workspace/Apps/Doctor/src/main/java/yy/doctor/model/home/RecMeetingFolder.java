package yy.doctor.model.home;

import lib.ys.model.EVal;
import yy.doctor.model.home.RecMeetingFolder.TRecMeetingFolder;

/**
 * @author CaiXiang
 * @since 2017/7/19
 */

public class RecMeetingFolder extends EVal<TRecMeetingFolder> implements IHome {

    @Override
    public int getType() {
        return HomeType.meeting_folder;
    }

    public enum TRecMeetingFolder {
        headImg, // http://10.0.0.234/file/headimg/17062316273507625748.jpg
        id, // 2017072617081230
        infinityName, // YaYa-技术部

        @Bind(asList = Lecturer.class)
        lecturerList, //

        unitUserName, // 莆田卫生培训
        userId, // 287564
        meetCount, // 5
    }

}
