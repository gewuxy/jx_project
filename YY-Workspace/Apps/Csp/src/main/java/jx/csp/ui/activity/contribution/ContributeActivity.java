package jx.csp.ui.activity.contribution;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.constant.AppType;
import jx.csp.dialog.ContributeChargeDialog;
import jx.csp.model.Profile;
import jx.csp.sp.SpApp;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;

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
    private View mLayoutDivider;
    private EditText mEtContactWay;
    private ImageView mIvClear;

    private int mCharge;

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
        mLayoutDivider = findView(R.id.contribute_large_divider);
        mEtContactWay = findView(R.id.contribute_et_contact_way);
        mIvClear = findView(R.id.contribute_iv_clear_contact_way);
    }

    @Override
    public void setViews() {

        if (SpApp.inst().getAppType() == AppType.overseas) {
            goneView(mLayoutDivider);
            goneView(mLayoutRemuneration);
        }

        String contact_way = Profile.inst().getContactWay();
        if (TextUtil.isNotEmpty(contact_way)) {
            mEtContactWay.setText(contact_way);
            mEtContactWay.setSelection(contact_way.length());
            showView(mIvClear);
        }

        mIvUnitNum.placeHolder(R.drawable.ic_default_unit_num).load();
        mIvCover.placeHolder(R.drawable.ic_default_contribute_cover).load();
        mEtContactWay.addTextChangedListener(new TextWatcher() {
            
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
        setOnClickListener(R.id.contribute_tv_change);
        setOnClickListener(mLayoutRemuneration);
        setOnClickListener(mIvClear);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contribute_layout_remuneration: {
                if (mTvRemuneration.getText().toString().equals(getString(R.string.free_use))) {
                    mCharge = 0;
                }
                ContributeChargeDialog d = new ContributeChargeDialog(this);
                d.setCharge(mCharge);
                d.setOnDismissListener(dialog -> {
                    mCharge = d.getCharge();
                    if (mCharge == 0) {
                        mTvRemuneration.setText(R.string.free_use);
                    } else {
                        mTvRemuneration.setText(String.format(getString(R.string.money), mCharge));
                    }
                });
                d.setCancelable(false);
                d.show();
            }
            break;
            case R.id.contribute_iv_clear_contact_way: {
                mEtContactWay.setText("");
            }
            break;
            case R.id.contribute_tv_change: {
                showToast("change");
            }
            break;
        }
    }
}
