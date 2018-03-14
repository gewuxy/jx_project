package jx.csp.adapter.contribution;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import jx.csp.R;
import jx.csp.adapter.VH.contribution.SearchUnitNumVH;
import jx.csp.model.contribution.HotUnitNum;
import jx.csp.model.contribution.HotUnitNum.THotUnitNum;
import jx.csp.model.contribution.UnitNum;
import jx.csp.model.contribution.UnitNum.TUnitNum;
import jx.csp.model.main.Meet;
import jx.csp.ui.activity.contribution.ContributeActivityRouter;
import lib.ys.YSLog;
import lib.ys.adapter.AdapterEx;
import lib.ys.util.res.ResLoader;

/**
 * @author CaiXiang
 * @since 2018/3/9
 */

public class SearchUnitNumAdapter extends AdapterEx<HotUnitNum, SearchUnitNumVH> {

    private Meet mMeet;
    private String mKeyWord;  // 搜索关键字

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_search_unit_num_item;
    }

    @Override
    protected void refreshView(int position, SearchUnitNumVH holder) {
        HotUnitNum item = getItem(position);
        holder.getIv()
                .placeHolder(R.drawable.ic_default_unit_num)
                .load();
        String name = item.getString(THotUnitNum.acceptName);
        SpannableStringBuilder spannable = new SpannableStringBuilder(name);
        if (name.contains(mKeyWord)) {
            int beginIndex = name.indexOf(mKeyWord);
            YSLog.d(TAG, "beginIndex = " + beginIndex);
            spannable.setSpan(new ForegroundColorSpan(ResLoader.getColor(R.color.text_81c101)), beginIndex, beginIndex + mKeyWord.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        holder.getTvName().setText(spannable);
        holder.getTvContributionNum().setText(String.format(ResLoader.getString(R.string.already_contribution), item.getInt(THotUnitNum.acceptCount)));
        setOnViewClickListener(position, holder.getItemLayout());
    }

    @Override
    protected void onViewClick(int position, View v) {
        HotUnitNum item = getItem(position);
        UnitNum unitNum = new UnitNum();
        unitNum.put(TUnitNum.id, 1); // 平台id  目前只有YaYa医师单位号 是 1
        unitNum.put(TUnitNum.unitNumId, item.getInt(THotUnitNum.acceptId));
        unitNum.put(TUnitNum.platformName, item.getString(THotUnitNum.acceptName));
        unitNum.put(TUnitNum.imgUrl, item.getString(THotUnitNum.headimg));
        ContributeActivityRouter.create(mMeet, unitNum).route(getContext());
    }

    public void setMeetData(Meet meet) {
        mMeet = meet;
    }

    public void setKeyWord(String keyWord) {
        mKeyWord = keyWord;
    }
}
