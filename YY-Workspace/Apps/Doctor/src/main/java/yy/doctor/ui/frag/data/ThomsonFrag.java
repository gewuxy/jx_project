package yy.doctor.ui.frag.data;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.adapter.data.ThomsonAdapter;
import yy.doctor.model.data.ThomsonDetail;
import yy.doctor.model.data.ThomsonDetail.TThomsonDetail;
import yy.doctor.network.NetFactory;

/**
 * 汤森路透
 *
 * @author CaiXiang
 * @since 2017/4/24
 */
public class ThomsonFrag extends BaseSRListFrag<ThomsonDetail, ThomsonAdapter> {

    private int mType = 1; // type为1，表示汤森

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

  /*  @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_divider);
    }*/

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.collection(getOffset(), getLimit(),mType));
    }

    @Override
    public int getLimit() {
        return 500;
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        //取消收藏后，收藏列表要删除对应的药品
        if (type == NotifyType.getCancel_collection_thomson) {
            String thomsonId = (String) data;
            List<ThomsonDetail> list = getData();
            for (ThomsonDetail td : list) {
                if (thomsonId.equals(td.getString(TThomsonDetail.id))) {
                    getData().remove(td);
                    invalidate();
                    return;
                }
            }
        }
    }

    @Override
    public boolean enableInitRefresh() {
        return true;
    }

}
