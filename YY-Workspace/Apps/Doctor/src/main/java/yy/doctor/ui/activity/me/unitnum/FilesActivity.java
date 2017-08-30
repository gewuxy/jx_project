package yy.doctor.ui.activity.me.unitnum;

import android.view.View;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.Extra.FileFrom;
import yy.doctor.R;
import yy.doctor.adapter.FileDataAdapter;
import yy.doctor.model.unitnum.FileData;
import yy.doctor.model.unitnum.FileData.TFileData;
import yy.doctor.network.NetworkAPISetter.MeetAPI;
import yy.doctor.network.NetworkAPISetter.UnitNumAPI;
import yy.doctor.ui.activity.data.DownloadFileActivityRouter;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.util.CacheUtil;
import yy.doctor.util.Util;

/**
 * 资料列表
 *
 * @author CaiXiang
 * @since 2017/5/3
 */
@Route
public class FilesActivity extends BaseSRListActivity<FileData, FileDataAdapter> {

    @Arg
    String mId;

    @Arg
    @FileFrom
    int mType;

    private String mFilePath;
    private String mFileId;

    private String mFileName;
    private String mFileUrl;
    private String mFileType;
    private long mFileSize;


    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, R.string.file_data, this);
    }

    @Override
    public void setViews() {
        super.setViews();

        if (mType == FileFrom.unit_num) {
            mFilePath = CacheUtil.getUnitNumCacheDir(mId);
        } else {
            mFilePath = CacheUtil.getMeetingCacheDir(mId);
        }
    }

    @Override
    public void getDataFromNet() {
        if (mType == FileFrom.unit_num) {
            exeNetworkReq(UnitNumAPI.unitNumData(mId, getOffset(), getLimit()).build());
        } else {
            exeNetworkReq(MeetAPI.meetData(mId, getOffset(), getLimit()).build());
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);

        FileData item = getItem(position);

        mFileSize = item.getLong(TFileData.fileSize);
        mFileName = item.getString(TFileData.materialName);
        mFileId = item.getString(TFileData.id);
        if (TextUtil.isEmpty(mFileName)) {
            mFileName = item.getString(TFileData.name);
            mFileUrl = item.getString(TFileData.fileUrl);
            mFileType = item.getString(TFileData.fileType);
        } else {
            mFileName = item.getString(TFileData.materialName);
            mFileUrl = item.getString(TFileData.materialUrl);
            mFileType = item.getString(TFileData.materialType);
        }

        DownloadFileActivityRouter.create()
                .filePath(mFilePath)
                .fileName(mFileName)
                .url(mFileUrl)
                .fileSuffix(mFileType)
                .fileSize(mFileSize)
                .dataFileId(mFileId)
                .dataType(DataType.un_know)
                .route(this);
    }

}
