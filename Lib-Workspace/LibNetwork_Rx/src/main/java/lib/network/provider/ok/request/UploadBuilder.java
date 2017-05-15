package lib.network.provider.ok.request;

import android.content.Context;
import android.os.Environment;

import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import lib.network.LogNetwork;
import lib.network.Network;
import lib.network.model.NetworkMethod;
import lib.network.model.NetworkReq;
import lib.network.model.OnNetworkListener;
import lib.network.param.NameByteValuePair;
import lib.network.param.NameFileValuePair;

/**
 * @author yuansui
 */
public class UploadBuilder extends PostBuilder {

    public UploadBuilder(NetworkReq request, Object tag, int id, OnNetworkListener listener) {
        super(request, tag, id, listener);
    }

    @Override
    protected OkHttpRequestBuilder initBuilder() {
        PostFormBuilder builder = (PostFormBuilder) super.initBuilder();

        List<NameByteValuePair> byteParams = request().getByteParams();
        if (byteParams != null) {
            Observable.fromIterable(byteParams)
                    .subscribe(p -> builder.addFile(p.getName(), p.getName(), DeleteOnExit.inst().add(tag(), id(), p.getValue())));
        }

        List<NameFileValuePair> fileParams = request().getFileParams();
        if (fileParams != null) {
            Observable.fromIterable(fileParams)
                    .subscribe(p -> builder.addFile(p.getName(), p.getValue(), new File(p.getValue())));
        }

        return builder;
    }

    @Override
    @NetworkMethod
    public int method() {
        return NetworkMethod.upload;
    }

    private static class DeleteUnit {
        public String mPath;
        public Object mTag;
        public int mId;

        public DeleteUnit(Object tag, int id, String path) {
            mTag = tag;
            mId = id;
            mPath = path;
        }
    }

    private static String getBasePath(Context context) {
        String path;
        // 兼容6.0的文件动态权限问题, 尽量不进行申请, 某些机型申请不过
        File diskRootFile = null;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)
                && !state.equals(Environment.MEDIA_SHARED)) {
            diskRootFile = context.getExternalCacheDir();
        } else {
            diskRootFile = context.getCacheDir();
        }

        if (diskRootFile != null) {
            path = diskRootFile.getPath();
        } else {
            throw new IllegalArgumentException("disk is invalid");
        }
        return path;
    }

    public static class DeleteOnExit {

        public String KTmpSuffix = ".tmp";
        public String KTmpPrefix = "/network_upload_tmp";

        private List<DeleteUnit> mUnits;

        private String mPrefix;

        private static DeleteOnExit mSelf;

        private DeleteOnExit() {
            mUnits = new ArrayList<>();

            mPrefix = getBasePath(Network.getContext()) + KTmpPrefix;
            File file = new File(mPrefix);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        public static DeleteOnExit inst() {
            if (mSelf == null) {
                mSelf = new DeleteOnExit();
            }
            return mSelf;
        }

        public File add(Object tag, int id, byte[] bytes) {
            File file = bytesToFile(bytes, mPrefix, bytes.hashCode() + KTmpSuffix);
            mUnits.add(new DeleteUnit(tag, id, file.getAbsolutePath()));
            return file;
        }

        public void delete(Object tag, int id) {
            if (mUnits.isEmpty()) {
                return;
            }

            for (DeleteUnit unit : mUnits) {
                if (unit.mTag.equals(tag) && unit.mId == id) {
                    File file = new File(unit.mPath);
                    file.delete();
                    break;
                }
            }
        }

        private File bytesToFile(byte[] bytes, String filePath, String fileName) {
            BufferedOutputStream bos = null;
            FileOutputStream fos = null;
            File file = null;
            try {
                File dir = new File(filePath);
                if (!dir.exists() && dir.isDirectory()) {
                    dir.mkdirs();
                }
                file = new File(filePath + File.separator + fileName);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(bytes);
            } catch (Exception e) {
                LogNetwork.e(e);
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        LogNetwork.e(e);
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        LogNetwork.e(e);
                    }
                }
            }

            return file;
        }
    }
}
