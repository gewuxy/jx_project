package yy.doctor.model;

import lib.ys.model.EVal;
import yy.doctor.model.Title.TTitle;

/**
 * @auther WangLan
 * @since 2017/7/25
 */

public class Title extends EVal<TTitle> {

    public enum TTitle {
        @Bind(asList = String.class)
        grade,

        @Bind(asList = String.class)
        title,
    }
}
