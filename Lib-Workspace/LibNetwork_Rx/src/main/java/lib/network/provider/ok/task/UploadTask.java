package lib.network.provider.ok.task;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import lib.network.Network;
import lib.network.NetworkLog;
import lib.network.NetworkUtil;
import lib.network.model.NetworkReq;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.model.param.BytePair;
import lib.network.model.param.CommonPair;
import lib.network.model.param.FilePair;
import lib.network.provider.NativeListener;
import lib.network.provider.ok.callback.OkCallback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;


/**
 * @auther yuansui
 * @since 2017/6/10
 */

public class UploadTask extends Task {

    public UploadTask(int id, NetworkReq req, OkCallback callback) {
        super(id, req, callback);
    }

    @Override
    public Request buildRealReq() {
        String url = NetworkUtil.generateGetUrl(getReq().getUrl(), getReq().getParams());
        NetworkLog.d("url_upload = " + url);

        MultipartBody.Builder b = new Builder().setType(MultipartBody.FORM);

        List<BytePair> bytes = getReq().getByteParams();
        if (bytes != null) {
            for (BytePair p : bytes) {
                // FIXME: 图片流的上传只能使用存储到本地以后以文件形式上传(这样会残留本地图片文件在特定文件夹)
                File f = UploadFile.create(p.getVal());
                b.addFormDataPart(p.getName(), p.getName(), RequestBody.create(MultipartBody.FORM, f));
            }
        }

        List<FilePair> files = getReq().getFileParams();
        if (files != null) {
            for (FilePair p : files) {
                File f = new File(p.getVal());
                b.addFormDataPart(p.getName(), p.getName(), RequestBody.create(MultipartBody.FORM, f));
            }
        }

        List<CommonPair> params = getReq().getParams();
        if (params != null) {
            for (CommonPair p : params) {
                b.addFormDataPart(p.getName(), p.getVal());
            }
        }

        RequestBody body = new ProgressRequestBody(getId(), b.build(), getCallback().getListener());

        Request.Builder reqBuilder = new Request.Builder()
                .tag(getId())
                .url(getReq().getUrl())
                .post(body);

        addHeaders(reqBuilder);

        return reqBuilder.build();
    }

    public static class ProgressRequestBody extends RequestBody {
        private final RequestBody mBody;
        private final OnNetworkListener mListener;
        private BufferedSink mSink;

        private int mId;

        /**
         * 构造函数，赋值
         *
         * @param mBody 待包装的请求体
         * @param l     回调接口
         */
        public ProgressRequestBody(int id, RequestBody mBody, OnNetworkListener l) {
            this.mBody = mBody;
            this.mListener = l;
            mId = id;
        }

        /**
         * 重写调用实际的响应体的contentType
         *
         * @return MediaType
         */
        @Override
        public MediaType contentType() {
            return mBody.contentType();
        }

        /**
         * 重写调用实际的响应体的contentLength
         *
         * @return contentLength
         * @throws IOException 异常
         */
        @Override
        public long contentLength() throws IOException {
            return mBody.contentLength();
        }

        /**
         * 重写进行写入
         *
         * @param sink BufferedSink
         * @throws IOException 异常
         */
        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            if (mSink == null) {
                //包装
                mSink = Okio.buffer(sink(sink));
            }
            //写入
            mBody.writeTo(mSink);
            //必须调用flush，否则最后一部分数据可能不会被写入
            mSink.flush();

        }

        private Sink sink(Sink sink) {
            return new ForwardingSink(sink) {
                //当前写入字节数
                long mBytesWritten = 0;
                //总字节长度，避免多次调用contentLength()方法
                long mContentLength = 0;

                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    if (mContentLength == 0) {
                        mContentLength = contentLength();
                    }

                    //增加当前写入的字节数
                    mBytesWritten += byteCount;

                    //回调
                    if (mListener != null) {
                        float progress = (float) mBytesWritten / mContentLength;
                        NativeListener.inst().onProgress(mId, progress, mContentLength, mListener);
                    }
                }
            };
        }
    }

    public static class UploadFile {

        public static File create(byte[] bytes) {
            Builder b = new Builder(bytes);
            return b.build();
        }

        private static class Builder {
            public String KTmpSuffix = ".tmp";

            private String mFilePath;
            private byte[] mBytes;

            public Builder(byte[] bytes) {
                mFilePath = Network.getConfig().getCacheDir();
                if (mFilePath == null) {
                    throw new IllegalStateException("必须设置Network的cache dir");
                }

                File file = new File(mFilePath);
                if (!file.exists()) {
                    file.mkdirs();
                }

                mBytes = bytes;
            }

            public File build() {
                File file = bytesToFile(mBytes, mFilePath, mBytes.hashCode() + KTmpSuffix);
                return file;
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
                    NetworkLog.e(e);
                } finally {
                    if (bos != null) {
                        try {
                            bos.close();
                        } catch (IOException e) {
                            NetworkLog.e(e);
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            NetworkLog.e(e);
                        }
                    }
                }

                return file;
            }
        }
    }


}
