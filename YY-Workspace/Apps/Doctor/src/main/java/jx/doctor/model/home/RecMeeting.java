package jx.doctor.model.home;

import jx.doctor.model.meet.Meeting;

/**
 * Created by CaiXiang on 2017/5/12.
 */

public class RecMeeting extends Meeting implements IHome {

    @Override
    public int getHomeType() {
        return getInt(TMeeting.type) == MeetType.meet ? HomeType.meeting : HomeType.meeting_folder;
    }

}
