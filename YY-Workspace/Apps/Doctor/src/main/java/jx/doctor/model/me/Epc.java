package jx.doctor.model.me;

import lib.ys.model.EVal;
import jx.doctor.model.me.Epc.TEpc;

/**
 * @author CaiXiang
 * @since 2017/5/6
 */
public class Epc extends EVal<TEpc> {

    public enum TEpc {

        id,  //商品id
        name,  //商品名字
        price,  //商品价格
        picture, //商品图片url

    }

}
