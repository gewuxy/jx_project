package lib.ys.activity;

import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.EditText;

import lib.network.NetworkExecutor;
import lib.network.error.ConnectionNetError;
import lib.network.error.NetError;
import lib.network.model.NetworkRequest;
import lib.network.model.NetworkResponse;
import lib.network.model.OnNetworkListener;
import lib.ys.AppEx;
import lib.ys.LogMgr;
import lib.ys.R;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.decor.DecorViewEx;
import lib.ys.decor.DecorViewEx.TNavBarState;
import lib.ys.decor.DecorViewEx.ViewState;
import lib.ys.dialog.DialogEx;
import lib.ys.ex.NavBar;
import lib.ys.fitter.DpFitter;
import lib.ys.fitter.LayoutFitter;
import lib.ys.inst.LoadingDialogInst;
import lib.ys.interfaces.IFitParams;
import lib.ys.interfaces.IInitialize;
import lib.ys.interfaces.INetwork;
import lib.ys.interfaces.IOption;
import lib.ys.interfaces.IRefresh;
import lib.ys.interfaces.OnRetryClickListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.stats.Stats;
import lib.ys.util.DeviceUtil;
import lib.ys.util.InjectUtil.IInjectView;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.UIUtil;
import lib.ys.util.UtilEx;
import lib.ys.util.permission.CheckTask;
import lib.ys.util.permission.OnPermissionListener;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionChecker;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.swipeBack.SwipeBackActivity;

abstract public class ActivityEx extends SwipeBackActivity implements IFitParams,
        IInitialize,
        INetwork,
        IOption,
        OnClickListener,
        OnRetryClickListener,
        IRefresh,
        OnPermissionListener,
        IInjectView {

    protected final String TAG = getClass().getSimpleName();

    /**
     * 两次按下退出
     */
    private boolean mEnableExit;
    private Handler mExitHandler;

    private DecorViewEx mDecorView;

    private boolean mInitComplete = false;

    private NetworkExecutor mNetworkExecutor;

    private boolean mEnableSwipeFinish;

    @RefreshWay
    private int mRefreshWay = getInitRefreshWay();


    protected void onCreate(Bundle savedInstanceState) {
        if (AppEx.getConfig().isFlatBarEnabled()) {
            UIUtil.setFlatBar(getWindow());
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSPARENT);
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        if (enableSwipeFinish() == null) {
            mEnableSwipeFinish = AppEx.getConfig().isSwipeFinishEnabled();
        } else {
            mEnableSwipeFinish = enableSwipeFinish();
        }
        getSwipeBackLayout().setEnableGesture(mEnableSwipeFinish);

        setOnRetryClickListener(this);

        startInAnim();
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

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        // 数据的初始化提前, 可以根据数据来装载不同的view id
        initData();

        mDecorView = new DecorViewEx(this, getNavBarState(), getInitRefreshWay(), initLoadingDialog());
        mDecorView.setContentView(layoutResID, getContentHeaderViewId(), getContentFooterViewId());

        super.setContentView(mDecorView);
        init();
    }

    private void init() {
        initNavBar(getNavBar());

        fit(mDecorView);

        findViews();
        setViews();

        mInitComplete = true;
    }

    /**
     * 获取titleBar的状态
     *
     * @return null表示使用整体设置
     */
    protected TNavBarState getNavBarState() {
        return null;
    }

    @NonNull
    @RefreshWay
    public int getInitRefreshWay() {
        return AppEx.getConfig().getInitRefreshWay();
    }

    /**
     * 是否允许使用手势退出
     *
     * @return 如果遵循app的整体设计, 返回null; 要使用单独的设置, 返回true or false
     */
    protected Boolean enableSwipeFinish() {
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        NetworkImageView.clearMemoryCache(this);
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mExitHandler != null) {
            mExitHandler.removeMessages(0);
            mExitHandler = null;
        }

        LayoutFitter.clearFitSet();

        if (mNetworkExecutor != null) {
            mNetworkExecutor.destroy();
            mNetworkExecutor = null;
        }
    }

    /**
     * http task part
     */
    @Override
    public void exeNetworkRequest(int id, NetworkRequest request) {
        exeNetworkRequest(id, request, this);
    }

    @Override
    public void exeNetworkRequest(int id, NetworkRequest request, OnNetworkListener listener) {
        if (isFinishing()) {
            return;
        }

        if (request == null) {
            onNetworkError(id, new NetError());
            return;
        }

        if (!DeviceUtil.isNetworkEnable()) {
            onNetworkError(id, new ConnectionNetError(getString(R.string.toast_network_disconnect)));
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

    /**
     * http task callback part
     */

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
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
        stopRefresh();
        showToast(error.getMessage());
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
    }

    protected NavBar getNavBar() {
        return mDecorView.getNavBar();
    }

    /**
     * 更改当前视图状态
     *
     * @param state
     */
    public void setViewState(@ViewState int state) {
        mDecorView.setViewState(state);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Stats.onActivityPause(this, TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Stats.onActivityResume(this, TAG);
    }

    @Override
    public void finish() {
        super.finish();

        startOutAnim();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (enableHideKeyboardWhenChangeFocus()) {
            /**
             * 网上的模式是判断单个的Et点击区域进行收起键盘的操作
             * 我使用的是失去焦点的判断
             */
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
//            if (v instanceof EditText) {
//                // 从et开始监听
//                v.setOnFocusChangeListener(mOnFocusChangeListener);
//            }

                if (isShouldHideKeyboard(v, ev)) {
                    KeyboardUtil.hideFromView(v);
                    v.clearFocus();
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(final View v, boolean hasFocus) {
            if (!hasFocus) {
                v.setOnFocusChangeListener(null);

                runOnUIThread(() -> {
                    View nextFocusV = getCurrentFocus();

                    if (nextFocusV instanceof EditText) {
                        // 如果下一个v也是EditText, 不必收回键盘, 同时设置监听
                        nextFocusV.setOnFocusChangeListener(mOnFocusChangeListener);
                    } else {
                        KeyboardUtil.hideFromView(v);
                    }
                });

            }
        }
    };

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 是否允许点击空白处收起键盘
     *
     * @return
     */
    protected boolean enableHideKeyboardWhenChangeFocus() {
        return false;
    }

    protected boolean enableExit() {
        if (mExitHandler == null) {
            mExitHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    mEnableExit = false;
                }
            };
        }

        boolean enable = mEnableExit;

        if (!mEnableExit) {
            mEnableExit = true;
            mExitHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            mExitHandler.removeMessages(0);
        }

        return enable;
    }

    /**
     * 开始切换动画
     *
     * @param enterAnim
     * @param exitAnim
     */
    protected void startActSwitchAnim(@AnimRes int enterAnim, @AnimRes int exitAnim) {
        UtilEx.startActAnim(this, enterAnim, exitAnim);
    }

    /**
     * 退出动画
     */
    protected void startOutAnim() {
        if (mEnableSwipeFinish) {
            startActSwitchAnim(R.anim.hold, R.anim.swipe_out);
        } else {
            startActSwitchAnim(R.anim.right_in, R.anim.right_out);
        }
    }

    /**
     * 进入动画
     */
    protected void startInAnim() {
        if (mEnableSwipeFinish) {
            startActSwitchAnim(R.anim.swipe_in, R.anim.hold);
        } else {
            startActSwitchAnim(R.anim.left_in, R.anim.left_out);
        }
    }

    /************************
     * 适配相关
     */
    @Override
    public int fitDp(float dp) {
        return DpFitter.dp(dp);
    }

    @Override
    public void fitAbsParamsPx(View v, int x, int y) {
        LayoutFitter.fitAbsParamsPx(v, x, y);
    }

    @Override
    public void fit(View v) {
        LayoutFitter.fit(v);
    }


    /**************************
     * Option相关
     **************************/

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
        startActivity(new Intent(this, clz));
    }

    @Override
    public void startActivityForResult(Class<?> clz, int requestCode) {
        startActivityForResult(new Intent(this, clz), requestCode);
    }

    @Override
    public void showToast(String content) {
        AppEx.showToast(content);
    }

    @Override
    public void showToast(@StringRes int... resId) {
        AppEx.showToast(resId);
    }

    protected void setBackgroundColor(@ColorInt int color) {
        mDecorView.setBackgroundColor(color);
    }

    protected void setBackgroundResource(@DrawableRes int resId) {
        mDecorView.setBackgroundResource(resId);
    }

    protected void setBackground(Drawable background) {
        ViewUtil.setBackground(mDecorView, background);
    }

    protected DecorViewEx getDecorView() {
        return mDecorView;
    }

    protected boolean initComplete() {
        return mInitComplete;
    }

    protected void setOnRetryClickListener(OnRetryClickListener listener) {
        mDecorView.setOnRetryClickListener(listener);
    }

    @ViewState
    protected int getViewState() {
        return mDecorView.getViewState();
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * 设置点击事件监听
     *
     * @param resId view的id
     */
    protected void setOnClickListener(@IdRes int resId) {
        setOnClickListener(findView(resId));
    }

    protected void setOnClickListener(@NonNull View v) {
        if (v != null) {
            v.setOnClickListener(this);
        }
    }

    protected void clearOnClickListener(@NonNull View v) {
        if (v != null) {
            v.setOnClickListener(null);
        }
    }

    @Override
    public boolean onRetryClick() {
        if (!DeviceUtil.isNetworkEnable()) {
            showToast(R.string.toast_network_disconnect);
            return true;
        }
        return false;
    }

    /**
     * 初始化loading dialog
     *
     * @return
     */
    protected DialogEx initLoadingDialog() {
        DialogEx dialog = new LoadingDialogInst(this);
        dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                stopRefresh();
                cancelAllNetworkRequest();
            }
        });
        return dialog;
    }

    protected void showLoadingDialog() {
        if (!isFinishing()) {
            mDecorView.showLoadingDialog();
        }
    }

    protected void dismissLoadingDialog() {
        if (!isFinishing()) {
            mDecorView.dismissLoadingDialog();
        }
    }

    /**
     * 重载一下, 因为直接从FragmentActivity里重载出来的话无具体参数名
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected Fragment findFragmentById(int id) {
        return getSupportFragmentManager().findFragmentById(id);
    }

    protected View inflate(@LayoutRes int resource) {
        return getLayoutInflater().inflate(resource, null);
    }

    protected View inflate(@LayoutRes int resource, @Nullable ViewGroup root) {
        return getLayoutInflater().inflate(resource, root);
    }

    protected void runOnUIThread(Runnable r, long delayMillis) {
        UtilEx.runOnUIThread(r, delayMillis);
    }

    protected void runOnUIThread(Runnable r) {
        // 考虑到activity的切换动画时间, 加入延迟
        UtilEx.runOnUIThread(r, ResLoader.getInteger(R.integer.anim_default_duration));
    }

    /***********************************************************
     * 统一管理刷新方法
     */

    /**
     * 改变刷新形式, 不推荐使用, 会导致状态错乱
     * 暂时只给手动下拉刷新的回调使用
     *
     * @param way
     */
    public void setRefreshWay(@RefreshWay int way) {
        mRefreshWay = way;
    }

    @Override
    public void refresh(@RefreshWay int way) {
        mRefreshWay = way;

        switch (way) {
            case RefreshWay.dialog: {
                dialogRefresh();
            }
            break;
            case RefreshWay.embed: {
                embedRefresh();
            }
            break;
            case RefreshWay.swipe: {
                swipeRefresh();
            }
            break;
        }
    }

    @Override
    public void dialogRefresh() {
        showLoadingDialog();
    }

    @Override
    public void embedRefresh() {
        setViewState(ViewState.loading);
    }

    @Override
    public void swipeRefresh() {
    }

    @Override
    public void stopRefresh() {
        switch (mRefreshWay) {
            case RefreshWay.dialog: {
                stopDialogRefresh();
            }
            break;
            case RefreshWay.embed: {
                stopEmbedRefresh();
            }
            break;
            case RefreshWay.swipe: {
                stopSwipeRefresh();
            }
            break;
        }
    }

    @Override
    public void stopDialogRefresh() {
        dismissLoadingDialog();
    }

    @Override
    public void stopEmbedRefresh() {
    }

    @Override
    public void stopSwipeRefresh() {
    }

    @RefreshWay
    public int getRefreshWay() {
        return mRefreshWay;
    }

    /***********************************
     * 有关DecorView的ViewTreeObserver的操作
     */

    /**
     * ViewTreeObserver是否Alive
     *
     * @return
     */
    protected boolean isViewTreeObserverAlive() {
        return mDecorView.getViewTreeObserver().isAlive();
    }

    protected void startService(Class<? extends Service> clz) {
        startService(new Intent(this, clz));
    }

    protected ViewTreeObserver getViewTreeObserver() {
        return mDecorView.getViewTreeObserver();
    }

    protected boolean addOnPreDrawListener(OnPreDrawListener listener) {
        if (isViewTreeObserverAlive()) {
            getViewTreeObserver().addOnPreDrawListener(listener);
            return true;
        }
        return false;
    }

    protected void removeOnPreDrawListener(OnPreDrawListener listener) {
        if (getViewTreeObserver().isAlive()) {
            getViewTreeObserver().removeOnPreDrawListener(listener);
        }
    }

    protected boolean addOnGlobalLayoutListener(OnGlobalLayoutListener listener) {
        if (isViewTreeObserverAlive()) {
            getViewTreeObserver().addOnGlobalLayoutListener(listener);
            return true;
        }
        return false;
    }

    protected void removeOnGlobalLayoutListener(OnGlobalLayoutListener listener) {
        if (getViewTreeObserver().isAlive()) {
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                getViewTreeObserver().removeOnGlobalLayoutListener(listener);
            } else {
                getViewTreeObserver().removeGlobalOnLayoutListener(listener);
            }
        }
    }

    /******************************
     * 以下是permission相关
     */

    /**
     * 检查权限
     *
     * @param ps
     * @param code
     * @return
     */
    protected boolean checkPermission(int code, @Permission String... ps) {
        if (PermissionChecker.allow(this, ps)) {
            return true;
        }

        CheckTask.Builder builder = new CheckTask.Builder()
                .permissions(ps)
                .host(this)
                .code(code)
                .listener(this);
        return PermissionChecker.inst().check(builder.build());
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int code = 0;
        if (requestCode < 65536) {
            // 表示为activity自身发出的请求
            code = requestCode;
        } else {
            // 表示为fragment发出的请求
            code = requestCode & 0xffff;
        }
        PermissionChecker.inst().onRequestPermissionsResult(this, code, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
    }

    /**
     * @param id
     * @return
     * @deprecated user {@link #findView(int)} )} instead
     */
    @Override
    public View findViewById(int id) {
        return super.findViewById(id);
    }

    @Override
    public <T extends View> T findView(int id) {
        return (T) super.findViewById(id);
    }
}
