package yy.doctor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CaiXiang
 * @since 2017/6/14
 */

public class NoticeSize {

    private static List mHomeInst = null;

    public synchronized static List homeInst() {
        if (mHomeInst == null) {
            if (mHomeInst == null) {
                mHomeInst = new ArrayList();
            }
        }
        return mHomeInst;
    }

}
