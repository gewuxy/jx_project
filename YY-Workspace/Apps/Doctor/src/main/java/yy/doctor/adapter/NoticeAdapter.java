package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.NoticeVH;

/**
 * @author CaiXiang
 * @since 2017/5/3
 */
public class NoticeAdapter extends AdapterEx<String, NoticeVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_notice_item;
    }

    @Override
    protected void refreshView(int position, NoticeVH holder) {

    }

}
