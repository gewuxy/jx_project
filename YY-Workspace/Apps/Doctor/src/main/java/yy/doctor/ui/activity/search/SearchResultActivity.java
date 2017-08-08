package yy.doctor.ui.activity.search;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.processor.annotation.AutoIntent;
import lib.yy.network.ListResult;
import yy.doctor.model.meet.Meeting;
import yy.doctor.model.search.IRec;
import yy.doctor.model.search.Margin;
import yy.doctor.model.search.More;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 单位号和会议的结果
 *
 * @auther yuansui
 * @since 2017/6/12
 */
@AutoIntent
public class SearchResultActivity extends BaseSearchResultActivity {

    protected final int KMeeting = 0; // 搜索会议
    protected final int KUnitNum = 1; // 搜索单位号

    private final int KMeetingNum = 3;
    private final int KUnitNumNum = 3;

    protected List<IRec> mMeets;
    protected List<IRec> mUnitNums;

    private boolean mMeetReqIsOK;
    private boolean mUnitNumReqIsOK;

    @Override
    protected CharSequence getSearchHint() {
        return "搜索单位号或者会议";
    }

    @Override
    public void getDataFromNet() {
        if (mMeets != null) {
            mMeets.clear();
        }
        mMeetReqIsOK = false;
        exeNetworkReq(KMeeting, NetFactory.searchMeeting(mSearchContent, getOffset(), KMeetingNum + 1));
        if (mUnitNums != null) {
            mUnitNums.clear();
        }
        mUnitNumReqIsOK = false;
        exeNetworkReq(KUnitNum, NetFactory.searchUnitNum(mSearchContent, getOffset(), KUnitNumNum + 1));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        ListResult result = null;
        if (id == KMeeting) {
            // 会议
            result = JsonParser.evs(r.getText(), Meeting.class);
            // onNetworkSuccess接数据可能为空 ,因为这不在主线程
            if (result.isSucceed()) {
                mMeets = result.getData();
            }
        } else {
            // 单位号
            result = JsonParser.evs(r.getText(), UnitNum.class);
            if (result.isSucceed()) {
                mUnitNums = result.getData();
            }
        }
        return result;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        ListResult r = (ListResult) result;

        if (id == KMeeting) {
            // 会议
            mMeetReqIsOK = r.isSucceed();
        } else {
            // 单位号
            mUnitNumReqIsOK = r.isSucceed();
        }

        if (mMeetReqIsOK && mUnitNumReqIsOK) {
            boolean haveUnitNum = mUnitNums != null && mUnitNums.size() > 0;
            boolean haveMeet = mMeets != null && mMeets.size() > 0;

            ListResult<IRec> listResult = new ListResult<>();
            List<IRec> data = new ArrayList<>();

            // 先添加单位号
            if (haveUnitNum) {
                toggleResult(data, mUnitNums, KUnitNumNum);
            }

            // 如果单位号和会议都有才添加分割线
            if (haveUnitNum && haveMeet) {
                data.add(new Margin());
            }

            // 再添加会议的
            if (haveMeet) {
                toggleResult(data, mMeets, KMeetingNum);
            }

            listResult.setData(data);
            super.onNetworkSuccess(id, listResult);
        }
    }

    /**
     * 拼接结果
     *
     * @param result
     * @param data
     * @param num
     */
    private void toggleResult(List<IRec> result, List<IRec> data, int num) {
        if (data.size() > num) {
            // 超过三个显示三个和更多
            for (int i = 0; i < num; i++) {
                result.add(data.get(i));
            }
            // 显示更多
            result.add(new More());
        } else {
            // 小于三个直接加全部
            result.addAll(data);
        }
    }

    protected String getEmptyText() {
        return "暂无相关单位号或者会议";
    }

}