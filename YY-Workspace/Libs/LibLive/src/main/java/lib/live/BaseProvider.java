package lib.live;

import android.content.Context;

/**
 * @auther : GuoXuan
 * @since : 2017/12/11
 */
abstract public class BaseProvider implements IProvider {

    public final String TAG = getClass().getSimpleName();

    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    public BaseProvider(Context context) {
        mContext = context;
    }

}
