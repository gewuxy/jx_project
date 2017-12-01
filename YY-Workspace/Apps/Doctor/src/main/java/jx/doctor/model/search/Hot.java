package jx.doctor.model.search;

/**
 * 热门搜索(单位号)
 *
 * @auther : GuoXuan
 * @since : 2017/6/15
 */

public class Hot implements IRec {

    @Override
    public int getRecType() {
        return RecType.hot;
    }
}
