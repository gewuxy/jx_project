package jx.csp.model.me;

import jx.csp.model.me.GreenHandsGuideDetails.TGreenHandsGuideDetails;
import jx.csp.model.meeting.CourseDetail;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/2/5
 */

public class GreenHandsGuideDetails extends EVal<TGreenHandsGuideDetails> {

    public enum TGreenHandsGuideDetails {

        id,  //	会议id
        title,  //	会议标题

        @Bind(asList = CourseDetail.class)
        details,
    }

}
