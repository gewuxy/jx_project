package jx.csp.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 会员套餐id
 *
 * @auther HuoXuYu
 * @since 2017/12/14
 */

@IntDef({
        VipType.norm,
        VipType.advanced,
        VipType.profession
})
@Retention(RetentionPolicy.SOURCE)
public @interface VipType {
    int norm = 1;       //标准版
    int advanced =2;    //高级版
    int profession = 3; //专业版
}
