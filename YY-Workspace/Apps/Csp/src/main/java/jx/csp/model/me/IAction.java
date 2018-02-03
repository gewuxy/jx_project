package jx.csp.model.me;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CaiXiang
 * @since 2018/2/3
 */

public interface IAction {

    @IntDef({
            ActionType.guide,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ActionType {
        int guide = 0;
    }

    @ActionType
    int getActionType();
}
