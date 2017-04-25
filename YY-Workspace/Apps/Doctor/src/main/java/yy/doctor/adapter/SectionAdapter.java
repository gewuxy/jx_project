package yy.doctor.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import yy.doctor.R;

/**
 * 选择科室的Adapter
 *
 * 日期 : 2017/4/24
 * 创建人 : guoxuan
 */

public class SectionAdapter extends RecyclerView.Adapter {

    public interface OnItemClickListener {
        void onItemClick(ViewHolder viewHolder);
    }

    private static final String[] KSectionName = new String[]{
            "内科", "外科", "妇科", "儿科", "口腔科", "耳鼻咽喉科",
            "药剂科", "精神科", "康复科", "急诊科", "中药科", "麻醉科",
            "胃肠科", "医疗美容科", "眼科", "中医科", "内分泌科", "全科",
            "骨科", "肿瘤科", "产科", "消化科", "传染科", "其他"};

    private OnItemClickListener mOnItemClickListener;

    public SectionAdapter(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(parent.getContext(), R.layout.layout_meeting_section, null);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setText(position);
        viewHolder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(viewHolder);
                }
            }
        });
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
        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.meeting_layout_tv);
        }

        public void setText(int position) {
            mTextView.setText(KSectionName[position]);
        }

        public String getText() {
            return mTextView.getText().toString().trim();
        }
    }
}
