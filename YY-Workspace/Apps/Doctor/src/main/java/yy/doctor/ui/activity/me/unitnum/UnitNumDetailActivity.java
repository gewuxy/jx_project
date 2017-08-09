package yy.doctor.ui.activity.me.unitnum;

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

import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.interceptor.BlurInterceptor;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.ViewUtil;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.ListResult;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseSRListActivity;
import lib.yy.view.SwipeZoomView.SwipeZoomListView;
import yy.doctor.Extra;
import yy.doctor.Extra.FileFrom;
import yy.doctor.R;
import yy.doctor.adapter.meeting.MeetingAdapter;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.model.home.RecUnitNum.Attention;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.unitnum.FileData;
import yy.doctor.model.unitnum.UnitNumDetail;
import yy.doctor.model.unitnum.UnitNumDetail.TUnitNumDetail;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.network.image.CutInterceptor;
import yy.doctor.ui.activity.me.LaunchTmpActivity;
import yy.doctor.ui.activity.search.SearchActivity;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;

/**
 * 单位号详情
 *
 * @auther yuansui
 * @since 2017/4/25
 */
public class UnitNumDetailActivity extends BaseSRListActivity<Meeting, MeetingAdapter> {

    private static final int KColorNoAttention = Color.parseColor("#d14b4b");
    private static final int KColorNormal = Color.parseColor("#666666");
    private static final int KColorCancel = Color.parseColor("#01b557");
    private static final int KReqIdUnitNumDetail = 0;
    private static final int KReqIdAttention = 1;
    private static final int KReqIdCancelAttention = 2;


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

    private View mLayoutIntro;
    private View mLayoutFile;
    private ImageView mIvArrows;
    private LinearLayout mLayoutFileItem;
    private View mLargeDivider;
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
    public View createHeaderView() {
        return inflate(R.layout.layout_unit_num_detail_header);
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, this);
        changeAlphaByScroll(fitDp(219), bar);
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

        mLayoutIntro = findView(R.id.unit_num_detail_intro_layout);
        mTvFileNum = findView(R.id.unit_num_detail_tv_file_num);
        mLayoutFile = findView(R.id.unit_num_detail_layout_file);
        mIvArrows = findView(R.id.unit_num_detail_iv_arrow);
        mLayoutFileItem = findView(R.id.unit_num_detail_file_layout_content);
        mLargeDivider = findView(R.id.unit_num_detail_large_divider);
        mDivider = findView(R.id.unit_num_detail_divider);
    }

    @Override
    public void setViews() {
        super.setViews();

        getAdapter().hideUnitNum();
        setDividerHeight(fitDp(1));
        setRefreshEnabled(false);
        mZoomView.setZoomEnabled(true);

        setOnClickListener(R.id.unit_num_detail_layout_file);
        setOnClickListener(R.id.unit_num_detail_tv_attention);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(KReqIdUnitNumDetail, NetFactory.unitNumDetail(mUnitNumId, getOffset(), getLimit()));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();

        if (id == R.id.unit_num_detail_tv_attention) {
            exeNetworkReq(KReqIdAttention, NetFactory.attention(mUnitNumId, Attention.yes));
            //关注人数加1
            mUnitNumDetail.put(TUnitNumDetail.attentionNum, mUnitNumDetail.getInt(TUnitNumDetail.attentionNum) + 1);
            mTvAttentionNum.setText(mUnitNumDetail.getInt(TUnitNumDetail.attentionNum) + getString(R.string.attention_num_unit));
            //改变状态
            UISetter.setAttention(mTvAttention, Attention.yes);

            showToast(R.string.attention_success);
            //通知首页的单位号改变状态
            notify(NotifyType.unit_num_attention_change, new AttentionUnitNum(mUnitNumId, Attention.yes));
        }
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewUtil.recycleIvBmp(mIvZoom);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.unit_num_attention_change) {
            // 关注  取消关注后，对应的单位号的关注状态也要改变
            AttentionUnitNum attentionUnitNum = (AttentionUnitNum) data;
            int unitNumId = attentionUnitNum.getUnitNumId();
            int attention = attentionUnitNum.getAttention();
            if (mUnitNumId == unitNumId) {
                //改变状态
                UISetter.setAttention(mTvAttention, attention);
            }
        }

    }

    private void showDialogCancelAttention() {

        final BottomDialog dialog = new BottomDialog(this, position -> {

            if (position == 0) {
                exeNetworkReq(KReqIdCancelAttention, NetFactory.attention(mUnitNumId, Attention.no));
                //关注人数减1
                mUnitNumDetail.put(TUnitNumDetail.attentionNum, mUnitNumDetail.getInt(TUnitNumDetail.attentionNum) - 1);
                mTvAttentionNum.setText(mUnitNumDetail.getInt(TUnitNumDetail.attentionNum) + getString(R.string.attention_num_unit));

                //改变状态
                UISetter.setAttention(mTvAttention, Attention.no);

                showToast(R.string.cancel_attention_success);
                //通知首页的单位号改变状态
                notify(NotifyType.unit_num_attention_change, new AttentionUnitNum(mUnitNumId, Attention.no));

                //通知单位号页面去掉这个单位号的item
                notify(NotifyType.cancel_attention, mUnitNumDetail.getInt(TUnitNumDetail.id));
            } else if (position == 1) {
                createShortcut();
            }
        });

        dialog.addItem(getString(R.string.cancel_attention), KColorNoAttention);
        dialog.addItem(getString(R.string.add_to_desktop), KColorNormal);
        dialog.addItem(getString(R.string.cancel), KColorCancel);

        dialog.show();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {

        if (id == KReqIdUnitNumDetail) {
            if (getOffset() != getInitOffset()) {
                return JsonParser.evs(r.getText(), Meeting.class);
            } else {
                return JsonParser.ev(r.getText(), UnitNumDetail.class);
            }
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

        if (id == KReqIdUnitNumDetail) {
            if (getOffset() != getInitOffset()) {
                // 翻页逻辑 翻页时返回的数据结构不一样
                super.onNetworkSuccess(id, result);
                return;
            }

            Result<UnitNumDetail> r = (Result<UnitNumDetail>) result;
            if (!r.isSucceed()) {
                return;
            }

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

                    mIvZoom.url(mUnitNumDetail.getString(TUnitNumDetail.headimg))
                            .addInterceptor(new CutInterceptor(w, h))
                            .addInterceptor(new BlurInterceptor(UnitNumDetailActivity.this))
                            .load();
                    removeOnGlobalLayoutListener(this);
                }
            });

            mUnitNumName = mUnitNumDetail.getString(TUnitNumDetail.nickname);
            mTvName.setText(mUnitNumName);
            mTvAttentionNum.setText(mUnitNumDetail.getString(TUnitNumDetail.attentionNum) + getString(R.string.attention_num_unit));
            mTvAddress.setText(mUnitNumDetail.getString(TUnitNumDetail.province) + " " + mUnitNumDetail.getString(TUnitNumDetail.city));

            //判断是否有简介， 没有的话要gone掉layout
            String intro = mUnitNumDetail.getString(TUnitNumDetail.sign);
            if (TextUtil.isEmpty(intro)) {
                goneView(mLayoutIntro);
            } else {
                mTvIntroduction.setText(intro);
            }

            //判断用户是否已经关注过此单位号
            if (mUnitNumDetail.getInt(TUnitNumDetail.attention) == 1) {
                mTvAttention.setText(R.string.already_attention);
                mTvAttention.setSelected(true);
                mTvAttention.setClickable(false);
            }

            // FIXME: 2017/8/7
            List<FileData> listFile = mUnitNumDetail.getList(TUnitNumDetail.materialList);
            if (listFile == null || listFile.size() == 0) {
                goneView(mDivider);
                goneView(mLayoutFile);
                goneView(mLargeDivider);
            } else {
                UISetter.setFileData(mLayoutFileItem, listFile, mUnitNumId);
            }

            int fileNum = mUnitNumDetail.getInt(TUnitNumDetail.materialNum);
            if (fileNum > UnitNumDetail.KFileLimit) {
                String dataNum = String.format(ResLoader.getString(R.string.check_all), fileNum);
                mTvFileNum.setText(dataNum);
                showView(mIvArrows);
                mLayoutFile.setOnClickListener(v ->
                        FilesActivityIntent.create(mUnitNumDetail.getString(TUnitNumDetail.id),
                                FileFrom.unit_num)
                                .start(this)
                );
            }

            ListResult<Meeting> meetingResult = new ListResult<>();
            meetingResult.setCode(ErrorCode.KOk);
            meetingResult.setData(mUnitNumDetail.getList(TUnitNumDetail.meetFolderList));
            super.onNetworkSuccess(id, meetingResult);

        } else if (id == KReqIdAttention) {  //关注
            return;
        } else if (id == KReqIdCancelAttention) {  //取消关注
            return;
        }
    }

    /*
    * 生成桌面快捷方式
    */
    private void createShortcut() {
        Intent shortcutIntent = new Intent();
        //设置点击快捷方式时启动的Activity,因为是从Lanucher中启动，所以包名类名要写全。
        shortcutIntent.setComponent(new ComponentName(getPackageName(), LaunchTmpActivity.class.getName()));
        YSLog.d(TAG, " getPackageName() = " + getPackageName());
        YSLog.d(TAG, "UnitNumDetailActivity.class.getName()) = " + LaunchTmpActivity.class.getName());
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

    public static class AttentionUnitNum {

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
