package lib.ys.impl;

import lib.ys.adapter.interfaces.IPagerTitle;
import lib.ys.impl.PagerTitleImpl.TPagerTitle;
import lib.ys.model.EVal;

public class PagerTitleImpl extends EVal<TPagerTitle> implements IPagerTitle {

    public enum TPagerTitle {
        id,
        name,
    }

    @Override
    public String getTitle() {
        return getString(TPagerTitle.name);
    }
}