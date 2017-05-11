package yy.doctor.model.exam;

import lib.ys.model.EVal;
import yy.doctor.model.exam.Details.TDetails;

/**
 * 微课具体明细信息
 *
 * @author : GuoXuan
 * @since : 2017/5/9
 */

class Details extends EVal<TDetails> {
    public enum TDetails {
        audioUrl,//微课明细路径
        id,//微课明细ID
        imgUrl,//微课明细路径
        sort,//微课明细序号
    }
}