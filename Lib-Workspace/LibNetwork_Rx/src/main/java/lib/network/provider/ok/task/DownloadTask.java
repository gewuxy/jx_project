package lib.network.provider.ok.task;

import java.io.IOException;

import lib.network.model.NetworkReq;
import lib.network.model.interfaces.OnNetworkListener;
import lib.network.provider.NativeListener;
import lib.network.provider.ok.OkClient;
import lib.network.provider.ok.callback.OkCallback;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * @auther yuansui
 * @since 2017/6/11
 */

public class DownloadTask extends GetTask {

    public DownloadTask(int id, NetworkReq req, OkCallback callback) {
        super(id, req, callback);
    }

    @Override
    public void run() {
        Request request = buildRealReq();

        Call c = OkClient.inst()
                .addInterceptor(chain -> {
                    //拦截
                    Response originalResponse = chain.proceed(chain.request());
                    //包装响应体并返回
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(getId(), originalResponse.body(), getCallback().getListener()))
                            .build();
                })
                .newCall(request);

        c.enqueue(getCallback());
    }

    public static class ProgressResponseBody extends ResponseBody {
        private final ResponseBody responseBody;
        private BufferedSource bufferedSource;

        private OnNetworkListener mListener;
        private int mId;

        /**
         * 构造函数，赋值
         *
         * @param responseBody 待包装的响应体
         * @param l            回调接口
         */
        public ProgressResponseBody(int id, ResponseBody responseBody, OnNetworkListener l) {
            this.responseBody = responseBody;
            mListener = l;
            mId = id;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        /**
         * 读取，回调进度接口
         *
         * @param source Source
         * @return Source
         */
        private Source source(Source source) {

            return new ForwardingSource(source) {
                //当前读取字节数
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    //回调，如果contentLength()不知道长度，会返回-1

                    float progress = (float) totalBytesRead / responseBody.contentLength() * 100;

                    if (mListener != null) {
                        NativeListener.inst().onProgress(mId, progress, responseBody.contentLength(), mListener);
                    }
                    return bytesRead;
                }
            };
        }
    }
}
