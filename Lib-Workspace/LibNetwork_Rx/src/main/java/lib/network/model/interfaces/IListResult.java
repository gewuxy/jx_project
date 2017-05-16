package lib.network.model.interfaces;

import java.util.List;

/**
 * @author yuansui
 */
public interface IListResult<T> extends IResult {

    String getLastId();

    void setLastId(String id);

    @Override
    List<T> getData();

    void setData(List<T> data);

    void add(T item);
}
