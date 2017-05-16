package yy.doctor.model.me;

import lib.ys.model.EVal;
import yy.doctor.model.me.EpcDetail.TEpcDetail;

/**
 * @author CaiXiang
 * @since 2017/5/10
 */
public class EpcDetail extends EVal<TEpcDetail> {

    public enum TEpcDetail {

        id,    //商品id
        name,    //商品名字
        price,    //商品价格
        picture,    //商品图片url
        description,    //商品描述
        buyLimit,    //限购数

    }

}
