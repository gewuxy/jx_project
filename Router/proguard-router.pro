-keep class compiler.annotation.** { *; }
-dontwarn compiler.**

-keep @compiler.annotation.* class * {
    @compiler.annotation.* <fields>;
}

-keep class **Intent { *; }
-keep class **Arg { *; }