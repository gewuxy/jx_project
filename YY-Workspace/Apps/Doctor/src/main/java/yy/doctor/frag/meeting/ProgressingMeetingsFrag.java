package yy.doctor.frag.meeting;

import org.json.JSONException;

import lib.ys.network.resp.IListResponse;

/**
 * 会议进行中列表
 *
 * @auther yuansui
 * @since 2017/4/24
 */

public class ProgressingMeetingsFrag extends BaseMeetingsFrag {
    @Override
    public void initData() {
        for (int i = 0; i < 12; ++i) {
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
