package lib.ys.network.result;

import java.util.List;

/**
 * @author yuansui
 */
public interface IListResult<T> extends IResult {

    String getLastItemId();

    void setLastItemId(String id);

    @Override
    List<T> getData();

    void setData(List<T> data);

    void add(T item);
}
