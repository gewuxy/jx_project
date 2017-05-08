package lib.ys.ex;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout.LayoutParams;

import lib.network.NetworkExecutor;
import lib.network.error.ConnectionNetError;
import lib.network.error.NetError;
import lib.network.model.NetworkRequest;
import lib.network.model.NetworkResponse;
import lib.network.model.OnNetworkListener;
import lib.ys.AppEx;
import lib.ys.ConstantsEx;
import lib.ys.LogMgr;
import lib.ys.R;
import lib.ys.fitter.LayoutFitter;
import lib.ys.interfaces.IInitialize;
import lib.ys.interfaces.INetwork;
import lib.ys.interfaces.IOption;
import lib.ys.util.DeviceUtil;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.ViewUtil;


abstract public class PopupWindowEx implements
        IInitialize,
        INetwork,
        IOption,
        OnDismissListener,
        OnClickListener {

    protected final String TAG = getClass().getSimpleName();

    protected static final int MATCH_PARENT = LayoutParams.MATCH_PARENT;
    protected static final int WRAP_CONTENT = LayoutParams.WRAP_CONTENT;
    private static final float KDefaultDimAmount = 0.3f;

    private PopupWindow mPopupWindow;
    private OnDismissListener mOnDismissListener;

    private View mContentView;
    private Context mContext;
    private float mDimAmount = KDefaultDimAmount;

    private NetworkExecutor mNetworkExecutor;

    // 背景变暗
    private boolean mEnableDim = false;
    private PopupWindow mDimWindow;

    public PopupWindowEx(@NonNull Context context) {
        if (context == null) {
            throw new NullPointerException("context can not be null");
        }
        mContext = context;
        init();
    }

    private void init() {
        mPopupWindow = new PopupWindow(mContext);
        mContentView = LayoutInflater.from(mContext).inflate(getContentViewId(), null);
        LayoutFitter.fit(mContentView);

        mPopupWindow.setContentView(mContentView);
        mPopupWindow.setWidth(getWindowWidth());
        mPopupWindow.setHeight(getWindowHeight());
        mPopupWindow.setBackgroundDrawable(null);

        initData();
        findViews();
        setViews();

        mPopupWindow.setOnDismissListener(this);
    }

    @Override
    @Deprecated
    public final void initNavBar(NavBar bar) {
        // 没有nav bar
    }

    public <T extends View> T findView(@IdRes int id) {
        return (T) mContentView.findViewById(id);
    }

    @Nullable
    @Override
    public int getContentHeaderViewId() {
        return 0;
    }

    @Nullable
    @Override
    public int getContentFooterViewId() {
        return 0;
    }

    abstract public int getWindowWidth();

    abstract public int getWindowHeight();

    /**
     * 设置点击外部空白处是否自动消失
     *
     * @param enabled
     */
    public void setTouchOutsideDismissEnabled(boolean enabled) {
        setOutsideTouchable(enabled);
        setFocusable(enabled);
        if (enabled) {
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } else {
            mPopupWindow.setBackgroundDrawable(null);
        }
    }

    /**
     * 设置背景变暗的比例
     *
     * @param amount 0-1.0
     */
    private boolean setDimAmount(@FloatRange(from = 0, to = 1) float amount) {
        if (!mEnableDim) {
            return false;
        }

        if (mDimWindow != null) {
            int alpha = (int) (amount * ConstantsEx.KAlphaMax);
            int dimColor = Color.argb(alpha, 0, 0, 0);
            mDimWindow.setBackgroundDrawable(new ColorDrawable(dimColor));
        } else {
            mDimAmount = amount;
        }

        return true;
    }

    /**
     * 设置是否允许背景变暗, 默认不变
     *
     * @param enabled
     */
    public void setDimEnabled(boolean enabled) {
        setDimEnabled(enabled, mDimAmount);
    }

    /**
     * 设置是否允许背景变暗
     *
     * @param enabled
     * @param amount  变暗的比例
     */
    public void setDimEnabled(boolean enabled, @FloatRange(from = 0, to = 1) float amount) {
        mEnableDim = enabled;
        if (enabled) {
            setDimAmount(amount);
        } else {
            setDimAmount(0);
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) {
        return null;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        Exception e = error.getException();
        if (e != null) {
            LogMgr.d(TAG, "onNetworkError: id = " + id);
            LogMgr.d(TAG, "onNetworkError: e = " + e.getMessage());
            LogMgr.d(TAG, "onNetworkError: msg = " + error.getMessage());
            LogMgr.d(TAG, "onNetworkError: end=======================");
        } else {
            LogMgr.d(TAG, "onNetworkError(): " + "tag = [" + id + "], error = [" + error.getMessage() + "]");
        }
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
    }

    @Override
    public void exeNetworkRequest(int id, NetworkRequest request) {
        exeNetworkRequest(id, request, this);
    }

    public void exeNetworkRequest(int id, NetworkRequest request, OnNetworkListener listener) {
        if (!DeviceUtil.isNetworkEnable()) {
            onNetworkError(id, new ConnectionNetError(mContext.getString(R.string.toast_network_disconnect)));
            return;
        }

        if (mNetworkExecutor == null) {
            mNetworkExecutor = new NetworkExecutor(getClass().getName(), this);
        }
        mNetworkExecutor.execute(id, request, listener);
    }

    @Override
    public void cancelAllNetworkRequest() {
        if (mNetworkExecutor != null) {
            mNetworkExecutor.cancelAll();
        }
    }

    @Override
    public void cancelNetworkRequest(int id) {
        if (mNetworkExecutor != null) {
            mNetworkExecutor.cancel(id);
        }
    }

    @Override
    public void showView(View v) {
        ViewUtil.showView(v);
    }

    @Override
    public void hideView(View v) {
        ViewUtil.hideView(v);
    }

    @Override
    public void goneView(View v) {
        ViewUtil.goneView(v);
    }

    @Override
    public void startActivity(Class<?> clz) {
        LaunchUtil.startActivity(getContext(), clz);
    }

    @Override
    public void startActivity(Intent intent) {
        LaunchUtil.startActivity(getContext(), intent);
    }

    @Override
    public void startActivityForResult(Class<?> clz, int requestCode) {
        // 空实现
    }

    @Override
    public void showToast(String content) {
        AppEx.showToast(content);
    }

    @Override
    public void showToast(@StringRes int... resId) {
        AppEx.showToast(resId);
    }

    protected Context getContext() {
        return mContext;
    }

    protected LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }

    @Override
    public void onDismiss() {

        if (mNetworkExecutor != null) {
            mNetworkExecutor.cancelAll();
            mNetworkExecutor = null;
        }

        if (mEnableDim) {
            if (mDimWindow != null) {
                mDimWindow.dismiss();
            }
        }

        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }
    }

    public void setOnDismissListener(OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    public void setWidth(int width) {
        mPopupWindow.setWidth(width);
    }

    public void setHeight(int height) {
        mPopupWindow.setHeight(height);
    }

    public void setOutsideTouchable(boolean touchable) {
        mPopupWindow.setOutsideTouchable(touchable);
    }

    public void setFocusable(boolean focusable) {
        mPopupWindow.setFocusable(focusable);
    }

    public void setTouchable(boolean touchable) {
        mPopupWindow.setTouchable(touchable);
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public View getContentView() {
        return mPopupWindow.getContentView();
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    public void showAsDropDown(View anchor) {
        if (mEnableDim) {
            createDimWindow(anchor);
            mDimWindow.showAsDropDown(anchor);
        }
        mPopupWindow.showAsDropDown(anchor);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (mEnableDim) {
            createDimWindow(anchor);
            mDimWindow.showAsDropDown(anchor, xoff, yoff);
        }
        mPopupWindow.showAsDropDown(anchor, xoff, yoff);
    }

    /**
     * @param parent  a parent view to get the
     *                {@link View#getWindowToken()} token from
     * @param gravity the gravity which controls the placement of the popup
     *                window
     * @param x       the popup's x location offset
     * @param y       the popup's y location offset
     */
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (mEnableDim) {
            createDimWindow(parent);
            mDimWindow.showAtLocation(parent, gravity, x, y);
        }
        mPopupWindow.showAtLocation(parent, gravity, x, y);
    }

    public void update(View anchor, int xoff, int yoff, int width, int height) {
        if (mEnableDim) {
            createDimWindow(anchor);
            mDimWindow.update(anchor, xoff, yoff, width, height);
        }
        mPopupWindow.update(anchor, xoff, yoff, width, height);
    }

    /**
     * 创建变暗的背景window
     */
    private void createDimWindow(View v) {
        if (mDimWindow == null) {
            mDimWindow = new PopupWindow(mContext);

            mDimWindow.setContentView(ViewUtil.inflateSpaceViewPx(1));
            mDimWindow.setWidth(getWindowWidth());
            mDimWindow.setHeight(mPopupWindow.getMaxAvailableHeight(v));
            mDimWindow.setFocusable(false);
            mDimWindow.setTouchable(false);

            setDimAmount(mDimAmount);
        }
    }

    @Override
    public void onClick(View v) {
    }
}
