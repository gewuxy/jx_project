package yy.doctor.model.home;

import lib.ys.model.EVal;
import yy.doctor.model.home.RecommendMeeting.TRecommendMeeting;

/**
 * Created by XPS on 2017/5/12.
 */

public class RecommendMeeting extends EVal<TRecommendMeeting> {

    public enum TRecommendMeeting {

        id,	//会议ID
        lecturer,	//主讲者
        lecturerTile,	//主讲者职位
        meetName,	//会议名称
        meetType,	//会议科室类型

        }

}
