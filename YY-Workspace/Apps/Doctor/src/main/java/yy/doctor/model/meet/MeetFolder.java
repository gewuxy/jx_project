package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.MeetFolder.TMeetingFolder;

/**
 * @author GuoXuan
 * @since 2017/8/1
 */

public class MeetFolder extends EVal<TMeetingFolder> implements IMeet {

    @Override
    public int getMeetType() {
        return MeetType.folder;
    }

    public enum TMeetingFolder {
        hide, //  false,
        id, //  20170726170812301,
        infinityName, //  技术部-java后台开发组,
        leaf, //  true,
        meetCount, //  2,
        mountType, //  1,
        preid, //  2017072617081230,
        type, //  0,
        typeName, //  文件夹,
        userId, //  287564
    }

}
