package yaya.csp.sp;

import android.content.Context;

import java.util.Observable;

import lib.ys.util.sp.SpBase;
import yaya.csp.App;

/**
 * @auther yuansui
 * @since 2017/5/4
 */

public class SpUser extends SpBase {

    private static final String KFileName = "sp_user";

    private static SpUser mInst = null;

    private SpUser(Context context, String fileName) {
        super(context, fileName);
    }

    public synchronized static SpUser inst() {
        if (mInst == null) {
            mInst = new SpUser(App.getContext(), KFileName);
        }
        return mInst;
    }

    @Override
    public void update(Observable o, Object arg) {
        mInst = null;
    }

}
