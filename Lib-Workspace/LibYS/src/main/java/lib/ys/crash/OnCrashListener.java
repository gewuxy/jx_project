package lib.ys.crash;

@FunctionalInterface
public interface OnCrashListener {
    boolean handleCrashException(Throwable ex);
}
