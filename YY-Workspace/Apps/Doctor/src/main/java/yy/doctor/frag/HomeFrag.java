package yy.doctor.frag;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseSRListFrag;
import lib.yy.network.ListResult;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.activity.NoticeActivity;
import yy.doctor.activity.meeting.MeetingSearchActivity;
import yy.doctor.adapter.HomeAdapter;
import yy.doctor.adapter.HomeAdapter.onTvAttentionListener;
import yy.doctor.model.home.Banner;
import yy.doctor.model.home.IHome;
import yy.doctor.model.home.RecMeeting;
import yy.doctor.model.home.RecUnitNum;
import yy.doctor.model.home.RecUnitNum.TRecUnitNum;
import yy.doctor.model.home.RecUnitNums;
import yy.doctor.network.NetFactory;
import yy.doctor.view.BadgeView;
import yy.doctor.view.BannerView;

import static lib.yy.network.BaseJsonParser.evs;

/**
 * @author CaiXiang   extends BaseSRListFrag<Home>
 * @since 2017/4/5
 */
public class HomeFrag extends BaseSRListFrag<IHome, HomeAdapter> implements onTvAttentionListener {

    private static final int KReqIdBanner = 1;
    private static final int KReqIdMeeting = 2;
    private static final int KReqIdUnitNum = 3;
    private static final int KReqIdAttention = 4;
    private static final int KAttention = 1;  //关注单位号

    private boolean mBannerReqIsOK = false;
    private boolean mUnitNumReqIsOK = false;
    private boolean mMeetingReqIsOK = false;

    private List<RecUnitNum> mRecUnitNums;
    private List<IHome> mRecMeetings;
    private List<String> mBanners;

    private View mViewNotice;
    private BadgeView mBadgeView;
    private BannerView mBannerView;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        View v = inflate(R.layout.layout_home_nav_bar_search);
        bar.addViewRight(v, new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(MeetingSearchActivity.class);
            }
        });

        mViewNotice = bar.addViewRight(R.mipmap.nav_bar_ic_notice, v1 -> startActivity(NoticeActivity.class));
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_home_header);
    }

    @Override
    public void findViews() {
        super.findViews();

        mBannerView = findView(R.id.home_header_banner);
    }

    @Override
    public void setViews() {
        super.setViews();

        mBadgeView = new BadgeView(getContext());
        mBadgeView.setBadgeMargin(0, 7, 7, 0);
        mBadgeView.setTargetView(mViewNotice);

        getAdapter().setTvAttentionListener(this);
    }

    @Override
    public void getDataFromNet() {
        if (initComplete()) {
            mBannerReqIsOK = false;
            mUnitNumReqIsOK = false;
            mMeetingReqIsOK = false;
        }
        exeNetworkReq(KReqIdBanner, NetFactory.banner());
        exeNetworkReq(KReqIdUnitNum, NetFactory.recommendUnitNum());
        exeNetworkReq(KReqIdMeeting, NetFactory.recommendMeeting());
    }

    private boolean isAttention = false;

    @Override
    public void onTvAttentionClick(int attention, int unitNumId, TextView tv) {
        //判断是否已经关注
        if (attention == 1) {
            // do nothing
        } else {
            tv.setSelected(true);
            tv.setClickable(false);
            tv.setText("已关注");
            exeNetworkReq(KReqIdAttention, NetFactory.attention(unitNumId, KAttention));
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {

        if (id == KReqIdAttention) {
            return true;
        }
        ListResult result = null;
        if (id == KReqIdBanner) {
            result = evs(r.getText(), Banner.class);
            mBannerReqIsOK = result.isSucceed();
            if (mBannerReqIsOK) {
                mBanners = result.getData();
            }
        } else if (id == KReqIdUnitNum) {
            result = evs(r.getText(), RecUnitNum.class);
            mUnitNumReqIsOK = result.isSucceed();
            if (mUnitNumReqIsOK) {
                mRecUnitNums = result.getData();
            }

            if (BuildConfig.TEST) {
                for (RecUnitNum num : mRecUnitNums) {
                    num.put(TRecUnitNum.attention, 0);
                }
            }
        } else if (id == KReqIdMeeting) {
            result = evs(r.getText(), RecMeeting.class);
            mMeetingReqIsOK = result.isSucceed();
            if (mMeetingReqIsOK) {
                mRecMeetings = result.getData();
            }
        }

        return result;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KReqIdAttention) {
            return;
        }
        //确保所有数据都已经获取
        ListResult r = (ListResult) result;
        if (id == KReqIdBanner) {
            mBannerReqIsOK = r.isSucceed();
            mBannerView.setData(mBanners);
        } else if (id == KReqIdUnitNum) {
            mUnitNumReqIsOK = r.isSucceed();
        } else if (id == KReqIdMeeting) {
            mMeetingReqIsOK = r.isSucceed();
        }

        // 拼接数据
        if (mBannerReqIsOK && mUnitNumReqIsOK && mMeetingReqIsOK) {
            ListResult<IHome> ret = new ListResult();
            List<IHome> homes = new ArrayList<>();

            homes.addAll(mRecMeetings);

            RecUnitNums nums = new RecUnitNums();
            nums.setData(mRecUnitNums);
            homes.add(nums);

            // FIXME: 为了添加多一些测试数据
            homes.addAll(mRecMeetings);

            ret.setData(homes);

            super.onNetworkSuccess(id, ret);
        }
    }

}
