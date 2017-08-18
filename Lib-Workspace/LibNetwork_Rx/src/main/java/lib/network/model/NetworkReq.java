package lib.network.model;


import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lib.network.NetworkUtil;
import lib.network.model.param.BytePair;
import lib.network.model.param.CommonPair;
import lib.network.model.param.FilePair;

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

    private String mDir;
    private String mFileName;

    private NetworkRetry mRetry;

    private NetworkReq() {
    }

    public NetworkRetry getRetry() {
        return mRetry;
    }

    public String getDir() {
        return mDir;
    }

    public String getFileName() {
        return mFileName;
    }

    public List<BytePair> getByteParams() {
        return mByteParams;
    }

    public List<FilePair> getFileParams() {
        return mFileParams;
    }

    public List<CommonPair> getHeaders() {
        return mHeaders;
    }

    public List<CommonPair> getParams() {
        return mParams;
    }

    @NetworkMethod
    public int getMethod() {
        return mMethod;
    }

    public String getUrl() {
        return mUrl;
    }

    public static Builder newBuilder(String baseUrl) {
        return new Builder(baseUrl);
    }

    /**
     * 内部builder
     */
    public static class Builder {
        protected List<CommonPair> mParams;
        private List<BytePair> mByteParams;
        private List<FilePair> mFileParams;

        private List<CommonPair> mHeaders;

        private String mUrl;

        private String mDir;
        private String mFileName;

        @NetworkMethod
        private int mMethod = NetworkMethod.get;

        private NetworkRetry mRetry;


        protected Builder(String baseUrl) {
            mUrl = baseUrl;

            mParams = new ArrayList<>();
        }

        public <T extends Builder> T get() {
            mMethod = NetworkMethod.get;
            return (T) this;
        }

        public <T extends Builder> T post() {
            mMethod = NetworkMethod.post;
            return (T) this;
        }

        public <T extends Builder> T upload() {
            mMethod = NetworkMethod.upload;
            return (T) this;
        }

        /**
         * 下载任务需要其他参数
         *
         * @param dir
         * @param fileName
         * @return
         */
        public <T extends Builder> T download(String dir, String fileName) {
            mMethod = NetworkMethod.download_file;
            mDir = dir;
            mFileName = fileName;
            return (T) this;
        }

        /**
         * 下载到内存中
         *
         * @param <T>
         * @return
         */
        public <T extends Builder> T download() {
            mMethod = NetworkMethod.download;
            return (T) this;
        }

        public <T extends Builder> T param(String name, String val) {
            if (val == null) {
                return (T) this;
            }
            mParams.add(new CommonPair(name, String.valueOf(val)));
            return (T) this;
        }

        public <T extends Builder> T param(String name, boolean val) {
            mParams.add(new CommonPair(name, String.valueOf(val)));
            return (T) this;
        }

        public <T extends Builder> T param(String name, int val) {
            param(name, String.valueOf(val));
            return (T) this;
        }

        public <T extends Builder> T param(String name, long val) {
            param(name, String.valueOf(val));
            return (T) this;
        }

        public <T extends Builder> T param(@Nullable List<CommonPair> pairs) {
            if (pairs == null) {
                return (T) this;
            }
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
            if (value == null) {
                return (T) this;
            }
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
         * @param pair
         */
        public <T extends Builder> T param(FilePair pair) {
            if (mFileParams == null) {
                mFileParams = new ArrayList<>();
            }
            mFileParams.add(pair);
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

        public <T extends Builder> T header(@Nullable List<CommonPair> pairs) {
            if (pairs == null) {
                return (T) this;
            }

            if (mHeaders == null) {
                mHeaders = new ArrayList<>();
            }
            mHeaders.addAll(pairs);
            return (T) this;
        }

        public <T extends Builder> T retry(int count, long delay) {
            mRetry = new NetworkRetry(count, delay);
            return (T) this;
        }

        public NetworkReq build() {
            NetworkReq r = new NetworkReq();

            r.mMethod = mMethod;
            r.mUrl = mUrl;

            r.mRetry = mRetry;

            r.mDir = mDir;
            r.mFileName = mFileName;

            r.mHeaders = mHeaders;
            r.mParams = mParams;
            r.mByteParams = mByteParams;
            r.mFileParams = mFileParams;

            return r;
        }

    }
}
