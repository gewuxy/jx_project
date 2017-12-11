package jx.csp.ui.activity.me;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jx.csp.R;
import jx.csp.adapter.VipPermissionAdapter;
import jx.csp.model.VipPermission;
import jx.csp.model.VipPermission.TVip;
import jx.csp.model.VipPackage;
import jx.csp.model.VipPackage.TPackage;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;
import lib.ys.util.res.ResLoader;

/**
 * @auther Huoxuyu
 * @since 2017/12/8
 */
public class VipManageActivity extends BaseActivity {

    private final int KNorm = 1;    //标准
    private final int KAdvanced = 2;//高级
    private final int KProfession = 3;//专业

    private final String KMeetingLimit = "10";
    private final String KMeetingInfinite = "∞";

    private RecyclerView mRvPermission; //使用权限

    private ImageView mIvCard;   //会员卡片
    private TextView mTvValidity;
    private TextView mTvMeetCount;
    private TextView mTvLimit;

    private int[] mImage;
    private String[] mText;
    private int[] mPermissionId;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_vip_manage;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.person_center_vip_manage, this);
    }

    @Override
    public void findViews() {
        mRvPermission = findView(R.id.rv_vip_permission);
        mIvCard = findView(R.id.iv_vip_manage_card);

        mTvValidity = findView(R.id.tv_vip_validity);
        mTvMeetCount = findView(R.id.tv_vip_meet_num);
        mTvLimit = findView(R.id.tv_vip_meet_limit);
    }

    @Override
    public void setViews() {
        getLevel();
    }

    private void getLevel() {
        mRvPermission.setLayoutManager(new GridLayoutManager(this, 4));
        VipPermissionAdapter adapter = new VipPermissionAdapter();

        long packageStart = VipPackage.inst().getLong(TPackage.packageStart);
        long packageEnd = VipPackage.inst().getLong(TPackage.packageEnd);

        mPermissionId = new int[]{1, 2, 3, 4, 5};
        switch (VipPackage.inst().getInt(TPackage.id)) {
            case KNorm: {
                mIvCard.setImageDrawable(ResLoader.getDrawable(R.drawable.vip_ic_norm_card_cn));
                mImage = new int[]{
                        R.drawable.vip_ic_record,
                        R.drawable.vip_ic_live,
                        R.drawable.vip_ic_meet_num,
                        R.drawable.vip_ic_un_advertising,
                        R.drawable.vip_ic_un_watermark,
                };

                mText = new String[]{
                        getString(R.string.vip_manage_record),
                        getString(R.string.vip_manage_live),
                        getString(R.string.vip_manage_three_meeting),
                        getString(R.string.vip_manage_advertising),
                        getString(R.string.vip_manage_close_watermark),
                };
            }
            break;
            case KAdvanced: {
                mIvCard.setImageDrawable(ResLoader.getDrawable(R.drawable.vip_ic_advanced_card_cn));
                mTvValidity.setText(TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + getString(R.string.vip_manage_to) + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                YSLog.d("TAG", "" + System.currentTimeMillis());
                mTvMeetCount.setText(VipPackage.inst().getString(TPackage.usedMeetCount));
                mTvLimit.setText(KMeetingLimit);
                mImage = new int[]{
                        R.drawable.vip_ic_record,
                        R.drawable.vip_ic_live,
                        R.drawable.vip_ic_meet_num,
                        R.drawable.vip_ic_advertising,
                        R.drawable.vip_ic_watermark,
                };

                mText = new String[]{
                        getString(R.string.vip_manage_record),
                        getString(R.string.vip_manage_live),
                        getString(R.string.vip_manage_ten_meeting),
                        getString(R.string.vip_manage_advertising),
                        getString(R.string.vip_manage_close_watermark),
                };
            }
            break;
            case KProfession: {
                mIvCard.setImageDrawable(ResLoader.getDrawable(R.drawable.vip_ic_profession_card_cn));
                mTvValidity.setText(TimeFormatter.milli(packageStart, TimeFormat.simple_ymd) + " 至 " + TimeFormatter.milli(packageEnd, TimeFormat.simple_ymd));
                mTvMeetCount.setText(VipPackage.inst().getString(TPackage.usedMeetCount));
                mTvLimit.setText(KMeetingInfinite);
                mImage = new int[]{
                        R.drawable.vip_ic_record,
                        R.drawable.vip_ic_live,
                        R.drawable.vip_ic_meet_num,
                        R.drawable.vip_ic_advertising,
                        R.drawable.vip_ic_watermark,
                };

                mText = new String[]{
                        getString(R.string.vip_manage_record),
                        getString(R.string.vip_manage_live),
                        getString(R.string.vip_manage_infinite_meeting),
                        getString(R.string.vip_manage_advertising),
                        getString(R.string.vip_manage_custom_watermark),
                };
            }
            break;
        }

        List<VipPermission> list = vipPermission(mPermissionId, mImage, mText);
        adapter.setData(list);
        mRvPermission.setAdapter(adapter);
    }

    private List<VipPermission> vipPermission(int[] id, int[] image, String[] text) {
        List<VipPermission> list = new ArrayList<>();
        if (image == null || text == null || id == null) {
            return list;
        }
        int length = Math.min(Math.min(image.length, text.length), id.length);
        for (int i = 0; i < length; i++) {
            VipPermission vip = new VipPermission();
            vip.put(TVip.image, image[i]);
            vip.put(TVip.text, text[i]);
            list.add(vip);
        }
        return list;
    }
}
