package jx.csp.ui.activity.main;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.model.meeting.Code;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ConstantsEx;
import lib.ys.config.AppConfig;
import lib.ys.network.image.ImageInfo;
import lib.ys.network.image.NetworkImageListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx;
import lib.ys.ui.other.NavBar;

/**
 * 星评界面
 *
 * @auther : GuoXuan
 * @since : 2018/1/17
 */
@Route
public class StartActivity extends BaseActivity {

    @Arg(opt = true)
    boolean mReb; // 是否是录播

    @Arg(opt = true)
    boolean mStart; // 是否有星评

    @Arg
    String mCourseId;

    @Arg
    long mStartTime;

    private TextView mTvFinish;
    private TextView mTvAll;
    private View mLayoutDefault;
    private View mLayoutDataMatrix;
    private NetworkImageView mIvDataMatrix;

    @Override
    public void initData() {
        // do nothing
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_start;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addBackIcon(R.drawable.default_ic_close, this);
        bar.addTextViewMid(mStart ? R.string.start_comment : R.string.start_finish);
        bar.addViewRight(R.drawable.share_ic_share, v -> {
            // fixme:
//            ShareDialog d = new ShareDialog(StartActivity.this, mCourseId, "", "");
//            d.show();
        });

        Util.addDivider(bar);
    }

    @Override
    public void findViews() {
        mLayoutDefault = findView(R.id.start_layout_meet_default);
        mLayoutDataMatrix = findView(R.id.start_layout_data_matrix);
        mIvDataMatrix = findView(R.id.start_iv_data_matrix);
        mTvFinish = findView(R.id.start_tv_finish);
        mTvAll = findView(R.id.start_tv_all_time);
    }

    @Override
    public void setViews() {
        mTvFinish.setText(mReb ? R.string.reb_finish : R.string.live_finish);

        refresh(AppConfig.RefreshWay.embed);
        getDataFromNet();
    }

    private void getDataFromNet() {
        exeNetworkReq(NetworkApiDescriptor.MeetingAPI.code(mCourseId).build());
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(AppConfig.RefreshWay.embed);
            getDataFromNet();
        }
        return true;
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Code.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            Code c = (Code) r.getData();
            if (c == null) {
                return;
            }
            long serverTime = c.getLong(Code.TCode.serverTime);
            if (serverTime > mStartTime) {
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        mTvAll.setText(formatTime(serverTime - mStartTime));
                        removeOnGlobalLayoutListener(this);
                    }

                });
            }
            boolean startStatus = c.getBoolean(Code.TCode.starStatus);
            if (startStatus) {
                mIvDataMatrix.url(c.getString(Code.TCode.startCodeUrl))
                        .listener(new NetworkImageListener() {

                            @Override
                            public void onImageSet(ImageInfo info) {
                                startState(true);
                            }

                            @Override
                            public void onFailure() {
                                startState(false);
                            }
                        })
                        .load();
            } else {
                startState(false);
            }

        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        setViewState(DecorViewEx.ViewState.error);
    }

    /**
     * 格式化时间
     *
     * @param time 时间
     * @return x'xx"
     */
    private String formatTime(long time) {
        long min = time / TimeUnit.MINUTES.toMillis(1);
        long sec = time % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        return new StringBuffer()
                .append(min)
                .append("'")
                .append(sec > 9 ? ConstantsEx.KEmpty : "0")
                .append(sec)
                .append("\"")
                .toString();
    }

    /**
     * 星评关闭
     *
     * @param flag true 展示二维码, false 展示会讲制作
     */
    private void startState(boolean flag) {
        stopRefresh();
        setViewState(DecorViewEx.ViewState.normal);
        if (flag) {
            goneView(mLayoutDefault);
            showView(mLayoutDataMatrix);
        } else {
            showView(mLayoutDefault);
            goneView(mLayoutDataMatrix);
        }
    }
}
