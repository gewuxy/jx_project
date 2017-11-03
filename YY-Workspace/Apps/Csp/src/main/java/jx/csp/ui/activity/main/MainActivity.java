package jx.csp.ui.activity.main;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.main.Meet;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.ui.activity.login.ThirdPartyLoginActivity;
import jx.csp.ui.activity.me.MeActivity;
import jx.csp.ui.frag.main.MeetGridFrag;
import jx.csp.ui.frag.main.MeetVpFrag;
import jx.csp.util.Util;
import lib.jg.jpush.SpJPush;
import lib.ys.YSLog;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseVpActivity;

/**
 * 首页
 * @auther WangLan
 * @since 2017/9/30
 */

public class MainActivity extends BaseVpActivity {

    private final int KPageGrid = 0;
    private final int KPageVp = 1;

    private ImageView mIvShift;

    private boolean mFlag;
    private MeetVpFrag mVpFrag;
    private MeetGridFrag mGridFrag;

    @Override
    public void initData() {
        // 列表(空)
        mVpFrag = new MeetVpFrag();
        // 网格
        mGridFrag = new MeetGridFrag();

        mGridFrag.setListener(data -> mVpFrag.setData(data));
        add(mGridFrag);
        add(mVpFrag);
        mFlag = true;
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
            mIvShift.setSelected(mFlag);
            mFlag = !mFlag;
            if (mFlag) {
                // 网格
                setCurrentItem(KPageGrid);
                mGridFrag.setPosition(mVpFrag.getPosition());
            } else {
                // 列表
                setCurrentItem(KPageVp);
                mVpFrag.setPosition(mGridFrag.getPosition());
            }
        });
        mIvShift = Util.getBarView(group, ImageView.class);
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
            CommonServRouter.create()
                    .type(ReqType.j_push)
                    .jPushRegisterId(SpJPush.inst().registerId())
                    .route(this);
            YSLog.d(TAG, "启动绑定极光服务");
        }
    }

    @Override
    public void onClick(View v) {
        startActivity(ScanActivity.class);
    }

    public interface OnMeetGridListener {
        void onMeetRefresh(List<Meet> data);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.logout) {
            finish();
        } else if (type == NotifyType.token_out_of_date) {
            //清除栈里的activity
            Intent intent = new Intent(this, ThirdPartyLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        SingletonImpl.inst().freeAll();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (enableExit()) {
//            super.onBackPressed();
//        } else {
//            showToast("再按一次退出");
//        }
//    }
}
