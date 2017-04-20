package lib.ys.util;

import android.widget.Toast;

import lib.ys.AppEx;
import lib.ys.util.res.ResLoader;

public class ToastUtil {

    private static Toast mToastKeeper;
    private static String mContent = "";

    public static void makeToast(String content) {
        if (content == null) {
            return;
        }

        if (mToastKeeper == null || !mContent.equals(content)) {
            mContent = content;
            mToastKeeper = Toast.makeText(AppEx.ct(), content, Toast.LENGTH_SHORT);
        }

        mToastKeeper.show();
    }

    public static void makeToast(String content, int duration) {
        if (content == null) {
            return;
        }

        if (mToastKeeper == null || !mContent.equals(content)) {
            mContent = content;
            mToastKeeper = Toast.makeText(AppEx.ct(), content, duration);
        }
        mToastKeeper.show();
    }

    public static void makeToast(int strResId) {
        if (strResId == 0) {
            return;
        }

        String content = ResLoader.getString(strResId);
        if (mToastKeeper == null || !mContent.equals(content)) {
            mContent = content;
            mToastKeeper = Toast.makeText(AppEx.ct(), mContent, Toast.LENGTH_SHORT);
        }
        mToastKeeper.show();
    }
}
