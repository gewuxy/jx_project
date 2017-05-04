package lib.ys.timer;

import java8.lang.FunctionalInterface;

@FunctionalInterface
public interface TimerListener {
    void onTimerTick();
}
