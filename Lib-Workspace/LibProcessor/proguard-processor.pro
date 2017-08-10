-keep class lib.processor.annotation.** { *; }
-dontwarn lib.processor.**

#-keepclasseswithmembernames class * {
#    @lib.processor.annotation.* <fields>;
#}

#-keep public class * extends android.support.v4.app.Fragment {
#    @lib.processor.annotation.* <fields>;
#}

-keep @lib.processor.annotation.* class * {*;}

#-keep public class * extends android.support.v4.app.Fragment {
#    @lib.processor.annotation.* <fields>;
#}

-keep class **Intent { *; }
-keep class **Arg { *; }