package yy.doctor.model.data;

import lib.ys.model.EVal;
import yy.doctor.model.data.Thomson.TThomson;

/**
 * @author CaiXiang
 * @since 2017/5/26
 */

public class Thomson extends EVal<TThomson> {

    public enum TThomson {
        id,    // 栏目id
        name,    // 栏目名字
        leaf,    // 是否是叶子节点	如果为true 则下一级为数据 否则为栏目
    }
}
