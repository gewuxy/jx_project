package lib.ys.crash;

import java8.lang.FunctionalInterface;

@FunctionalInterface
public interface OnCrashListener {
    boolean handleCrashException(Throwable ex);
}
