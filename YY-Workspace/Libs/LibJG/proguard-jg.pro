-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-keep public class cn.jiguang.analytics.android.api.** {
        *;
    }