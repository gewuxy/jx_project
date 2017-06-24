package yy.doctor.frag;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.YSLog;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import lib.yy.network.ListResult;
import yy.doctor.R;
import yy.doctor.activity.home.NoticeActivity;
import yy.doctor.activity.me.unitnum.UnitNumDetailActivity.AttentionUnitNum;
import yy.doctor.activity.search.SearchActivity;
import yy.doctor.adapter.HomeAdapter;
import yy.doctor.adapter.HomeUnitNumAdapter.onAttentionListener;
import yy.doctor.model.home.Banner;
import yy.doctor.model.home.IHome;
import yy.doctor.model.home.RecMeeting;
import yy.doctor.model.home.RecUnitNum;
import yy.doctor.model.home.RecUnitNum.TRecUnitNum;
import yy.doctor.model.home.RecUnitNums;
import yy.doctor.model.notice.NoticeNum;
import yy.doctor.network.NetFactory;
import yy.doctor.view.BadgeView;
import yy.doctor.view.BannerView;

import static lib.yy.network.BaseJsonParser.evs;

/**
 * 首页
 *
 * @author CaiXiang   extends BaseSRListFrag<Home>
 * @since 2017/4/5
 */
public class HomeFrag extends BaseSRListFrag<IHome, HomeAdapter> implements onAttentionListener {

    private static final int KReqIdBanner = 1;
    private static final int KReqIdMeeting = 2;
    private static final int KReqIdUnitNum = 3;
    private static final int KReqIdAttention = 4;
    private static final int KAttention = 1;  //关注单位号

    private static final int KFirstSection = 3;
    private static final int KSecondSection = 5;

    private static final int KBadgeMarginTop = 8;
    private static final int KBadgeMarginLeft = 0;

    private boolean mBannerReqIsOK = false;
    private boolean mUnitNumReqIsOK = false;
    private boolean mMeetingReqIsOK = false;
    private boolean mIsFirstLoad = true;
    private boolean mIsSwipeRefresh = false;

    private boolean mIsNetworkError = false;

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
        mBadgeView.setBadgeMargin(KBadgeMarginLeft, KBadgeMarginTop, KBadgeMarginTop, KBadgeMarginLeft);
        mBadgeView.setTargetView(mViewNotice);
        //判断小红点是否出现
        YSLog.d(TAG, " 小红点个数 = " + NoticeNum.inst().getCount());
        if (NoticeNum.inst().getCount() > 0) {
            showView(mBadgeView);
        } else {
            hideView(mBadgeView);
        }

        exeNetworkReq(KReqIdBanner, NetFactory.banner());
        exeNetworkReq(KReqIdUnitNum, NetFactory.recommendUnitNum());
    }

    @Override
    public void getDataFromNet() {
        if (initComplete()) {
            mMeetingReqIsOK = false;
        }
        exeNetworkReq(KReqIdMeeting, NetFactory.recommendMeeting(getOffset(), getLimit()));
    }

    @Override
    public void onSwipeRefresh() {
        super.onSwipeRefresh();

        //重置数据
        mBannerReqIsOK = false;
        mUnitNumReqIsOK = false;
        mMeetingReqIsOK = false;
        mIsFirstLoad = true;
        mIsSwipeRefresh = true;

        exeNetworkReq(KReqIdBanner, NetFactory.banner());
        exeNetworkReq(KReqIdUnitNum, NetFactory.recommendUnitNum());
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
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KReqIdAttention) {
            return;
        }

        //判断是否所有数据都已经获取   刷新出现错误时不能显示加载错误  不做处理
        if (mIsSwipeRefresh) {
            if (!(mBanners != null && mRecUnitNums != null && mRecMeetings != null)) {
                stopSwipeRefresh();
                return;
            }
        }
        mIsSwipeRefresh = false;

        //确保所有数据都已经获取
        ListResult r = (ListResult) result;

        if (!r.isSucceed()) {
            if (!mIsNetworkError) {
                onNetworkError(id, new NetError(id, r.getError()));
                YSLog.d(TAG, "network error id = " + id);
                mIsNetworkError = true;
            }
        }
        if (id == KReqIdBanner) {
            mBannerReqIsOK = r.isSucceed();
            mBannerView.setData(mBanners);
            // 设置当前第一页的位置偏移, 支持初始化后直接手势右滑
            mBannerView.initCurrentItem(mBanners.size() * 100);
        } else if (id == KReqIdUnitNum) {
            mUnitNumReqIsOK = r.isSucceed();
        } else if (id == KReqIdMeeting) {
            mMeetingReqIsOK = r.isSucceed();
        }

        // 拼接数据
        if (mBannerReqIsOK && mUnitNumReqIsOK && mMeetingReqIsOK) {
            ListResult<IHome> ret = new ListResult();
            List<IHome> homes = new ArrayList<>();

            // 第一次和下拉刷新加载需要拼接数据， 分页加载时不需要
            if (mIsFirstLoad) {
                //数据分组  推荐会议
                List<IHome> firstSectionMeetings = new ArrayList<>();
                List<IHome> secondSectionMeetings = new ArrayList<>();

                int index = 0;
                int size = mRecMeetings.size();
                for (int i = 0; i < KFirstSection && i < size; i++) {
                    firstSectionMeetings.add(mRecMeetings.get(i));
                    index++;
                }
                for (int i = index; i < (KSecondSection + index) && i < size; ++i) {
                    secondSectionMeetings.add(mRecMeetings.get(i));
                }
                homes.addAll(firstSectionMeetings);

                RecUnitNums nums = new RecUnitNums();
                nums.setData(mRecUnitNums);
                homes.add(nums);

                homes.addAll(secondSectionMeetings);

                mIsFirstLoad = false;
            } else {
                homes.addAll(mRecMeetings);
            }

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

    @Override
    public boolean onRetryClick() {
        mIsNetworkError = false;
        return super.onRetryClick();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBannerView != null) {
            mBannerView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mBannerView != null) {
            mBannerView.onPause();
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();

        if (mBannerView != null) {
            mBannerView.onResume();
        }
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();

        if (mBannerView != null) {
            mBannerView.onPause();
        }
    }

}
