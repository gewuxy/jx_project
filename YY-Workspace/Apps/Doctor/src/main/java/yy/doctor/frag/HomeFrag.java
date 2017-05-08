package yy.doctor.frag;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseFrag;
import yy.doctor.R;
import yy.doctor.activity.NoticeActivity;

/**
 * @author CaiXiang   extends BaseSRListFrag<Home>
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
    public void initNavBar(NavBar bar) {
        View v = inflate(R.layout.layout_home_nav_bar);
        bar.addViewRight(v, null);

        bar.addViewRight(R.mipmap.nav_bar_ic_notice, new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(NoticeActivity.class);

            }
        });
    }

    @Override
    public void findViews() {


    }

    @Override
    public void setViews() {

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
