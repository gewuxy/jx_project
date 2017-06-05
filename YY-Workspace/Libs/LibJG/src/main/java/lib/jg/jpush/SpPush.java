package lib.jg.jpush;

import android.content.Context;

import java.util.Observable;

import lib.ys.AppEx;
import lib.ys.util.sp.SpBase;

/**
 * @author yuansui
 */
public class SpPush extends SpBase {

    private static final String KSpFileName = "sp_push";

    public static final String KKeyAlias = "alias";

    private static SpPush mInst;

    private SpPush(Context context, String fileName) {
        super(context, fileName);
    }

    public static SpPush inst() {
        if (mInst == null) {
            mInst = new SpPush(AppEx.ct(), KSpFileName);
        }
        return mInst;
    }

    @Override
    public void update(Observable observable, Object data) {

    }
}
