package yy.doctor.ui.frag;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.yy.network.ListResult;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.R;
import yy.doctor.adapter.home.HomeAdapter;
import yy.doctor.adapter.home.HomeUnitNumAdapter.onAttentionListener;
import yy.doctor.model.home.Banner;
import yy.doctor.model.home.IHome;
import yy.doctor.model.home.RecMeeting;
import yy.doctor.model.home.RecUnitNum;
import yy.doctor.model.home.RecUnitNum.Attention;
import yy.doctor.model.home.RecUnitNum.TRecUnitNum;
import yy.doctor.model.home.RecUnitNums;
import yy.doctor.model.notice.NoticeNum;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.CommonAPI;
import yy.doctor.network.NetworkAPISetter.MeetAPI;
import yy.doctor.network.NetworkAPISetter.UnitNumAPI;
import yy.doctor.ui.activity.home.NoticeActivity;
import yy.doctor.ui.activity.me.unitnum.UnitNumDetailActivity.AttentionUnitNum;
import yy.doctor.ui.activity.search.SearchActivity;
import yy.doctor.view.BadgeView;
import yy.doctor.view.BannerView;

/**
 * 首页
 *
 * @author CaiXiang   extends BaseSRListFrag<Home>
 * @since 2017/4/5
 */
public class HomeFrag extends BaseSRListFrag<IHome, HomeAdapter> implements onAttentionListener {

    private final int KReqIdBanner = 1;
    private final int KReqIdMeeting = 2;
    private final int KReqIdUnitNum = 3;
    private final int KReqIdAttention = 4;

    private final int KFirstSection = 3;
    private final int KSecondSection = 5;

    private final int KBadgeMarginTop = 8;
    private final int KBadgeMarginLeft = 0;

    private final int KLimit = 8;

    private boolean mBannerReqIsOK = false;
    private boolean mUnitNumReqIsOK = false;
    private boolean mMeetingReqIsOK = false;
    private boolean mIsLoadFirstPage = true;  //  是否是在加载第一页数据
    private boolean mIsSwipeRefresh = false;  // 是否正在下拉刷新

    private boolean mIsNetworkError = false;  // 是否网络错误

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
        bar.addViewMid(inflate(R.layout.layout_home_nav_bar_search), v -> startActivity(SearchActivity.class));
        mViewNotice = bar.addViewRight(R.drawable.nav_bar_ic_notice, v -> startActivity(NoticeActivity.class));
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

        exeNetworkReq(KReqIdBanner, CommonAPI.banner().build());
        exeNetworkReq(KReqIdUnitNum, UnitNumAPI.recommendUnitNum().build());
    }

    @Override
    public void getDataFromNet() {
        if (initComplete()) {
            mMeetingReqIsOK = false;
        }
        exeNetworkReq(KReqIdMeeting, MeetAPI.recommendMeeting(getOffset(), getLimit()).build());
    }

    @Override
    public void onSwipeRefreshAction() {
        //重置数据
        mBannerReqIsOK = false;
        mUnitNumReqIsOK = false;
        mMeetingReqIsOK = false;
        mIsLoadFirstPage = true;
        mIsSwipeRefresh = true;

        exeNetworkReq(KReqIdBanner, CommonAPI.banner().build());
        exeNetworkReq(KReqIdUnitNum, UnitNumAPI.recommendUnitNum().build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {

        if (id == KReqIdAttention) {
            return true;
        }

        ListResult result = null;
        if (id == KReqIdBanner) {
            result = JsonParser.evs(r.getText(), Banner.class);
            if (result.isSucceed()) {
                mBanners = result.getData();
            }
        } else if (id == KReqIdUnitNum) {
            result = JsonParser.evs(r.getText(), RecUnitNum.class);
            if (result.isSucceed()) {
                mRecUnitNums = result.getData();
            }
        } else if (id == KReqIdMeeting) {
            result = JsonParser.evs(r.getText(), RecMeeting.class);
            if (result.isSucceed()) {
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

        ListResult r = (ListResult) result;

        if (!r.isSucceed()) {

            //刷新出现错误时不能显示加载错误  不做处理，还是显示以前的数据
            if (mIsSwipeRefresh) {
                stopSwipeRefresh();
                mIsSwipeRefresh = false;
                return;
            }

            //3个网络请求中只要有一个错误，就要显示加载错误
            if (!mIsNetworkError) {
                YSLog.d(TAG, "network error id = " + id);
                mIsNetworkError = true;
                onNetworkError(id, r.getError());
            }
        }

        if (id == KReqIdBanner) {
            mBannerReqIsOK = r.isSucceed();
        } else if (id == KReqIdUnitNum) {
            mUnitNumReqIsOK = r.isSucceed();
        } else if (id == KReqIdMeeting) {
            mMeetingReqIsOK = r.isSucceed();
        }

        // 确保所有数据都已经获取才拼接数据
        if (mBannerReqIsOK && mUnitNumReqIsOK && mMeetingReqIsOK) {

            if (mBanners != null && mBanners.size() > 0) {
                mBannerView.setData(mBanners);
                // 设置当前第一页的位置偏移, 支持初始化后直接手势右滑
                mBannerView.initCurrentItem(mBanners.size() * 50);
            }

            ListResult<IHome> ret = new ListResult();
            List<IHome> homes = new ArrayList<>();

            // 第一次和下拉刷新加载需要拼接数据， 分页加载时不需要
            if (mIsLoadFirstPage) {

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

                if (mRecUnitNums != null && mRecUnitNums.size() > 0) {
                    RecUnitNums nums = new RecUnitNums();
                    nums.setData(mRecUnitNums);
                    homes.add(nums);
                }

                homes.addAll(secondSectionMeetings);

                mIsLoadFirstPage = false;
            } else {
                homes.addAll(mRecMeetings);
            }

            ret.setData(homes);
            super.onNetworkSuccess(id, ret);
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        stopSwipeRefresh();
        setViewState(ViewState.error);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.unit_num_attention_change) {
            // 关注  取消关注后，首页对应的单位号的状态也要改变
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
        exeNetworkReq(KReqIdAttention, UnitNumAPI.attention(unitNumId, Attention.yes).build());
    }

    @Override
    public void onDataSetChanged() {
        super.onDataSetChanged();

        getAdapter().setTvAttentionListener(this);
    }

    @Override
    public boolean onRetryClick() {
        super.onRetryClick();

        //点击重新加载的时候，只会执行getDataFromNet（）方法，所有需要添加另外两个网络请求
        exeNetworkReq(KReqIdBanner, CommonAPI.banner().build());
        exeNetworkReq(KReqIdUnitNum, UnitNumAPI.recommendUnitNum().build());
        mIsNetworkError = false;

        return false;
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

    @Override
    public int getLimit() {
        return KLimit;
    }
}
