package jx.csp.ui.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import java.util.List;

import jx.csp.R;
import jx.csp.constant.LangType;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Copy;
import jx.csp.model.meeting.Copy.TCopy;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.login.AuthLoginActivity;
import jx.csp.ui.activity.login.AuthLoginEnActivity;
import jx.csp.ui.activity.me.MeActivity;
import jx.csp.ui.frag.main.MeetGridFrag;
import jx.csp.ui.frag.main.MeetSingleFrag;
import jx.csp.ui.frag.main.MeetVpFrag;
import jx.csp.util.Util;
import lib.jg.jpush.SpJPush;
import lib.ys.YSLog;
import lib.ys.impl.SingletonImpl;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.yy.notify.LiveNotifier;
import lib.yy.notify.LiveNotifier.LiveNotifyType;
import lib.yy.notify.LiveNotifier.OnLiveNotify;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseVpActivity;

/**
 * 首页
 *
 * @auther WangLan
 * @since 2017/9/30
 */

public class MainActivity extends BaseVpActivity implements OnLiveNotify {

    private final int KPageGrid = 0;
    private final int KPageVp = 1;

    private ImageView mIvShift;

    private MeetGridFrag mGridFrag;
    private MeetVpFrag mVpFrag;

    @Override
    public void initData(Bundle state) {
        LiveNotifier.inst().add(this);
        if (state != null) {
            return;
        }

        mGridFrag = new MeetGridFrag();
        mVpFrag = new MeetVpFrag();

        mGridFrag.setListener(data -> gridFragListener(data));
        add(mGridFrag);
        add(mVpFrag);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initNavBar(NavBar bar) {
        View view = inflate(R.layout.layout_main_user);
        NetworkImageView iv = view.findViewById(R.id.main_ic_user);
        iv.placeHolder(R.drawable.ic_default_user_header)
                .url(Profile.inst().getString(TProfile.avatar))
                .load();
        bar.addViewLeft(view, v -> startActivity(MeActivity.class));

        bar.addTextViewMid(getString(R.string.CSPmeeting));

        ViewGroup group = bar.addViewRight(R.drawable.main_shift_selector, v -> {
            boolean flag = getCurrentItem() == KPageGrid;
            mIvShift.setSelected(!mIvShift.isSelected());
            if (!flag) {
                // 网格
                setCurrentItem(KPageGrid, false);
                mGridFrag.setPosition(mVpFrag.getPosition());
                SpUser.inst().saveMainAcPage(KPageGrid);
            } else {
                // 列表
                setCurrentItem(KPageVp, false);
                mVpFrag.setPosition(mGridFrag.getPosition());
                SpUser.inst().saveMainAcPage(KPageVp);
            }
        });
        mIvShift = Util.getBarView(group, ImageView.class);
        mIvShift.setSelected(false);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.main_scan);

        //不能左右滑动
        setScrollable(false);

        // 判断是否已经绑定极光推送
        YSLog.d(TAG, " 是否重新绑定极光推送 " + SpJPush.inst().needRegisterJP());
        YSLog.d(TAG, " 保存的RegistrationId = " + SpJPush.inst().registerId());
        if (SpJPush.inst().needRegisterJP() && !TextUtil.isEmpty(SpJPush.inst().registerId())) {
            CommonServRouter.create(ReqType.j_push)
                    .jPushRegisterId(SpJPush.inst().registerId())
                    .route(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (checkPermission(0, Permission.camera)) {
            startActivity(ScanActivity.class);
        }
    }

    @Override
    public void onLiveNotify(@LiveNotifyType int type, Object data) {
        int position = getCurrentItem();
        Fragment f = getItem(position);
        switch (type) {
            case LiveNotifyType.accept: {
                if (f instanceof MeetGridFrag) {
                    MeetGridFrag frag = (MeetGridFrag) f;
                    frag.enter();
                } else if (f instanceof MeetVpFrag) {
                    MeetVpFrag frag = (MeetVpFrag) f;
                    MeetSingleFrag item = (MeetSingleFrag) frag.getItem();
                    item.enter();
                }
            }
            break;
            case LiveNotifyType.reject: {
                if (f instanceof MeetGridFrag) {
                    MeetGridFrag frag = (MeetGridFrag) f;
                    frag.noEnter();
                } else if (f instanceof MeetVpFrag) {
                    MeetVpFrag frag = (MeetVpFrag) f;
                    MeetSingleFrag item = (MeetSingleFrag) frag.getItem();
                    item.noEnter();
                }
            }
            break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.logout) {
            finish();
        } else if (type == NotifyType.token_out_of_date) {
            Intent intent;
            //清除栈里的activity2
            if (SpApp.inst().getLangType() != LangType.en) {
                intent = new Intent(this, AuthLoginActivity.class);
            } else {
                intent = new Intent(this, AuthLoginEnActivity.class);
            }
            startActivity(intent);
            finish();
        } else if (type == NotifyType.delete_meeting) {
            String str = (String) data;
            YSLog.d(TAG, str + "删除接收通知");
            for (Meet meet : mGridFrag.getData()) {
                if (Integer.valueOf(str) == meet.getInt(TMeet.id)) {
                    mGridFrag.getData().remove(meet);
                    mGridFrag.invalidate();
                    break;
                }
            }
            showToast(R.string.delete_success);
        } else if (type == NotifyType.copy_duplicate) {
            Copy copy = (Copy) data;
            for (Meet meet : mGridFrag.getData()) {
                if (copy.getInt(TCopy.oldId) == meet.getInt(TMeet.id)) {
                    Meet m = (Meet) meet.clone();
                    m.put(TMeet.title, m.getString(TMeet.title) + getString(R.string.duplicate));
                    m.put(TMeet.id, copy.getInt(TCopy.id));
                    mGridFrag.addItem(m);
                    mGridFrag.invalidate();
                    break;
                }
            }
            showToast(R.string.copy_duplicate_success);
        }
    }

    @Override
    public void onBackPressed() {
        if (enableExit()) {
            super.onBackPressed();
        } else {
            showToast(R.string.click_again_exit);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LiveNotifier.inst().remove(this);
        SingletonImpl.inst().freeAll();
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                startActivity(ScanActivity.class);
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {
                showToast(getString(R.string.user_photo_permission));
            }
            break;
        }
    }

    private void gridFragListener(List<Meet> data) {
        mVpFrag.setData(data);
        mVpFrag.nativeInvalidate();
        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int page = SpUser.inst().getMainAcVpPage();
                if (page != 0) {
                    setCurrentItem(page, false);
                    mVpFrag.setPosition(0);
                    mIvShift.setSelected(true);
                }

                removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mGridFrag = restoreFragment(KPageGrid);
        mVpFrag = restoreFragment(KPageVp);
        mGridFrag.setListener(data -> gridFragListener(data));

        invalidate();
        mGridFrag.invalidate();
    }
}
