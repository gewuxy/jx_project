package jx.csp.adapter.main;

import android.widget.ImageView;

import jx.csp.R;
import jx.csp.adapter.VH.main.MeetVH;
import jx.csp.model.main.Meet;

/**
 * @auther : GuoXuan
 * @since : 2018/2/1
 */
public class MeetCardAdapter extends MeetAdapter {

    private boolean mNeedShow = true;

    public void setNeedShow() {
        mNeedShow = true;
    }

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_main_meet_card_item;
    }

    @Override
    protected void meetItemRefresh(int position, MeetVH holder) {
        if (mNeedShow) {
            showView(holder.getDividerTop());
            mNeedShow = false;
        } else {
            goneView(holder.getDividerTop());
        }

        holder.getIvHead()
                .placeHolder(R.drawable.ic_default_main_grid)
                .url(getItem(position).getString(Meet.TMeet.coverUrl))
                .resize(fit(340), fit(180))
                .load();
    }

}
