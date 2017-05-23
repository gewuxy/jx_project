package yy.doctor.frag;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseSRListFrag;
import lib.yy.network.ListResult;
import yy.doctor.R;
import yy.doctor.activity.NoticeActivity;
import yy.doctor.adapter.HomeAdapter;
import yy.doctor.model.home.Banner;
import yy.doctor.model.home.IHome;
import yy.doctor.model.home.RecMeeting;
import yy.doctor.model.home.RecUnitNum;
import yy.doctor.model.home.RecUnitNums;
import yy.doctor.network.NetFactory;
import yy.doctor.view.BannerView;

import static lib.yy.network.BaseJsonParser.evs;

/**
 * @author CaiXiang   extends BaseSRListFrag<Home>
 * @since 2017/4/5
 */
public class HomeFrag extends BaseSRListFrag<IHome, HomeAdapter> {

    private static final int KReqBannerId = 1;
    private static final int KReqMeetingId = 2;
    private static final int KReqUnitNumId = 3;

    private boolean mBannerReqIsOK = false;
    private boolean mUnitNumReqIsOK = false;
    private boolean mMeetingReqIsOK = false;

    private List<RecUnitNum> mRecUnitNums;
    private List<IHome> mRecMeetings;

    private EditText mEtSearch;
    private BannerView mBannerView;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        View v = inflate(R.layout.layout_home_nav_bar);
        bar.addViewRight(v, null);
        bar.addViewRight(R.mipmap.nav_bar_ic_notice, new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(NoticeActivity.class);
            }
        });
    }

    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_home_header);
    }

    @Override
    public void findViews() {
        super.findViews();

        mEtSearch = findView(R.id.home_nav_bar_et);
        mBannerView = findView(R.id.home_header_banner);
    }

    @Override
    public void setViews() {
        super.setViews();
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(KReqBannerId, NetFactory.banner());
        exeNetworkReq(KReqUnitNumId, NetFactory.recommendUnitNum());
        exeNetworkReq(KReqMeetingId, NetFactory.recommendMeeting());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        ListResult result = null;
        if (id == KReqBannerId) {
            result = evs(r.getText(), Banner.class);
            mBannerReqIsOK = result.isSucceed();
            if (mBannerReqIsOK) {
                mBannerView.setData(result.getData());
            }
        } else if (id == KReqUnitNumId) {
            result =  evs(r.getText(), RecUnitNum.class);
            mUnitNumReqIsOK = result.isSucceed();
            if (mUnitNumReqIsOK) {
                mRecUnitNums = result.getData();
            }
        } else if (id == KReqMeetingId) {
            result =  evs(r.getText(), RecMeeting.class);
            mMeetingReqIsOK = result.isSucceed();
            if (mMeetingReqIsOK) {
                mRecMeetings = result.getData();
            }
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
            return ret;
        } else {
            // do nothing
        }
        return result;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (mBannerReqIsOK && mUnitNumReqIsOK && mMeetingReqIsOK) {
            super.onNetworkSuccess(id, result);
        }
    }
}
