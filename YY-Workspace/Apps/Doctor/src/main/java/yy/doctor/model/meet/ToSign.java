package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.ToSign.TToSign;

/**
 * 签到信息
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class ToSign extends EVal<TToSign> {
    public enum TToSign {
        id,//签到ID
        meetId,//会议ID
        moduleId,//模块ID
        positionLat,//位置维度
        positionLng,//位置经度
        positionName,//位置名称
    }
}
