package jx.doctor.ui.activity.search;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import inject.annotation.router.Route;
import lib.network.model.interfaces.IResult;
import lib.yy.network.Result;
import jx.doctor.model.meet.Meeting;
import jx.doctor.model.search.IRec;
import jx.doctor.model.search.Margin;
import jx.doctor.model.search.More;
import jx.doctor.model.unitnum.UnitNum;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.network.NetworkApiDescriptor.UnitNumAPI;

/**
 * 单位号和会议的结果
 *
 * @auther yuansui
 * @since 2017/6/12
 */
@Route
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
        exeNetworkReq(KMeeting, MeetAPI.searchMeet(mSearchContent, getOffset(), KMeetingNum + 1).build());
        if (mUnitNums != null) {
            mUnitNums.clear();
        }
        mUnitNumReqIsOK = false;
        exeNetworkReq(KUnitNum, UnitNumAPI.searchUnitNum(mSearchContent, getOffset(), KUnitNumNum + 1).build());
    }

    @Override
    public IResult parseNetworkResponse(int id, String text) throws JSONException {
        Result result = null;
        if (id == KMeeting) {
            // 会议
            result = JsonParser.evs(text, Meeting.class);
            // onNetworkSuccess接数据可能为空 ,因为这不在主线程
            if (result.isSucceed()) {
                mMeets = result.getList();
            }
        } else {
            // 单位号
            result = JsonParser.evs(text, UnitNum.class);
            if (result.isSucceed()) {
                mUnitNums = result.getList();
            }
        }
        return result;

    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (id == KMeeting) {
            // 会议
            mMeetReqIsOK = r.isSucceed();
        } else {
            // 单位号
            mUnitNumReqIsOK = r.isSucceed();
        }

        if (mMeetReqIsOK && mUnitNumReqIsOK) {
            boolean haveUnitNum = mUnitNums != null && mUnitNums.size() > 0;  // 单位号有数据
            boolean haveMeet = mMeets != null && mMeets.size() > 0; // 会议有数据

            Result<IRec> Result = new Result<>();
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

            Result.setData(data);
            super.onNetworkSuccess(id, Result);
        }
    }

    /**
     * 拼接结果
     *
     * @param result 接收
     * @param data 提供
     * @param num 需要的展示的数据数量
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