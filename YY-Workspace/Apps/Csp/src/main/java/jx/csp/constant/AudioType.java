package jx.csp.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
        AudioType.mp3,
        AudioType.amr
})
@Retention(RetentionPolicy.SOURCE)
public @interface AudioType {
    String mp3 = "mp3";
    String amr = "amr";
}