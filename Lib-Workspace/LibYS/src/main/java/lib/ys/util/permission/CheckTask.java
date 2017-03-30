package lib.ys.util.permission;

/**
 * @author yuansui
 */
public class CheckTask {
    private Object mHost;

    @Permission
    private String[] mPermissions;

    private int mCode;

    private OnPermissionListener mListener;

    @Permission
    public String[] getPermissions() {
        return mPermissions;
    }

    public int getCode() {
        return mCode;
    }

    public Object getHost() {
        return mHost;
    }

    public OnPermissionListener getListener() {
        return mListener;
    }


    public static class Builder {

        @Permission
        private String[] mPermissions;
        private OnPermissionListener mListener;
        private int mCode;
        private Object mHost;

        public Builder listener(OnPermissionListener l) {
            mListener = l;
            return this;
        }

        public Builder code(int code) {
            mCode = code;
            return this;
        }

        public Builder host(Object host) {
            mHost = host;
            return this;
        }

        public Builder permissions(@Permission String... ps) {
            mPermissions = ps;
            return this;
        }

        public CheckTask build() {
            CheckTask task = new CheckTask();

            task.mListener = mListener;
            task.mCode = mCode;
            task.mPermissions = mPermissions;
            task.mHost = mHost;

            return task;
        }
    }
}
