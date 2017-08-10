-keep class lib.processor.annotation.** { *; }
-dontwarn lib.processor.**

-keep @lib.processor.annotation.* class * {
    @lib.processor.annotation.* <fields>;
}

-keep class **Intent { *; }
-keep class **Arg { *; }