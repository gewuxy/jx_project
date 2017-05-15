package lib.network.model;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import lib.network.NetworkUtil;
import lib.network.param.BytePair;
import lib.network.param.CommonPair;
import lib.network.param.FilePair;

/**
 * Network任务实例
 *
 * @author yuansui
 */
public class NetworkReq {

    private List<CommonPair> mParams;
    private List<BytePair> mByteParams;
    private List<FilePair> mFileParams;
    
    private List<CommonPair> mHeaders;

    // 默认为get方式
    @NetworkMethod
    private int mMethod = NetworkMethod.get;

    private String mUrl;

    private String mDestDir;
    private String mDestFileName;

    private NetworkRetry mRetry;


    public NetworkReq(@NetworkMethod int method, String url) {
        mMethod = method;
        mUrl = url;

        mParams = new ArrayList<>();
    }

    public NetworkReq retry(NetworkRetry retry) {
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

    protected void param(String n, Object v) {
        mParams.add(new CommonPair(n, v));
    }

    protected void param(List<CommonPair> ps) {
        if (ps == null) {
            return;
        }
        mParams.addAll(ps);
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

    protected void paramByte(List<BytePair> pairs) {
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
        mByteParams.add(new BytePair(name + extend, value));
    }

    public List<BytePair> getByteParams() {
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
        mFileParams.add(new FilePair(name, uri));
    }

    protected void paramFile(List<FilePair> params) {
        if (params == null) {
            return;
        }

        if (mFileParams == null) {
            mFileParams = new ArrayList<>();
        }
        mFileParams.addAll(params);
    }

    public List<FilePair> getFileParams() {
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
        mHeaders.add(new CommonPair(name, value));
    }

    protected void header(String name, int value) {
        header(name, String.valueOf(value));
    }

    protected void header(List<CommonPair> headers) {
        if (headers == null) {
            return;
        }

        if (mHeaders == null) {
            mHeaders = new ArrayList<>();
        }
        mHeaders.addAll(headers);
    }

    public List<CommonPair> getHeaders() {
        return mHeaders;
    }

    public List<CommonPair> getParams() {
        return mParams;
    }

    @NetworkMethod
    public int method() {
        return mMethod;
    }

    public String getUrl() {
        return mUrl;
    }

    /**
     * 内部builder
     */
    public static class Builder {
        protected List<CommonPair> mParams;
        protected List<BytePair> mByteParams;
        protected List<FilePair> mFileParams;

        protected List<CommonPair> mHeaders;

        private String mBaseUrl;

        private String mDir;
        private String mFileName;

        @NetworkMethod
        private int mNetworkMethod = NetworkMethod.get;

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
            mParams.add(new CommonPair(name, String.valueOf(value)));
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

        public <T extends Builder> T param(List<CommonPair> pairs) {
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
            mByteParams.add(new BytePair(name + extend, value));
            return (T) this;
        }

        /**
         * 添加文件param
         *
         * @param name
         * @param path
         */
        public <T extends Builder> T paramFile(String name, String path) {
            if (mFileParams == null) {
                mFileParams = new ArrayList<>();
            }
            mFileParams.add(new FilePair(name, path));
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
            mHeaders.add(new CommonPair(name, value));
            return (T) this;
        }

        public <T extends Builder> T header(String name, int value) {
            header(name, String.valueOf(value));
            return (T) this;
        }

        public <T extends Builder> T header(List<CommonPair> pairs) {
            if (mHeaders == null) {
                mHeaders = new ArrayList<>();
            }
            mHeaders.addAll(pairs);
            return (T) this;
        }

        public NetworkReq build() {
            NetworkReq r = new NetworkReq(mNetworkMethod, mBaseUrl);

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
