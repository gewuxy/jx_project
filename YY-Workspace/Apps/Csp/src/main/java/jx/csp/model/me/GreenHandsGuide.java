package jx.csp.model.me;

import jx.csp.model.me.GreenHandsGuide.TGreenHandsGuide;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/2/5
 */

public class GreenHandsGuide extends EVal<TGreenHandsGuide> {

    public enum TGreenHandsGuide {

        @Bind(GreenHandsGuideDetails.class)
        audioCourse,
    }
}
