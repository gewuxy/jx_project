package jx.csp.adapter.contribution;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import jx.csp.R;
import jx.csp.adapter.VH.contribution.SearchUnitNumVH;
import jx.csp.model.contribution.UnitNum;
import jx.csp.model.contribution.UnitNum.TUnitNum;
import lib.ys.YSLog;
import lib.ys.adapter.AdapterEx;
import lib.ys.util.res.ResLoader;

/**
 * @author CaiXiang
 * @since 2018/3/9
 */

public class SearchUnitNumAdapter extends AdapterEx<UnitNum, SearchUnitNumVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_search_unit_num_item;
    }

    @Override
    protected void refreshView(int position, SearchUnitNumVH holder) {
        UnitNum item = getItem(position);
        holder.getIv()
                .placeHolder(R.drawable.ic_default_unit_num)
                .load();
        String name = item.getString(TUnitNum.name);
        String search = item.getString(TUnitNum.search);
        SpannableStringBuilder spannable = new SpannableStringBuilder(name);
        if (name.contains(search)) {
            int beginIndex = name.indexOf(search);
            YSLog.d(TAG, "beginIndex = " + beginIndex);
            spannable.setSpan(new ForegroundColorSpan(ResLoader.getColor(R.color.text_81c101)), beginIndex, beginIndex + search.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        holder.getTvName().setText(spannable);
    }
}
