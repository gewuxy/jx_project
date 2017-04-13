package yy.doctor.frag;

import android.support.annotation.NonNull;

import lib.yy.frag.base.BaseFrag;
import yy.doctor.R;
import yy.doctor.activity.MainActivity;
import yy.doctor.util.Util;

/**
 * @author Administrator   extends BaseSRListFrag<Home>
 * @since 2017/4/5
 */
public class HomeFrag extends BaseFrag {
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
        Util.addMenuIcon(getTitleBar(), (MainActivity) getActivity());
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
