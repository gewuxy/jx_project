package jx.csp.model.me;

import jx.csp.ui.Royalty;
import lib.ys.model.EVal;

/**
 * @auther : GuoXuan
 * @since : 2018/3/13
 */
public class ExtractSelectInfo extends EVal<ExtractSelectInfo.TExtractSelectInfo> {

    public enum TExtractSelectInfo {
        severTime,

        @Bind(asList = Royalty.class)
        list,
    }

}
