package yy.doctor.model;

import lib.ys.model.EVal;
import lib.ys.model.inject.BindList;
import lib.ys.model.inject.BindObj;
import yy.doctor.model.Test.ETest;

/**
 * @author Administrator
 * @since 2017/4/1
 */
public class Test extends EVal<ETest> {

    public enum ETest {
        name,
        psd,
        user_name,
        count,

        @BindList(Profile.class)
        list,

        @BindObj(Profile.class)
        test,
    }
}
