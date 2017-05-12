package yy.doctor.model.meet;

import lib.ys.model.EVal;
import yy.doctor.model.meet.SignResult.TSignResult;

/**
 * 签到结果信息
 *
 * @author : GuoXuan
 * @since : 2017/5/8
 */

public class SignResult extends EVal<TSignResult> {
    public enum TSignResult {
        signTime,//签到时间
    }
}
