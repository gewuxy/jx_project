package jx.csp.ui.activity.contribution;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import jx.csp.R;
import jx.csp.adapter.contribution.ContributeHistoryHotUnitNumAdapter;
import jx.csp.model.contribution.ContributeHistories;
import jx.csp.model.contribution.ContributeHistory;
import jx.csp.model.contribution.HotUnitNum;
import jx.csp.model.contribution.IContributeHistoryHotUnitNum;
import jx.csp.model.contribution.LargeDivider;
import jx.csp.model.contribution.ListTitle;
import jx.csp.model.contribution.ListTitle.TListTitle;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseListActivity;
import lib.ys.ui.other.NavBar;

/**
 * 投稿历史、热门单位号
 *
 * @author CaiXiang
 * @since 2018/3/9
 */

public class ContributeHistoryHotUnitNumActivity extends BaseListActivity<IContributeHistoryHotUnitNum, ContributeHistoryHotUnitNumAdapter> {

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View view = inflate(R.layout.layout_contribute_history_hot_unit_num_nav_bar);
        bar.addViewLeft(view, v -> showToast("nav bar"));
    }

    @Override
    public void setViews() {
        super.setViews();

        setDividerHeight(0);
        List<IContributeHistoryHotUnitNum> data = new ArrayList<>();

        ListTitle listTitle1 = new ListTitle();
        listTitle1.put(TListTitle.title, "YaYa医师单位号");
        data.add(listTitle1);

//        ContributeHistoryEmpty empty = new ContributeHistoryEmpty();
//        data.add(empty);

        ContributeHistories contributeHistories = new ContributeHistories();
        List<ContributeHistory> historyList = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            ContributeHistory history = new ContributeHistory();
            historyList.add(history);
        }
        contributeHistories.setData(historyList);
        data.add(contributeHistories);

        LargeDivider divider = new LargeDivider();
        data.add(divider);

        ListTitle listTitle2 = new ListTitle();
        listTitle2.put(TListTitle.title, "热门单位号");
        data.add(listTitle2);

        for (int i = 0; i < 8; ++i) {
            HotUnitNum hotUnitNum = new HotUnitNum();
            data.add(hotUnitNum);
        }

        addAll(data);
    }

}
