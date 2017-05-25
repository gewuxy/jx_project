package yy.doctor.model.config;

import lib.network.model.param.BasePair;

/**
 * @author CaiXiang
 * @since 2017/5/24
 */

public class Config extends BasePair<String> {

    public Config(String name, String val) {
        super(name, val);
    }

    public Config(String name, int val) {
        this(name, String.valueOf(val));
    }
}
