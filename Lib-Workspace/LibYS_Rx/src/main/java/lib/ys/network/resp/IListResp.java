package lib.ys.network.resp;

import java.util.List;

/**
 * @author yuansui
 */
public interface IListResp<T> extends IResp {

    String getLastItemId();

    void setLastItemId(String id);

    @Override
    List<T> getData();

    void setData(List<T> data);

    void add(T item);
}
