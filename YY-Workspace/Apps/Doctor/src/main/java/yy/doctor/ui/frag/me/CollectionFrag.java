package yy.doctor.ui.frag.me;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.Constants.FileSuffix;
import yy.doctor.adapter.data.DataUnitAdapter;
import yy.doctor.model.data.DataUnit;
import yy.doctor.model.data.DataUnit.FileOpenType;
import yy.doctor.model.data.DataUnit.TDataUnit;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.data.DataUnitDetailActivityRouter;
import yy.doctor.ui.activity.data.DownloadFileActivityRouter;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.util.CacheUtil;

/**
 * @auther yuansui
 * @since 2017/8/9
 */
@Route
public class CollectionFrag extends BaseSRListFrag<DataUnit, DataUnitAdapter> {

    @Arg(defaultInt = DataType.un_know)
    int mType;

    @Override
    public void initData() {
        if (mType == DataType.un_know) {
            throw new IllegalStateException("收藏类型未知");
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
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

            @FileOpenType int type = item.getInt(TDataUnit.openType);
            YSLog.d(TAG, type + "");
            if (type == FileOpenType.pdf) {
                DownloadFileActivityRouter.create()
                        .filePath(filePath)
                        .fileName(fileName)
                        .url(url)
                        .fileSuffix(FileSuffix.KPdf)
                        .fileSize(fileSize)
                        .dataType(mType)
                        .dataFileId(dataFileId)
                        .route(getContext());
            } else if (type == FileOpenType.details) {
                DataUnitDetailActivityRouter.create(
                        dataFileId, fileName, mType
                )
                        .route(getContext());
            }

        });
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.collection(getOffset(), getLimit(), mType));
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        boolean removeState = (type == NotifyType.collection_cancel_thomson && mType == DataType.thomson)
                || (type == NotifyType.collection_cancel_drug && mType == DataType.drug)
                || (type == NotifyType.collection_cancel_clinic && mType == DataType.clinic);

        //取消收藏后，收藏列表要删除对应的药品
        if (removeState) {
            String id = (String) data;
            for (DataUnit unit : getData()) {
                if (id.equals(unit.getString(TDataUnit.id))) {
                    getData().remove(unit);
                    invalidate();
                    break;
                }
            }
        }
    }
}
