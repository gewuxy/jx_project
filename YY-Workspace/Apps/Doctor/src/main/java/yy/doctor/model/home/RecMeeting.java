package yy.doctor.model.home;

import lib.ys.model.EVal;
import yy.doctor.model.home.RecMeeting.TRecMeeting;

/**
 * Created by XPS on 2017/5/12.
 */

public class RecMeeting extends EVal<TRecMeeting> implements IHome {

    @Override
    public int getType() {
        return HomeType.meeting;
    }

    public enum TRecMeeting {

        id,	//会议ID
        lecturer,	//主讲者
        lecturerTile,	//主讲者职位
        meetName,	//会议名称
        meetType,	//会议科室类型
        endTime,
        startTime,
        pubUserHead,
        lecturerImg

        }

}
