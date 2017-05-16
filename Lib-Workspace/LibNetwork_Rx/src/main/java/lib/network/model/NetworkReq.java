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

    private String mDir;
    private String mFileName;

    private Class mConvertTo;
    private Class mConvertToList;

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
    public int method() {
        return mMethod;
    }

    public String getUrl() {
        return mUrl;
    }

    public Class getConvertTo() {
        return mConvertTo;
    }

    public Class getConvertToList() {
        return mConvertToList;
    }

    public static Builder newBuilder(String baseUrl) {
        return new Builder(baseUrl);
    }

    /**
     * 内部builder
     */
    public static class Builder {
        private List<CommonPair> mParams;
        private List<BytePair> mByteParams;
        private List<FilePair> mFileParams;

        private List<CommonPair> mHeaders;

        private String mUrl;

        private String mDir;
        private String mFileName;

        private Class mConvertTo;
        private Class mConvertToList;

        @NetworkMethod
        private int mMethod = NetworkMethod.get;

        private NetworkRetry mRetry;


        private Builder(String baseUrl) {
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

        public <T extends Builder> T convertTo(Class c) {
            mConvertTo = c;
            return (T) this;
        }

        public <T extends Builder> T convertToList(Class c) {
            mConvertToList = c;
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
            mMethod = NetworkMethod.download_file;
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

        public <T extends Builder> T retry(int count, long delay) {
            mRetry = new NetworkRetry(count, delay);
            return (T) this;
        }

        public NetworkReq build() {
            NetworkReq r = new NetworkReq();

            r.mMethod = mMethod;
            r.mUrl = mUrl;

            r.mRetry = mRetry;

            r.mConvertTo = mConvertTo;
            r.mConvertToList = mConvertToList;

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
