package jx.csp.ui.activity.contribution;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.constant.AppType;
import jx.csp.dialog.ContributeChargeDialog;
import jx.csp.model.Profile;
import jx.csp.model.contribution.UnitNum;
import jx.csp.model.contribution.UnitNum.TUnitNum;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.CourseType;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import jx.csp.sp.SpApp;
import jx.csp.util.Util;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.network.image.shape.CircleRenderer;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.TimeFormatter;
import lib.ys.util.TimeFormatter.TimeFormat;

/**
 * 立即投稿
 *
 * @author CaiXiang
 * @since 2018/3/12
 */
@Route
public class ContributeActivity extends BaseActivity {

    @Arg
    Meet mMeet;

    @Arg
    UnitNum mUnitNum;

    private TextView mTvContribute;
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

    private int mCharge = 0;

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
        bar.addViewLeft(R.drawable.default_ic_close, v -> {
            notify(NotifyType.finish_contribute);
            finish();
        });
        mTvContribute = bar.addTextViewRight(R.string.immediately_contribute, R.color.text_ace400, v -> {
            String str = mEtContactWay.getText().toString();
            refresh(RefreshWay.dialog);
            if (SpApp.inst().getAppType() == AppType.inland) {
                exeNetworkReq(DeliveryAPI.immediatelyContribute(mUnitNum.getInt(TUnitNum.unitNumId),
                        mMeet.getInt(TMeet.id), mUnitNum.getInt(TUnitNum.id))
                        .contact(str)
                        .build());
            } else {
                exeNetworkReq(DeliveryAPI.immediatelyContribute(mUnitNum.getInt(TUnitNum.unitNumId),
                        mMeet.getInt(TMeet.id), mUnitNum.getInt(TUnitNum.id))
                        .contact(str)
                        .remuneration(mCharge)
                        .build());
            }
        });
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

        Util.setTextViewBackground(mTvContribute);

        if (SpApp.inst().getAppType() == AppType.overseas) {
            goneView(mLayoutDivider);
            goneView(mLayoutRemuneration);
        }

        mIvUnitNum.placeHolder(R.drawable.ic_default_unit_num)
                .url(mUnitNum.getString(TUnitNum.imgUrl))
                .renderer(new CircleRenderer())
                .load();
        mTvUnitNumName.setText(mUnitNum.getString(TUnitNum.platformName));
        setOnClickListener(R.id.contribute_tv_change);

        mIvCover.placeHolder(R.drawable.ic_default_contribute_cover)
                .url(mMeet.getString(TMeet.coverUrl))
                .load();
        mTvMeetName.setText(mMeet.getString(TMeet.title));
        if (mMeet.getInt(TMeet.playType) == CourseType.reb) {
            String time = mMeet.getString(TMeet.playTime);
            if (TextUtil.isEmpty(time)) {
                time = Util.getSpecialTimeFormat(0, "'", "\"");
            }
            mTvTime.setText(time);
            mTvMeetType.setText(R.string.recorded);
        } else {
            mTvTime.setText(TimeFormatter.milli(mMeet.getLong(TMeet.startTime), TimeFormat.form_MM_dd_24));
            mTvMeetType.setText(R.string.solive);
        }

        setOnClickListener(mLayoutRemuneration);

        String contact_way = Profile.inst().getContactWay();
        if (TextUtil.isNotEmpty(contact_way)) {
            mEtContactWay.setText(contact_way);
            mEtContactWay.setSelection(contact_way.length());
            showView(mIvClear);
        }
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
                finish();
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.error(resp.getText());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (r.isSucceed()) {
            showToast(R.string.contribute_platform_succeed);
            notify(NotifyType.finish_contribute);
            finish();
        } else {
            showToast(R.string.contribute_fail);
        }
    }
}
