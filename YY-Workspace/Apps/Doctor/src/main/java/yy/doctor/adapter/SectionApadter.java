package yy.doctor.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 日期 : 2017/4/24
 * 创建人 : guoxuan
 */

public class SectionApadter extends RecyclerView.Adapter {

    private static final String[] KSectionName = new String[]{
            "内科", "外科", "妇科", "儿科", "口腔科", "耳鼻咽喉科",
            "药剂科", "精神科", "康复科", "急诊科", "中药科", "麻醉科",
            "胃肠科", "医疗美容科", "眼科", "中医科", "内分泌科", "全科",
            "骨科", "肿瘤科", "产科", "消化科", "传染科", "其他"};

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setText("12345");
        ViewHolder viewHolder = new ViewHolder(textView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return KSectionName.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
