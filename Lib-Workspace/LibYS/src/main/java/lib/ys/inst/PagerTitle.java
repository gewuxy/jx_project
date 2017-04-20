package lib.ys.inst;

import lib.ys.adapter.interfaces.IPagerTitle;
import lib.ys.inst.PagerTitle.TPagerTitle;
import lib.ys.model.EVal;

public class PagerTitle extends EVal<TPagerTitle> implements IPagerTitle {

    public enum TPagerTitle {
        id,
        name,
    }

    @Override
    public String getTitle() {
        return getString(TPagerTitle.name);
    }
}