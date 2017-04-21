package yy.doctor.frag;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import lib.yy.frag.base.BaseFrag;
import yy.doctor.R;

/**
 * @author Administrator   extends BaseSRListFrag<Home>
 * @since 2017/4/5
 */
public class HomeFrag extends BaseFrag {
    private EditText mEtSearch;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_home;
    }

    @Override
    public void initTitleBar() {
        View v = inflate(R.layout.layout_home_title_bar);
        getTitleBar().addViewMid(v);

    }

    @Override
    public void findViews() {


    }

    @Override
    public void setViewsValue() {

    }

    /*@Override
    public MultiRecyclerAdapterEx<Home, ? extends RecyclerViewHolderEx> createAdapter() {
        return new HomeAdapter();
    }

    @Override
    public void getDataFromNet() {
        exeNetworkRequest(0, NetFactory.home());
    }

    @Override
    public IListResponse<Home> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.home(text);
    }*/

}
