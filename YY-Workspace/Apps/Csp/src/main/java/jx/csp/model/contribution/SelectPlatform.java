package jx.csp.model.contribution;

import jx.csp.model.contribution.SelectPlatform.TSelectPlatform;
import lib.ys.model.EVal;

/**
 * @author CaiXiang
 * @since 2018/3/13
 */

public class SelectPlatform extends EVal<TSelectPlatform> {

    public enum TSelectPlatform {
        id,  // 平台 id
        platformName,  // 平台名称
        imgUrl,  // 头像地址
        unitId,	 // 单位号id
    }
}
