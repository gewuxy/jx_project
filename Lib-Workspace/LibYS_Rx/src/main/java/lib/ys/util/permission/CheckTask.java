package lib.ys.util.permission;

/**
 * @author yuansui
 */
public class CheckTask {

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

    public OnPermissionListener getListener() {
        return mListener;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        @Permission
        private String[] mPermissions;
        private OnPermissionListener mListener;
        private int mCode;

        private Builder() {
        }

        public Builder listener(OnPermissionListener l) {
            mListener = l;
            return this;
        }

        public Builder code(int code) {
            mCode = code;
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

            return task;
        }
    }
}
