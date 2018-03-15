package jx.csp.ui.activity.contribution;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.adapter.contribution.SearchUnitNumAdapter;
import jx.csp.model.contribution.HotUnitNum;
import jx.csp.model.main.Meet;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import jx.csp.util.Util;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseSRListActivity;
import lib.ys.ui.other.NavBar;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;

/**
 * 搜索投稿单位号
 *
 * @author CaiXiang
 * @since 2018/3/9
 */
@Route
public class SearchUnitNumActivity extends BaseSRListActivity<HotUnitNum, SearchUnitNumAdapter> {

    @Arg
    Meet mMeet;

    private EditText mEtSearch;
    private ImageView mIvClear;
    private TextView mTvSearch;
    private TextView mTvFooter;

    private boolean mSearch = false;

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View view = inflate(R.layout.layout_search_unit_num_nav_bar);
        mEtSearch = view.findViewById(R.id.search_unit_num_et);
        mIvClear = view.findViewById(R.id.search_unit_num_iv_clear);
        mTvSearch = view.findViewById(R.id.search_unit_num_tv_search);
        bar.addViewLeft(view, null);
    }

    @Nullable
    @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_search_unit_num_header);
    }

    @Nullable
    @Override
    public View createFooterView() {
        return inflate(R.layout.layout_footer_no_more);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvFooter = findView(R.id.footer_no_more_tv);
    }

    @Override
    public void setViews() {
        super.setViews();

        Util.setTextViewBackground(mTvSearch);
        setRefreshEnabled(false);
        mTvFooter.setText(R.string.no_search_result);
        getAdapter().setMeetData(mMeet);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    showView(mIvClear);
                } else {
                    goneView(mIvClear);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setOnClickListener(mIvClear);
        setOnClickListener(mTvSearch);
    }

    @Override
    public boolean enableInitRefresh() {
        return false;
    }

    @Override
    public void getDataFromNet() {
        String key = mEtSearch.getText().toString();
        if (mSearch) {
            exeNetworkReq(DeliveryAPI.searchUnitNum(key, 0, getLimit()).build());
        } else {
            exeNetworkReq(DeliveryAPI.searchUnitNum(key, getOffset(), getLimit()).build());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_unit_num_iv_clear: {
                mEtSearch.setText("");
            }
            break;
            case R.id.search_unit_num_tv_search: {
                String key = mEtSearch.getText().toString();
                if (TextUtil.isNotEmpty(key)) {
                    mSearch = true;
                    getAdapter().setKeyWord(key);
                    if (KeyboardUtil.isActive()) {
                        // 键盘显示就隐藏
                        KeyboardUtil.hideFromView(mEtSearch);
                    }
                    getDataFromNet();
                }
            }
            break;
        }
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.no_search_result);
    }

    @Override
    protected int getEmptyImg() {
        return R.drawable.main_ic_empty;
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.finish_contribute) {
            finish();
        }
    }
}
