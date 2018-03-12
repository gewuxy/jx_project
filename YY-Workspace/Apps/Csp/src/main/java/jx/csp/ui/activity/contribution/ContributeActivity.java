package jx.csp.ui.activity.contribution;

import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import jx.csp.R;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;

/**
 * 立即投稿
 *
 * @author CaiXiang
 * @since 2018/3/12
 */

public class ContributeActivity extends BaseActivity {

    private NetworkImageView mIvUnitNum;
    private TextView mTvUnitNumName;
    private NetworkImageView mIvCover;
    private TextView mTvMeetName;
    private TextView mTvTime;
    private TextView mTvMeetType;
    private LinearLayout mLayoutRemuneration;
    private TextView mTvRemuneration;
    private EditText mEtContactWay;
    private EditText mEtRemuneration;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_contribute;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.drawable.default_ic_close, v -> showToast("close"));
        bar.addTextViewRight(R.string.immediately_contribute, R.color.text_ace400, v -> showToast("contribute"));
    }

    @Override
    public void findViews() {

        mIvUnitNum = findView(R.id.contribute_iv_unit_num);
        mTvUnitNumName = findView(R.id.contribute_tv_unit_num_name);
        mIvCover = findView(R.id.contribute_iv_meet_cover);
        mTvMeetName = findView(R.id.contribute_tv_meet_name);
        mTvTime = findView(R.id.contribute_tv_time);
        mTvMeetType = findView(R.id.contribute_tv_meet_type);
        mLayoutRemuneration = findView(R.id.contribute_layout_remuneration);
        mTvRemuneration = findView(R.id.contribute_tv_remuneration);
        mEtContactWay = findView(R.id.contribute_et_contact_way);
    }

    @Override
    public void setViews() {

        mIvUnitNum.placeHolder(R.drawable.ic_default_unit_num).load();
        mIvCover.placeHolder(R.drawable.ic_default_contribute_cover).load();
        setOnClickListener(R.id.contribute_tv_change);
    }
}
