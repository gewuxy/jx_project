package yy.doctor.activity.me;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.fitter.LayoutFitter;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.interceptor.CutInterceptor;
import lib.ys.network.image.renderer.CircleRenderer;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.NestCheckBox;
import lib.yy.activity.base.BaseListActivity;
import lib.yy.network.Resp;
import lib.yy.view.SwipeZoomView.SwipeZoomListView;
import yy.doctor.R;
import yy.doctor.adapter.UnitNumDetailAdapter;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.dialog.BottomDialog.OnDialogItemClickListener;
import yy.doctor.model.unitnum.UnitNumDetail;
import yy.doctor.model.unitnum.UnitNumDetail.TUnitNumDetail;
import yy.doctor.model.unitnum.UnitNumDetailDatum;
import yy.doctor.model.unitnum.UnitNumDetailDatum.TUnitNumDetailDatum;
import yy.doctor.model.unitnum.UnitNumDetailMeeting;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 单位号
 *
 * @auther yuansui
 * @since 2017/4/25
 */
public class UnitNumDetailActivity extends BaseListActivity<UnitNumDetailMeeting, UnitNumDetailAdapter> {

    private static final int KColorNormal = Color.parseColor("#666666");
    private static final int KColorCancel = Color.parseColor("#01b557");
    private static final float KAvatarScale = 0.5f;

    private SwipeZoomListView mZoomView;
    private NetworkImageView mIvZoom;
    private NetworkImageView mIvAvatar;
    private View mLayoutHeaderRoot;
    private NestCheckBox mCBAttention;
    private TextView mTvName;
    private TextView mTvAttentionNum;
    private TextView mTvAddress;
    private TextView mTvIntroduction;
    private TextView mTvFileNum;

    private View mVFileLayout;
    private ImageView mIvArrrow;
    private LinearLayout mLayoutFile;
    private View mVLargeDivider;



    private UnitNumDetail mUnitNumDetail;

    @Override
    public void initData() {

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

                showToast("555");
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
        mCBAttention = findView(R.id.unit_num_detail_check_box_attention);
        mTvName = findView(R.id.unit_num_detail_tv_name);
        mTvAttentionNum = findView(R.id.unit_num_deatil_tv_attention_num);
        mTvAddress = findView(R.id.unit_num_detail_tv_address);
        mTvIntroduction = findView(R.id.unit_num_detail_tv_introduction);

        mTvFileNum = findView(R.id.unit_num_detail_tv_file_num);
        mVFileLayout = findView(R.id.unit_num_detail_layout_file);
        mIvArrrow = findView(R.id.unit_num_detail_iv_arrow);
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

        exeNetworkReq(0, NetFactory.unitNumDetail(8, 1, 8));

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

        mCBAttention.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mCBAttention.getRealCheckBox().setText("已关注");
                } else {
                    mCBAttention.getRealCheckBox().setText("关注");
                }

            }
        });

        setOnClickListener(R.id.unit_num_detail_layout_file);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        /*switch (id) {
            case R.id.unit_num_detail_layout_file: {
                startActivity(UnitNumDataActivity.class);
            }
            break;
        }*/
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public void onItemClick(View v, int position) {
        showToast(position + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ViewUtil.recycleIvBmp(mIvZoom);
    }

    private void showDialogCancelAttention() {

        final List<String> data = new ArrayList<>();
        data.add("不再关注");
        data.add("取消");

        final BottomDialog dialog = new BottomDialog(this, new OnDialogItemClickListener() {

            @Override
            public void onDialogItemClick(int position) {

                if (position == 0) {

                    exeNetworkReq(1, NetFactory.attention(14, 0));
                }

            }
        });

        for (int i = 0; i < data.size(); ++i) {
            if (i != (data.size() - 1)) {
                dialog.addItem(data.get(i), KColorNormal);
            } else {
                dialog.addItem(data.get(i), KColorCancel);
            }
        }

        dialog.show();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {

        if (id == 0) {
            return JsonParser.ev(r.getText(), UnitNumDetail.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

        if (id == 0) {
            Resp<UnitNumDetail> r = (Resp<UnitNumDetail>) result;
            mUnitNumDetail = r.getData();
            mTvName.setText(mUnitNumDetail.getString(TUnitNumDetail.nickname));
            mTvAttentionNum.setText(mUnitNumDetail.getString(TUnitNumDetail.attentionNum) + "人");
            mTvAddress.setText(mUnitNumDetail.getString(TUnitNumDetail.province) + "-" + mUnitNumDetail.getString(TUnitNumDetail.city));

            List<UnitNumDetailDatum> listFile = mUnitNumDetail.getList(TUnitNumDetail.materialDTOList);
            int listSize = listFile.size();
            if (listFile == null) {
                hideView(mVFileLayout);
                hideView(mVLargeDivider);
            }

            for (int i = 0; i < listFile.size() && i < 3; i++) {
                addFile(listFile.get(i).getString(TUnitNumDetailDatum.materialName), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //startActivity(UnitNumDataDetailActivity.class);
                        File file = new File("sd");
                        Uri path = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(path, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
            if (listSize >= 3) {
                mTvFileNum.setText("查看其他" + (listSize-3) + "个文件");
                showView(mIvArrrow);
                mVFileLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(UnitNumDataActivity.class);
                    }
                });

            }

            setData(mUnitNumDetail.getList(TUnitNumDetail.meetingDTOList));

        } else {
            Resp r = (Resp) result;
            if (r.isSucceed()) {
                showToast("成功");
            }
        }
    }

    public void addFile(CharSequence text, OnClickListener l) {

        View v = getLayoutInflater().inflate(R.layout.layout_unit_num_detail_file_item, null);

        TextView tv = (TextView) v.findViewById(R.id.unit_num_detail_file_item_tv_name);
        tv.setText(text);
        v.setOnClickListener(l);

        LayoutFitter.fit(v);
        mLayoutFile.addView(v, LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
    }

}
