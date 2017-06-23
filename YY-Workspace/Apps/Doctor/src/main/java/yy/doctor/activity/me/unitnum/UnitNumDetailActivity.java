package yy.doctor.activity.me.unitnum;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.interceptor.CutInterceptor;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.Notifier.NotifyType;
import lib.yy.activity.base.BaseListActivity;
import lib.yy.network.Result;
import lib.yy.view.SwipeZoomView.SwipeZoomListView;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.me.LaunchTempActivity;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.activity.search.SearchActivity;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.model.home.RecUnitNum.Attention;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.meet.Meeting.TMeeting;
import yy.doctor.model.unitnum.FileData;
import yy.doctor.model.unitnum.UnitNumDetail;
import yy.doctor.model.unitnum.UnitNumDetail.TUnitNumDetail;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;

/**
 * 单位号详情
 *
 * @auther yuansui
 * @since 2017/4/25
 */
public class UnitNumDetailActivity extends BaseListActivity<Meeting, MeetingAdapter> {

    private static final int KColorNoAttention = Color.parseColor("#d14b4b");
    private static final int KColorNormal = Color.parseColor("#666666");
    private static final int KColorCancel = Color.parseColor("#01b557");
    private static final int KReqIdUnitNumDetail = 0;
    private static final int KReqIdAttention = 1;
    private static final int KReqIdNoAttention = 2;
    private static final int KAttention = 1;  //关注
    private static final int KNoAttention = 0;  //取消关注

    private SwipeZoomListView mZoomView;
    private NetworkImageView mIvZoom;
    private NetworkImageView mIvAvatar;
    private View mLayoutHeaderRoot;
    private TextView mTvName;
    private TextView mTvAttentionNum;
    private TextView mTvAttention;
    private TextView mTvAddress;
    private TextView mTvIntroduction;
    private TextView mTvFileNum;

    private View mVFileLayout;
    private ImageView mIvArrows;
    private LinearLayout mLayoutFile;
    private View mVLargeDivider;
    private View mDivider;

    private UnitNumDetail mUnitNumDetail;

    private int mUnitNumId;
    private String mUnitNumName;

    public static void nav(Context context, int unitUnmId) {
        Intent i = new Intent(context, UnitNumDetailActivity.class)
                .putExtra(Extra.KData, unitUnmId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mUnitNumId = getIntent().getIntExtra(Extra.KData, 10);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_unit_num_detail;
    }

    @Override
    public int getListViewResId() {
        return android.R.id.list;
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_unit_num_detail_header);
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, this);
        setNavBarAutoAlphaByScroll(fitDp(219), bar);
        bar.addViewRight(R.mipmap.nav_bar_ic_search, v -> startActivity(SearchActivity.class));
        bar.addViewRight(R.mipmap.nav_bar_ic_more, v -> showDialogCancelAttention());
    }

    @Override
    public void findViews() {
        super.findViews();

        mZoomView = findView(R.id.unit_num_detail_layout_zoom);
        mIvZoom = findView(R.id.unit_num_detail_zoom_iv);
        mIvAvatar = findView(R.id.unit_num_detail_iv_avatar);
        mLayoutHeaderRoot = findView(R.id.unit_num_zoom_header_root);
        mTvName = findView(R.id.unit_num_detail_tv_name);
        mTvAttentionNum = findView(R.id.unit_num_deatil_tv_attention_num);
        mTvAttention = findView(R.id.unit_num_detail_tv_attention);
        mTvAddress = findView(R.id.unit_num_detail_tv_address);
        mTvIntroduction = findView(R.id.unit_num_detail_tv_introduction);

        mTvFileNum = findView(R.id.unit_num_detail_tv_file_num);
        mVFileLayout = findView(R.id.unit_num_detail_layout_file);
        mIvArrows = findView(R.id.unit_num_detail_iv_arrow);
        mLayoutFile = findView(R.id.unit_num_detail_file_layout_content);
        mVLargeDivider = findView(R.id.unit_num_detail_large_divider);
        mDivider = findView(R.id.unit_num_detail_divider);
    }

    @Override
    public void setViews() {
        super.setViews();

        exeNetworkReq(KReqIdUnitNumDetail, NetFactory.unitNumDetail(mUnitNumId, 1, 8));

        mZoomView.setZoomEnabled(true);

        setOnClickListener(R.id.unit_num_detail_layout_file);
        setOnClickListener(R.id.unit_num_detail_tv_attention);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();

        if (id == R.id.unit_num_detail_tv_attention) {
            exeNetworkReq(KReqIdAttention, NetFactory.attention(mUnitNumId, KAttention));
        }
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public void onItemClick(View v, int position) {
        MeetingDetailsActivity.nav(this, getItem(position).getString(TMeeting.id));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewUtil.recycleIvBmp(mIvZoom);
    }

    private void showDialogCancelAttention() {

        final List<String> data = new ArrayList<>();
        data.add(getString(R.string.cancel_attention));
        data.add(getString(R.string.add_to_desktop));
        data.add(getString(R.string.cancel));

        final BottomDialog dialog = new BottomDialog(this, position -> {
            if (position == 0) {
                exeNetworkReq(KReqIdNoAttention, NetFactory.attention(mUnitNumId, KNoAttention));
            } else if (position == 1) {
                createShortcut();
            }
        });
        for (int i = 0; i < data.size(); ++i) {
            if (i == 0) {
                dialog.addItem(data.get(i), KColorNoAttention);
            } else if (i == 1) {
                dialog.addItem(data.get(i), KColorNormal);
            } else {
                dialog.addItem(data.get(i), KColorCancel);
            }
        }
        dialog.show();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {

        if (id == KReqIdUnitNumDetail) {
            return JsonParser.ev(r.getText(), UnitNumDetail.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

        if (id == KReqIdUnitNumDetail) {
            Result<UnitNumDetail> r = (Result<UnitNumDetail>) result;
            mUnitNumDetail = r.getData();

            mIvAvatar.placeHolder(R.mipmap.ic_default_unit_num)
                    .url(mUnitNumDetail.getString(TUnitNumDetail.headimg))
                    .renderer(new CircleRenderer())
                    .load();
            //头像高斯模糊处理
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    int w = mLayoutHeaderRoot.getWidth();
                    int h = mLayoutHeaderRoot.getHeight();
                    if (w == 0 || h == 0) {
                        return;
                    }
                    AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(w, h);
                    mZoomView.setHeaderLayoutParams(localObject);

                    mIvZoom.placeHolder(R.mipmap.ic_default_unit_num)
                            .url(mUnitNumDetail.getString(TUnitNumDetail.headimg))
                            .addInterceptor(new CutInterceptor(w, h))
                            //.addInterceptor(new BlurInterceptor(UnitNumDetailActivity.this))
                            .load();
                    removeOnGlobalLayoutListener(this);
                }
            });

            mUnitNumName = mUnitNumDetail.getString(TUnitNumDetail.nickname);
            mTvName.setText(mUnitNumName);
            mTvAttentionNum.setText(mUnitNumDetail.getString(TUnitNumDetail.attentionNum) + getString(R.string.attention_num_unit));
            mTvAddress.setText(mUnitNumDetail.getString(TUnitNumDetail.province) + " " + mUnitNumDetail.getString(TUnitNumDetail.city));
            mTvIntroduction.setText(mUnitNumDetail.getString(TUnitNumDetail.sign));

            //判断用户是否已经关注过此单位号
            if (mUnitNumDetail.getInt(TUnitNumDetail.attention) == 1) {
                mTvAttention.setText(R.string.already_attention);
                mTvAttention.setSelected(true);
                mTvAttention.setClickable(false);
            }

            List<FileData> listFile = mUnitNumDetail.getList(TUnitNumDetail.materialDTOList);
            int listSize = listFile.size();
            if (listSize == 0 || listFile == null) {
                goneView(mDivider);
                goneView(mVFileLayout);
                goneView(mVLargeDivider);
            }

            int datumNum = mUnitNumDetail.getInt(TUnitNumDetail.materialNum);
            if (datumNum > 3) {
                mTvFileNum.setText(getString(R.string.check_all) + datumNum + getString(R.string.file_num));
                showView(mIvArrows);
                mVFileLayout.setOnClickListener(v -> FileDataActivity.nav(UnitNumDetailActivity.this, mUnitNumDetail.getInt(TUnitNumDetail.id), Extra.KUnitNumType));
            }

            UISetter.setFileData(mLayoutFile, listFile, mUnitNumId);

            setData(mUnitNumDetail.getList(TUnitNumDetail.meetingDTOList));
        } else if (id == KReqIdAttention) {  //关注
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast(R.string.attention_success);
                mTvAttention.setText(R.string.already_attention);
                mTvAttention.setSelected(true);
                mTvAttention.setClickable(false);
                //通知首页的单位号改变状态
                notify(NotifyType.unit_num_attention_change, new AttentionUnitNum(mUnitNumId, Attention.yes));
            }
        } else if (id == KReqIdNoAttention) {  //取消关注
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast(R.string.cancel_attention_success);
                mTvAttention.setText(R.string.attention);
                mTvAttention.setSelected(false);
                mTvAttention.setClickable(true);
                //通知首页的单位号改变状态
                notify(NotifyType.unit_num_attention_change, new AttentionUnitNum(mUnitNumId, Attention.no));
            }
        }
    }

    /*
    * 生成桌面快捷方式
    */
    private void createShortcut() {
        Intent shortcutIntent = new Intent();
        //设置点击快捷方式时启动的Activity,因为是从Lanucher中启动，所以包名类名要写全。
        shortcutIntent.setComponent(new ComponentName(getPackageName(), LaunchTempActivity.class.getName()));
        YSLog.d(TAG, " getPackageName() = " + getPackageName());
        YSLog.d(TAG, "UnitNumDetailActivity.class.getName()) = " + LaunchTempActivity.class.getName());
        //设置启动的模式
        shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.putExtra(Extra.KUnitNumId, mUnitNumId);

        Intent resultIntent = new Intent();
        //设置快捷方式图标
        resultIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher));
        //启动的Intent
        resultIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        //设置快捷方式的名称
        resultIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, mUnitNumName);

        resultIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(resultIntent);
    }

    public class AttentionUnitNum {

        private int mUnitNumId;
        private int mAttention;

        public AttentionUnitNum(int unitNumId, int attention) {
            mUnitNumId = unitNumId;
            mAttention = attention;
        }

        public int getUnitNumId() {
            return mUnitNumId;
        }

        public int getAttention() {
            return mAttention;
        }
    }

}
