package yy.doctor.ui.frag.collection;

import android.support.annotation.IntDef;
import android.view.View;

import java.util.List;

import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.adapter.data.DataUnitAdapter;
import yy.doctor.model.data.DataUnit;
import yy.doctor.model.data.DataUnit.TDataUnit;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.data.DownloadFileActivityIntent;
import yy.doctor.ui.activity.data.DrugDetailActivityIntent;
import yy.doctor.util.CacheUtil;

/**
 * @auther WangLan
 * @since 2017/7/29
 */

public class CollectionDrugListFrag extends BaseSRListFrag<DataUnit, DataUnitAdapter> {

    private int mType = 2; // type为2，表示药品目录

    @IntDef({
            openType.pdf,
            openType.detail_interface,
            openType.html,
    })

    private @interface openType {
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
        DataUnit item = getItem(position);
        String dataFileId = item.getString(TDataUnit.id);
        String fileName = item.getString(TDataUnit.title);

        long fileSize = item.getInt(TDataUnit.fileSize) * 1024;
        String filePath = CacheUtil.getThomsonCacheDir(item.getString(TDataUnit.id));
        String url = item.getString(TDataUnit.filePath);

        int type = item.getInt(TDataUnit.openType);
        YSLog.d(TAG, type + "");
        if (type == openType.pdf) {
            DownloadFileActivityIntent.create()
                    .filePath(filePath)
                    .fileName(fileName)
                    .url(url)
                    .type("pdf")
                    .fileSize(fileSize)
                    .dataFileId(dataFileId)
                    .start(getContext());
        } else if (type == openType.detail_interface) {
            DrugDetailActivityIntent.create()
                    .dataFileId(dataFileId)
                    .fileName(fileName)
                    .start(getContext());
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        //取消收藏后，收藏列表要删除对应的药品
        if (type == NotifyType.getCancel_collection_drug) {
            String drugId = (String) data;
            List<DataUnit> list = getData();
            for (DataUnit td : list) {
                if (drugId.equals(td.getString(TDataUnit.id))) {
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
