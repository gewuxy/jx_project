package jx.doctor.model.meet;

import lib.ys.model.EVal;
import jx.doctor.model.meet.Sign.TSign;

/**
 * 签到信息
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class Sign extends EVal<TSign> {
    public enum TSign {
        id,//签到ID
        meetId,//会议ID
        moduleId,//模块ID
        positionLat,//位置维度
        positionLng,//位置经度
        positionName,//位置名称
        finished,//
        signTime//
    }
}
