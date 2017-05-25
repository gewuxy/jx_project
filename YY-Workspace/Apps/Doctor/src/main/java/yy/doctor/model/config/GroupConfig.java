package yy.doctor.model.config;

import java.util.List;

/**
 * @author CaiXiang
 * @since 2017/5/24
 */

public class GroupConfig  {

    private Config mGroup;
    private List<Config> mChildren;

    public GroupConfig(Config group, List<Config> children) {
        this.mGroup = group;
        this.mChildren = children;
    }

}
