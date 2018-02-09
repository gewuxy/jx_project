package jx.csp.constant;

import lib.jx.BaseConstants;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public interface Constants extends BaseConstants {

    int KAccountFrozen = 103; // 冻结账号的返回码
    int KPhotoMax = 9; // 照片选择的最大数

    String KDesKey = "2b3e2d604fab436eb7171de397aee892"; // DES秘钥

    interface PageConstants {
        int KPage = 1;  // 起始页页数
        int KPageSize = 20;  //分页加载时 每页加载的条目数
    }

    String KAppTypeCn = "cn";
    String KData = "data";
}
