package yy.doctor.model.me;

import lib.ys.model.EVal;
import yy.doctor.model.me.EpnDetails.TEpnDetails;

/**
 * @author CaiXiang
 * @since 2017/5/5
 */
public class EpnDetails extends EVal<TEpnDetails>{

    public enum TEpnDetails {

        cost,
        costTime,
        oppositeName,
        description,
        type,

    }

}
