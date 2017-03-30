package lib.network.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        NetworkMethod.un_know,
        NetworkMethod.get,
        NetworkMethod.post,
        NetworkMethod.upload,
        NetworkMethod.download_file
})
@Retention(RetentionPolicy.SOURCE)
public @interface NetworkMethod {
    int un_know = 0;
    int get = 1;
    int post = 2;
    int upload = 3;
    int download_file = 4;
}