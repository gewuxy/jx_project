package yy.doctor.frag;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.yy.Notifier.NotifyType;
import lib.yy.frag.base.BaseSRListFrag;
import lib.yy.network.ListResult;
import yy.doctor.R;
import yy.doctor.activity.NoticeActivity;
import yy.doctor.activity.me.UnitNumDetailActivity.AttentionUnitNum;
import yy.doctor.activity.meeting.search.SearchActivity;
import yy.doctor.adapter.HomeAdapter;
import yy.doctor.adapter.HomeUnitNumAdapter.onAttentionListener;
import yy.doctor.model.NoticeSize;
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
public class HomeFrag extends BaseSRListFrag<IHome, HomeAdapter> implements onAttentionListener {

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
        bar.addViewRight(v, v12 -> startActivity(SearchActivity.class));

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
        mBadgeView.setBadgeMargin(0, 8, 8, 0);
        mBadgeView.setTargetView(mViewNotice);
        //判断小红点是否出现
        if (NoticeSize.homeInst().size() > 0) {
            showView(mBadgeView);
        } else {
            hideView(mBadgeView);
        }

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

            //数据分组  推荐会议
            List<IHome> threeMeetings = new ArrayList<>();
            List<IHome> fiveMeetings = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                threeMeetings.add(mRecMeetings.get(i));
            }
            for (int i = 3; i < mRecMeetings.size(); ++i) {
                fiveMeetings.add(mRecMeetings.get(i));
            }

            homes.addAll(threeMeetings);

            RecUnitNums nums = new RecUnitNums();

            nums.setData(mRecUnitNums);
            homes.add(nums);

            homes.addAll(fiveMeetings);

            ret.setData(homes);
            super.onNetworkSuccess(id, ret);
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.unit_num_attention_change) {
            AttentionUnitNum attentionUnitNum = (AttentionUnitNum) data;
            int unitNumId = attentionUnitNum.getUnitNumId();
            int attention = attentionUnitNum.getAttention();
            for (int i = 0; i < mRecUnitNums.size(); ++i) {
                RecUnitNum item = mRecUnitNums.get(i);
                if (item.getInt(TRecUnitNum.id) == unitNumId) {
                    item.put(TRecUnitNum.attention, attention);
                    getAdapter().refreshAttentionState(i, attention);
                    break;
                }
            }
        } else if (type == NotifyType.receiver_notice) {
            //显示小红点
            showView(mBadgeView);
        } else if (type == NotifyType.read_all_notice) {
            //隐藏小红点
            hideView(mBadgeView);
        }
    }

    @Override
    public void onAttentionChanged(int attention, int unitNumId) {
        exeNetworkReq(KReqIdAttention, NetFactory.attention(unitNumId, KAttention));
    }

    @Override
    public void onDataSetChanged() {
        super.onDataSetChanged();

        getAdapter().setTvAttentionListener(this);
    }

}
