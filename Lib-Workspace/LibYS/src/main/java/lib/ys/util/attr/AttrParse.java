package lib.ys.util.attr;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
        AttrParse.suffix_dip,
        AttrParse.prefix_src,
})
@Retention(RetentionPolicy.SOURCE)
public @interface AttrParse {
    String suffix_dip = "suffix_dip";
    String prefix_src = "@";
}