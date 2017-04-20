package lib.ys.crash;

public interface OnCrashListener {
    boolean handleCrashException(Throwable ex);
}
