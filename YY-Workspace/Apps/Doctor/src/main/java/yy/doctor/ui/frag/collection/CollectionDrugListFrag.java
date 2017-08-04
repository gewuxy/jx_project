package yy.doctor.ui.frag.collection;

import android.support.annotation.IntDef;
import android.view.View;

import java.util.List;

import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.adapter.CollectionDrugAdapter;
import yy.doctor.model.data.ThomsonDetail;
import yy.doctor.model.data.ThomsonDetail.TThomsonDetail;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.data.DrugDetailActivity;
import yy.doctor.ui.activity.me.DownloadDataActivity;
import yy.doctor.util.CacheUtil;

/**
 * @auther WangLan
 * @since 2017/7/29
 */

public class CollectionDrugListFrag extends BaseSRListFrag<ThomsonDetail, CollectionDrugAdapter> {

    private int mType = 2; // type为2，表示药品目录

    @IntDef({
            openType.pdf,
            openType.detail_interface,
            openType.html,
    })

    private @interface openType{
        int pdf = 1;
        int detail_interface = 2;
        int html = 3;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {

    }


    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.collection(getOffset(), getLimit(), mType));
    }

    @Override
    public int getLimit() {
        return 500;
    }

    @Override
    public void onItemClick(View v, int position) {
        ThomsonDetail item = getItem(position);
        String dataFileId = item.getString(TThomsonDetail.id);
        String fileName= item.getString(TThomsonDetail.title);
        long fileSize = item.getInt(TThomsonDetail.fileSize) * 1024;
        String filePath = CacheUtil.getThomsonCacheDir(item.getString(TThomsonDetail.categoryId));
        String url = item.getString(TThomsonDetail.filePath);

        int type = item.getInt(TThomsonDetail.openType);
        YSLog.d(TAG,type+"");
        if (type == openType.pdf) {
            DownloadDataActivity.nav(getContext(), filePath, fileName, url, "pdf", fileSize,dataFileId);
        }else if (type == openType.detail_interface) {
            DrugDetailActivity.nav(getContext(),dataFileId,fileName);
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        //取消收藏后，收藏列表要删除对应的药品
        if (type == NotifyType.getCancel_collection_drug) {
            String drugId = (String) data;
            List<ThomsonDetail> list = getData();
            for (ThomsonDetail td : list) {
                if (drugId.equals(td.getString(TThomsonDetail.id))) {
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
