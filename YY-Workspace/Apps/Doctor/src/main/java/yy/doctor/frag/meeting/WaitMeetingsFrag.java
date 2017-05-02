package yy.doctor.frag.meeting;

import org.json.JSONException;

import lib.ys.network.resp.IListResponse;

/**
 * 会议等待中列表
 *
 * 日期 : 2017/4/24
 * 创建人 : guoxuan
 */

public class WaitMeetingsFrag extends BaseMeetingsFrag {
    @Override
    public void initData() {
        for (int i = 0; i < 2; ++i) {
            addItem(i + "");
        }
    }

    @Override
    public void getDataFromNet() {

    }

    @Override
    public IListResponse<String> parseNetworkResponse(int id, String text) throws JSONException {
        return null;
    }
}
