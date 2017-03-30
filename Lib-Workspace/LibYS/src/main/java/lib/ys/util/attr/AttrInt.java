package lib.ys.util.attr;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
        AttrInt.src,
        AttrInt.button,
})
@Retention(RetentionPolicy.SOURCE)
public @interface AttrInt {
    String src = "src";
    String button = "button";
}