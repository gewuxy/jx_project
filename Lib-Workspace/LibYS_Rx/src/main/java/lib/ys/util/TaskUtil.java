package lib.ys.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

import lib.ys.AppEx;

public class TaskUtil {

    /**
     * 判断程序是否在前台运行
     *
     * @return
     */
    public static boolean isRunningInForeground(String packageName) {
        List<RunningTaskInfo> tasks = getRunningTaskInfos(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            return topActivity.getPackageName().equals(packageName);
        }
        return false;
    }

    /**
     * 程序是否运行
     *
     * @param packageName
     * @return
     */
    public static boolean isRunning(String packageName) {
        List<RunningTaskInfo> tasks = getRunningTaskInfos(100);
        for (int i = 0; i < tasks.size(); i++) {
            String name = tasks.get(i).topActivity.getPackageName();
            if (name.equals(packageName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取运行中的任务列表
     *
     * @param maxNum
     * @return
     */
    private static List<RunningTaskInfo> getRunningTaskInfos(int maxNum) {
        ActivityManager am = (ActivityManager) AppEx.ct().getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningTasks(maxNum);
    }

}
