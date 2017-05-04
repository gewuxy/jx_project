package lib.network.model;


import android.content.Context;
import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.network.NetworkUtil;
import lib.network.param.NameByteValuePair;
import lib.network.param.NameFileValuePair;
import lib.network.param.NameValuePair;
import okhttp3.OkHttpClient;

/**
 * Network任务实例
 *
 * @author yuansui
 */
public class NetworkRequest {

    private List<NameValuePair> mParams;
    private List<NameByteValuePair> mByteParams;
    private List<NameValuePair> mHeaders;
    private List<NameFileValuePair> mFileParams;

    @NetworkMethod
    private int mMethod = NetworkMethod.un_know;

    private String mUrl;

    private String mDestDir;
    private String mDestFileName;

    private NetworkRetry mRetry;


    public NetworkRequest(@NetworkMethod int method, String url) {
        mMethod = method;
        mUrl = url;

        mParams = new ArrayList<>();
    }

    public NetworkRequest retry(NetworkRetry retry) {
        mRetry = retry;
        return this;
    }

    public NetworkRetry getRetry() {
        return mRetry;
    }

    public void dir(String dir) {
        mDestDir = dir;
    }

    public String getDir() {
        return mDestDir;
    }

    public void fileName(String name) {
        mDestFileName = name;
    }

    public String getFileName() {
        return mDestFileName;
    }

    protected void param(String name, Object value) {
        mParams.add(new NameValuePair(name, String.valueOf(value)));
    }

    protected void param(List<NameValuePair> pairs) {
        if (pairs == null) {
            return;
        }
        mParams.addAll(pairs);
    }

    /**
     * 添加二进制param
     *
     * @param name
     * @param value
     */
    protected void param(String name, byte[] value) {
        param(name, value, NetworkUtil.KTextEmpty);
    }

    protected void paramByte(List<NameByteValuePair> pairs) {
        if (pairs == null) {
            return;
        }

        if (mByteParams == null) {
            mByteParams = new ArrayList<>();
        }
        mByteParams.addAll(pairs);
    }

    /**
     * 添加带有拓展名的二进制param
     * 部分接口需要传文件后缀名才能正确解析
     *
     * @param name
     * @param value
     * @param extend 拓展名, 如".jpg", ".png"等
     */
    protected void param(String name, byte[] value, String extend) {
        if (mByteParams == null) {
            mByteParams = new ArrayList<>();
        }
        mByteParams.add(new NameByteValuePair(name + extend, value));
    }

    public List<NameByteValuePair> getByteParams() {
        return mByteParams;
    }

    /**
     * 添加文件param
     *
     * @param name
     * @param uri
     */
    protected void paramFile(String name, String uri) {
        if (mFileParams == null) {
            mFileParams = new ArrayList<>();
        }
        mFileParams.add(new NameFileValuePair(name, uri));
    }

    protected void paramFile(List<NameFileValuePair> params) {
        if (params == null) {
            return;
        }

        if (mFileParams == null) {
            mFileParams = new ArrayList<>();
        }
        mFileParams.addAll(params);
    }

    public List<NameFileValuePair> getFileParams() {
        return mFileParams;
    }

    /**
     * 添加header数据
     *
     * @param name
     * @param value
     */
    protected void header(String name, String value) {
        if (mHeaders == null) {
            mHeaders = new ArrayList<>();
        }
        mHeaders.add(new NameValuePair(name, value));
    }

    protected void header(String name, int value) {
        header(name, String.valueOf(value));
    }

    protected void header(List<NameValuePair> headers) {
        if (headers == null) {
            return;
        }

        if (mHeaders == null) {
            mHeaders = new ArrayList<>();
        }
        mHeaders.addAll(headers);
    }

    public List<NameValuePair> getHeaders() {
        return mHeaders;
    }

    public List<NameValuePair> getParams() {
        return mParams;
    }

    @NetworkMethod
    public int method() {
        return mMethod;
    }

    public String getUrl() {
        return mUrl;
    }

    public static void init(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 内部builder
     */
    public static class Builder {
        protected List<NameValuePair> mParams;
        protected List<NameByteValuePair> mByteParams;
        protected List<NameValuePair> mHeaders;
        protected List<NameFileValuePair> mFileParams;

        private String mBaseUrl;

        private String mDir;
        private String mFileName;

        @NetworkMethod
        private int mNetworkMethod = NetworkMethod.un_know;

        public Builder(String baseUrl) {
            mBaseUrl = baseUrl;

            mParams = new ArrayList<>();
        }

        public <T extends Builder> T get() {
            mNetworkMethod = NetworkMethod.get;
            return (T) this;
        }

        public <T extends Builder> T post() {
            mNetworkMethod = NetworkMethod.post;
            return (T) this;
        }

        public <T extends Builder> T upload() {
            mNetworkMethod = NetworkMethod.upload;
            return (T) this;
        }

        /**
         * 下载任务需要其他参数
         *
         * @param dir
         * @param fileName
         * @return
         */
        public <T extends Builder> T downloadFile(String dir, String fileName) {
            mNetworkMethod = NetworkMethod.download_file;
            mDir = dir;
            mFileName = fileName;
            return (T) this;
        }

        public <T extends Builder> T param(String name, String value) {
            if (TextUtils.isEmpty(value)) {
                return (T) this;
            }
            mParams.add(new NameValuePair(name, String.valueOf(value)));
            return (T) this;
        }

        public <T extends Builder> T param(String name, int value) {
            param(name, String.valueOf(value));
            return (T) this;
        }

        public <T extends Builder> T param(String name, long value) {
            param(name, String.valueOf(value));
            return (T) this;
        }

        public <T extends Builder> T param(List<NameValuePair> pairs) {
            mParams.addAll(pairs);
            return (T) this;
        }

        /**
         * 添加二进制param
         *
         * @param name
         * @param value
         */
        public <T extends Builder> T param(String name, byte[] value) {
            return param(name, value, NetworkUtil.KTextEmpty);
        }

        /**
         * 添加二进制param
         *
         * @param name
         * @param value
         * @param extend
         * @return
         */
        public <T extends Builder> T param(String name, byte[] value, String extend) {
            if (mByteParams == null) {
                mByteParams = new ArrayList<>();
            }
            mByteParams.add(new NameByteValuePair(name + extend, value));
            return (T) this;
        }

        /**
         * 添加文件param
         *
         * @param name
         * @param uri
         */
        public <T extends Builder> T paramFile(String name, String uri) {
            if (mFileParams == null) {
                mFileParams = new ArrayList<>();
            }
            mFileParams.add(new NameFileValuePair(name, uri));
            return (T) this;
        }

        /**
         * 添加header数据
         *
         * @param name
         * @param value
         */
        public <T extends Builder> T header(String name, String value) {
            if (mHeaders == null) {
                mHeaders = new ArrayList<>();
            }
            mHeaders.add(new NameValuePair(name, value));
            return (T) this;
        }

        public <T extends Builder> T header(String name, int value) {
            header(name, String.valueOf(value));
            return (T) this;
        }

        public <T extends Builder> T header(List<NameValuePair> pairs) {
            if (mHeaders == null) {
                mHeaders = new ArrayList<>();
            }
            mHeaders.addAll(pairs);
            return (T) this;
        }

        public NetworkRequest build() {
            NetworkRequest r = new NetworkRequest(mNetworkMethod, mBaseUrl);

            if (mNetworkMethod == NetworkMethod.download_file) {
                r.dir(mDir);
                r.fileName(mFileName);
            }

            r.header(mHeaders);
            r.param(mParams);
            r.paramByte(mByteParams);
            r.paramFile(mFileParams);

            return r;
        }

    }
}
