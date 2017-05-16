package lib.ys.network.result;

import java.util.List;

/**
 * @author yuansui
 */
public interface IListResponse<T> extends IResponse {

    String getLastItemId();

    void setLastItemId(String id);

    @Override
    List<T> getData();

    void setData(List<T> data);

    void add(T item);
}
