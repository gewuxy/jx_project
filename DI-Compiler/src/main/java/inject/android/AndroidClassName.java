package inject.android;

import com.squareup.javapoet.ClassName;

public interface AndroidClassName {
    ClassName KActivity = ClassName.get("android.app", "Activity");
    ClassName KFragment = ClassName.get("android.support.v4.app", "Fragment");
    ClassName KView = ClassName.get("android.view", "View");
    ClassName KContext = ClassName.get("android.content", "Context");
    ClassName KIntent = ClassName.get("android.content", "Intent");
    ClassName KBundle = ClassName.get("android.os", "Bundle");
}