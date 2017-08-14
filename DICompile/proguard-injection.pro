# 混淆要 -keep @inject 规则要精准到注解所在的全路径, 不能只写上级路径, 所以需要罗列

-keep class inject.annotation.** { *; }
-dontwarn inject.**

-keep @inject.annotation.router.* class * {
    @inject.annotation.router.* <fields>;
}

-keep @inject.annotation.builder.* class * {
    @inject.annotation.builder.* <fields>;
}

-keep class **Router { *; }
-keep class **Builder { *; }