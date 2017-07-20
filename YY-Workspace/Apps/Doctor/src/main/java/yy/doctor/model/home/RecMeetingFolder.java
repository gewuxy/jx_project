package yy.doctor.model.home;

import lib.ys.model.EVal;
import yy.doctor.model.home.RecMeetingFolder.TRecMeetingFolder;

/**
 * @author CaiXiang
 * @since 2017/7/19
 */

public class RecMeetingFolder extends EVal<TRecMeetingFolder> implements IHome{

    @Override
    public int getType() {
        return HomeType.meeting_folder;
    }

    public enum TRecMeetingFolder {

    }

}
