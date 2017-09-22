package yy.doctor.view.meet;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lib.ys.fitter.LayoutFitter;
import lib.ys.util.TextUtil;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import yy.doctor.Extra.FileFrom;
import yy.doctor.R;
import yy.doctor.model.unitnum.File;
import yy.doctor.model.unitnum.File.TFile;
import yy.doctor.ui.activity.me.unitnum.FileActivityRouter;
import yy.doctor.util.Util;

/**
 * 资料View
 * @auther : GuoXuan
 * @since : 2017/9/21
 */
public class MaterialView extends LinearLayout {

    public static final int KFileLimit = 3; // 资料最多显示多少条

    private int mFileNum; // 文件数
    private int mFileType; // 文件类型
    private String mFileId;
    private List<File> mFiles;

    private View mLayoutData; // 查看资料
    private View mDividerLarge; // 分割线(大)
    private View mDivider; // 分割线
    private TextView mTvFileNum; // 文件个数
    private ImageView mIvFileArrow; // 箭头
    private LinearLayout mLayoutFiles; // 文件布局

    public MaterialView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);

        View v = inflate(getContext(), R.layout.layout_material, null);
        mLayoutData = v.findViewById(R.id.material_layout_data);
        mDividerLarge = v.findViewById(R.id.material_large_divider);
        mDivider = v.findViewById(R.id.material_divider);
        mTvFileNum = (TextView) v.findViewById(R.id.material_tv_data);
        mIvFileArrow = (ImageView) v.findViewById(R.id.material_iv_data);
        mLayoutFiles = (LinearLayout) v.findViewById(R.id.material_layout_file);
        addView(v);
    }

    public MaterialView setNum(int num) {
        mFileNum = num;
        return this;
    }

    public MaterialView setFileType(@FileFrom int type) {
        mFileType = type;
        return this;
    }

    public MaterialView setFiles(List<File> files) {
        mFiles = files;
        return this;
    }

    public MaterialView setFileId(String id) {
        mFileId = id;
        return this;
    }

    public void load() {
        if (mFileNum > KFileLimit) {
            ViewUtil.showView(mIvFileArrow);
            mTvFileNum.setText(String.format(getResources().getString(R.string.meeting_file_more), mFileNum));
            mLayoutData.setOnClickListener(v ->
                    FileActivityRouter.create(mFileId, mFileType).route(getContext()));
        }

        if (mFiles == null || mFiles.size() == 0) {
            ViewUtil.goneView(mDivider);
            ViewUtil.goneView(mLayoutData);
            ViewUtil.goneView(mDividerLarge);
        } else {
            LayoutParams params = LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT);
            for (int i = 0; i < mFiles.size(); i++) {
                mLayoutFiles.addView(getFileView(mFiles.get(i)), params);
            }
        }
    }

    /**
     * 获取每个文件
     */
    private View getFileView(File f) {
        View view = inflate(getContext(), R.layout.layout_unit_num_detail_file_item, null);

        String name = f.getString(TFile.materialName);
        if (TextUtil.isEmpty(name)) {
            name = f.getString(TFile.name);
        }
        TextView tv = (TextView) view.findViewById(R.id.unit_num_detail_file_item_tv_name);
        tv.setText(name);

        view.setOnClickListener(v -> Util.openFile(f, mFileType, String.valueOf(mFileId)));
        LayoutFitter.fit(view);
        return view;
    }

}
