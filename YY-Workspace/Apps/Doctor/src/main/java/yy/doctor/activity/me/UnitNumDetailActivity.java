package yy.doctor.activity.me;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.LogMgr;
import lib.ys.fitter.LayoutFitter;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.interceptor.CutInterceptor;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.activity.base.BaseListActivity;
import lib.yy.network.Result;
import lib.yy.view.SwipeZoomView.SwipeZoomListView;
import yy.doctor.BuildConfig;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.meeting.MeetingDetailsActivity;
import yy.doctor.activity.meeting.MeetingSearchActivity;
import yy.doctor.adapter.UnitNumDetailAdapter;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.dialog.BottomDialog.OnDialogItemClickListener;
import yy.doctor.model.unitnum.UnitNumDetail;
import yy.doctor.model.unitnum.UnitNumDetail.TUnitNumDetail;
import yy.doctor.model.unitnum.UnitNumDetailData;
import yy.doctor.model.unitnum.UnitNumDetailData.TUnitNumDetailData;
import yy.doctor.model.unitnum.UnitNumDetailMeeting;
import yy.doctor.model.unitnum.UnitNumDetailMeeting.TUnitNumDetailMeeting;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.CacheUtil;
import yy.doctor.util.Util;

/**
 * 单位号详情
 *
 * @auther yuansui
 * @since 2017/4/25
 */
public class UnitNumDetailActivity extends BaseListActivity<UnitNumDetailMeeting, UnitNumDetailAdapter> {

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
        mUnitNumId = getIntent().getIntExtra(Extra.KData, 0);
        if (BuildConfig.TEST) {
            mUnitNumId = 14;
        }
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
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, this);
        setNavBarAutoAlphaByScroll(fitDp(219), bar);
        bar.addViewRight(R.mipmap.nav_bar_ic_search, new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(MeetingSearchActivity.class);
            }
        });
        bar.addViewRight(R.mipmap.nav_bar_ic_more, new OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialogCancelAttention();
            }
        });

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
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_unit_num_detail_header);
    }

    @Override
    public void setViews() {
        super.setViews();

        exeNetworkReq(KReqIdUnitNumDetail, NetFactory.unitNumDetail(mUnitNumId, 1, 8));

        mZoomView.setZoomEnabled(true);
        mIvAvatar.res(R.mipmap.form_ic_personal_head)
                .renderer(new CircleRenderer())
                .load();

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

                mIvZoom.res(R.mipmap.form_ic_personal_head)
                        .addInterceptor(new CutInterceptor(w, h))
                        //.addInterceptor(new BlurInterceptor(UnitNumDetailActivity.this))
                        .load();

                removeOnGlobalLayoutListener(this);
            }
        });

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
        MeetingDetailsActivity.nav(this, getItem(position).getString(TUnitNumDetailMeeting.id));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewUtil.recycleIvBmp(mIvZoom);
    }

    private void showDialogCancelAttention() {

        final List<String> data = new ArrayList<>();
        data.add("不再关注");
        data.add("添加到桌面");
        data.add("取消");

        final BottomDialog dialog = new BottomDialog(this, new OnDialogItemClickListener() {

            @Override
            public void onDialogItemClick(int position) {
                if (position == 0) {
                    exeNetworkReq(KReqIdNoAttention, NetFactory.attention(mUnitNumId, KNoAttention));
                } else if (position == 1) {
                    createShortcut();
                }
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
            mTvName.setText(mUnitNumDetail.getString(TUnitNumDetail.nickname));
            mTvAttentionNum.setText(mUnitNumDetail.getString(TUnitNumDetail.attentionNum) + "人");
            mTvAddress.setText(mUnitNumDetail.getString(TUnitNumDetail.province) + " " + mUnitNumDetail.getString(TUnitNumDetail.city));
            //判断用户是否已经关注过此单位号
            if (mUnitNumDetail.getInt(TUnitNumDetail.attention) == 1) {
                mTvAttention.setText("已关注");
                mTvAttention.setSelected(true);
                mTvAttention.setClickable(false);
            }

            List<UnitNumDetailData> listFile = mUnitNumDetail.getList(TUnitNumDetail.materialDTOList);
            int listSize = listFile.size();
            if (listSize == 0) {
                goneView(mVFileLayout);
                goneView(mVLargeDivider);
            }

            for (int i = 0; i < listFile.size(); i++) {
                UnitNumDetailData fileItem = listFile.get(i);
                addFileItem(fileItem.getString(TUnitNumDetailData.materialName), new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        DownloadDataActivity.nav(UnitNumDetailActivity.this, CacheUtil.getUnitNumCacheDir(String.valueOf(mUnitNumId)),
                                fileItem.getString(TUnitNumDetailData.materialName), fileItem.getString(TUnitNumDetailData.materialUrl), fileItem.getString(TUnitNumDetailData.materialType),
                                fileItem.getLong(TUnitNumDetailData.fileSize));
                    }
                });
            }
            int datumNum = mUnitNumDetail.getInt(TUnitNumDetail.materialNum);
            if (datumNum > 3) {
                mTvFileNum.setText("查看全部" + datumNum + "个文件");
                showView(mIvArrows);
                mVFileLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        UnitNumDataActivity.nav(UnitNumDetailActivity.this, mUnitNumDetail.getInt(TUnitNumDetail.id));
                    }
                });
            }

            setData(mUnitNumDetail.getList(TUnitNumDetail.meetingDTOList));
        } else if (id == KReqIdAttention) {  //关注
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast("关注成功");
                mTvAttention.setText("已关注");
                mTvAttention.setSelected(true);
                mTvAttention.setClickable(false);
            }
        } else if (id == KReqIdNoAttention) {  //取消关注
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast("已取消关注");
                mTvAttention.setText("关注");
                mTvAttention.setSelected(false);
                mTvAttention.setClickable(true);
            }
        }
    }

    /*
    * 生成桌面快捷方式
    */
    private void createShortcut() {
        Intent shortcutIntent = new Intent();
        //设置点击快捷方式时启动的Activity,因为是从Lanucher中启动，所以包名类名要写全。
        shortcutIntent.setComponent(new ComponentName(getPackageName(),  LaunchTempActivity.class.getName()));
        LogMgr.d(TAG, " getPackageName() = " + getPackageName());
        LogMgr.d(TAG, "UnitNumDetailActivity.class.getName()) = " + LaunchTempActivity.class.getName());
        //设置启动的模式
        shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.putExtra(Extra.KUnitNumId, mUnitNumId);

        Intent resultIntent = new Intent();
        //设置快捷方式图标
        resultIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_default_home_meeting_speaker));
        //启动的Intent
        resultIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        //设置快捷方式的名称
        resultIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "测试");

        resultIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(resultIntent);
    }

    /**
     * 添加文件item
     *
     * @param text
     * @param l
     */
    public void addFileItem(CharSequence text, OnClickListener l) {

        View v = getLayoutInflater().inflate(R.layout.layout_unit_num_detail_file_item, null);
        TextView tv = (TextView) v.findViewById(R.id.unit_num_detail_file_item_tv_name);
        tv.setText(text);
        v.setOnClickListener(l);

        LayoutFitter.fit(v);
        mLayoutFile.addView(v, LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
    }

}
