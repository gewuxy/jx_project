package yy.doctor.ui.frag.collection;

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

import static yy.doctor.model.data.DataUnit.TDataUnit.openType;

/**
 * @auther WangLan
 * @since 2017/8/9
 */

public class CollectionThomsonsFrag extends BaseSRListFrag<DataUnit, DataUnitAdapter> {

    private int mType = 1; // type为1，表示汤森路透

    @Override
    public void initData() {

    }

    @Override
    public void setViews() {
        super.setViews();

        setOnAdapterClickListener((position, v) -> {
            DataUnit item = getItem(position);
            String dataFileId = item.getString(TDataUnit.id);
            String fileName = item.getString(TDataUnit.title);

            long fileSize = item.getInt(TDataUnit.fileSize) * 1024;
            String filePath = CacheUtil.getThomsonCacheDir(item.getString(TDataUnit.id));
            String url = item.getString(TDataUnit.filePath);

            int type = item.getInt(openType);
            YSLog.d(TAG, type + "");
            if (type == 1) {
                DownloadFileActivityIntent.create()
                        .filePath(filePath)
                        .fileName(fileName)
                        .url(url)
                        .type("pdf")
                        .fileSize(fileSize)
                        .dataFileId(dataFileId)
                        .start(getContext());
            } else if (type == 2) {
                DrugDetailActivityIntent.create()
                        .dataFileId(dataFileId)
                        .fileName(fileName)
                        .start(getContext());
            }

        });
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
    public void onNotify(@NotifyType int type, Object data) {

        //取消收藏后，收藏列表要删除对应的药品
        if (type == NotifyType.getCancel_collection_thomson) {
            String thomsonId = (String) data;
            List<DataUnit> list = getData();
            for (DataUnit td : list) {
                if (thomsonId.equals(td.getString(TDataUnit.id))) {
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


    @Override
    public void initNavBar(NavBar bar) {

    }
}
