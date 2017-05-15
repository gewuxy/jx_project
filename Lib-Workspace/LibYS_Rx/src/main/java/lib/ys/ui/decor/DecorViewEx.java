package lib.ys.ui.decor;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.AppEx;
import lib.ys.R;
import lib.ys.config.AppConfig;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.impl.ErrorDecorImpl;
import lib.ys.ui.dialog.DialogEx;
import lib.ys.ui.interfaces.listener.OnRetryClickListener;
import lib.ys.ui.other.NavBar;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;

public class DecorViewEx extends RelativeLayout {

    private static final String TAG = DecorViewEx.class.getSimpleName();

    private static final int MATCH_PARENT = LayoutParams.MATCH_PARENT;
    private static final int WRAP_CONTENT = LayoutParams.WRAP_CONTENT;

    private NavBar mNavBar;
    private ErrorDecorEx mErrorDecor;

    private LoadingDecorEx mLoadingDecor;
    private DialogEx mLoadingDialog;

    /**
     * 总体布局, 上中下
     */
    private View mHeaderView;
    private View mContentView;
    private View mFooterView;


    @IntDef({
            ViewState.normal,
            ViewState.loading,
            ViewState.error,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewState {
        int normal = 0; // 正常
        int loading = 1; // 加载中
        int error = 2; // 有错误
    }

    @ViewState
    private int mViewState = ViewState.normal;

    public enum TNavBarState {
        linear, // 和界面平级
        above, // 在界面上层
    }

    private TNavBarState mNavBarState = null;

    @RefreshWay
    private int mInitRefreshWay = RefreshWay.un_know;

    public DecorViewEx(Context context, TNavBarState state, @RefreshWay int initRefreshWay, DialogEx loadingDialog) {
        super(context);

        mNavBarState = state;
        mInitRefreshWay = initRefreshWay;
        mLoadingDialog = loadingDialog;

        init(context);
    }

    private void init(Context context) {
        setLayoutParams(LayoutUtil.getRelativeParams(MATCH_PARENT, MATCH_PARENT));

        /**
         * 背景色
         */
        AppConfig c = AppEx.getConfig();
        if (c.getBgRes() != 0) {
            setBackgroundResource(c.getBgRes());
        } else {
            if (c.getBgColorRes() != 0) {
                setBackgroundColor(ResLoader.getColor(c.getBgColorRes()));
            }
        }

        // 添加titleBar
        mNavBar = new NavBar(context);
        LayoutParams params = LayoutUtil.getRelativeParams(MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mNavBar, params);
        mNavBar.gone();
    }

    public void setContentView(@LayoutRes int layoutResId, @LayoutRes int headerResId, @LayoutRes int footerResId) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        LayoutParams params = LayoutUtil.getRelativeParams(MATCH_PARENT, MATCH_PARENT);
        boolean isNavBarAbove;

		/*
         * 判断title bar的形式
		 */
        TNavBarState state;
        if (mNavBarState == null) {
            state = NavBar.getConfig().getState();
        } else {
            state = mNavBarState;
        }

        isNavBarAbove = state == TNavBarState.above;

        // 添加content header view
        if (headerResId != 0) {
            mHeaderView = inflater.inflate(headerResId, null);
            mHeaderView.setId(R.id.content_header_view);
            LayoutParams headerParams = LayoutUtil.getRelativeParams(MATCH_PARENT, WRAP_CONTENT);
            if (isNavBarAbove) {
                addView(mHeaderView, 0, headerParams);
            } else {
                headerParams.addRule(BELOW, mNavBar.getId());
                addView(mHeaderView, headerParams);
            }
            params.addRule(BELOW, mHeaderView.getId());
        } else {
            if (!isNavBarAbove) {
                params.addRule(BELOW, mNavBar.getId());
            }
        }

        // 添加contentView
        mContentView = inflater.inflate(layoutResId, null);
        if (footerResId != 0) {
            // 先判断有没有footer, 会影响到规则
            mFooterView = inflater.inflate(footerResId, null);
            mFooterView.setId(R.id.content_footer_view);
            params.addRule(ABOVE, mFooterView.getId());
        }

        if (isNavBarAbove) {
            addView(mContentView, 0, params);
        } else {
            addView(mContentView, params);
        }

        // 添加完contentView以后再添加footer
        if (mFooterView != null) {
            LayoutParams footerParams = LayoutUtil.getRelativeParams(MATCH_PARENT, WRAP_CONTENT);
            // 这行不能加, 会影响规则导致报Circular dependencies的崩溃
//            footerParams.addRule(BELOW, mContentView.getId());
            footerParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
            addView(mFooterView, footerParams);
        }

        // 添加loadingView
        mLoadingDecor = new LoadingDecorEx(getContext(), mInitRefreshWay, mLoadingDialog);
        if (isNavBarAbove) {
            addView(mLoadingDecor, 0, params);
        } else {
            addView(mLoadingDecor, params);
        }
        goneView(mLoadingDecor);

        // 添加ErrorView
        mErrorDecor = ReflectionUtil.newInst(AppEx.getConfig().getErrorDecorClz(), getContext());
        if (mErrorDecor == null) {
            // 找不到, 使用默认的
            mErrorDecor = new ErrorDecorImpl(getContext());
        }

        if (isNavBarAbove) {
            addView(mErrorDecor, 0, params);
        } else {
            addView(mErrorDecor, params);
        }
        goneView(mErrorDecor);
    }

    public void setContentView(@LayoutRes int layoutResId) {
        setContentView(layoutResId, 0, 0);
    }

    public NavBar getNavBar() {
        return mNavBar;
    }

    public View getContentView() {
        return mContentView;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setViewState(@ViewState int state) {
        if (mViewState == state) {
            return;
        }

        mViewState = state;
        switch (mViewState) {
            case ViewState.normal: {
                showView(mContentView);

                goneView(mErrorDecor);
                goneView(mLoadingDecor);
            }
            break;
            case ViewState.loading: {
                showView(mLoadingDecor);

                goneView(mContentView);
                goneView(mErrorDecor);
            }
            break;
            case ViewState.error: {
                showView(mErrorDecor);

                goneView(mContentView);
                goneView(mLoadingDecor);
            }
            break;
            default:
                break;
        }
    }

    private void showView(View v) {
        ViewUtil.showView(v);
    }

    private void goneView(View v) {
        ViewUtil.goneView(v);
    }

    public void setOnRetryClickListener(OnRetryClickListener listener) {
        mErrorDecor.setOnRetryClickListener(listener);
    }

    @ViewState
    public int getViewState() {
        return mViewState;
    }

    public void showLoadingDialog() {
        mLoadingDecor.showDialog();
    }

    public void dismissLoadingDialog() {
        mLoadingDecor.dismissDialog();
    }
}
