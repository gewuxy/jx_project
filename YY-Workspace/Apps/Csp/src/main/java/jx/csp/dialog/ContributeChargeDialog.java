package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import jx.csp.R;
import lib.jx.dialog.BaseDialog;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;

/**
 * 投稿收费的dialog 自定义键盘
 *
 * @author CaiXiang
 * @since 2018/3/12
 */

public class ContributeChargeDialog extends BaseDialog {

    private TextView mTvCharge;
    private TextView mTvRMB;
    private TextView mTvMoney;
    private CheckBox mCb;
    private TextView mTvFree;

    private int mCharge = 0;
    private boolean mBack = false;

    public ContributeChargeDialog(Context context) {
        super(context);
    }

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_contribute_charge;
    }

    @Override
    public void findViews() {
        mTvCharge = findView(R.id.contribute_charge_tv_charge);
        mTvRMB = findView(R.id.contribute_charge_tv_rmb);
        mTvMoney = findView(R.id.contribute_charge_tv_money);
        mCb = findView(R.id.contribute_charge_cb);
        mTvFree = findView(R.id.contribute_charge_tv_free);
    }

    @Override
    public void setViews() {

        setGravity(Gravity.BOTTOM);
        setOnClickListener(R.id.contribute_charge_tv_1);
        setOnClickListener(R.id.contribute_charge_tv_2);
        setOnClickListener(R.id.contribute_charge_tv_3);
        setOnClickListener(R.id.contribute_charge_tv_4);
        setOnClickListener(R.id.contribute_charge_tv_5);
        setOnClickListener(R.id.contribute_charge_tv_6);
        setOnClickListener(R.id.contribute_charge_tv_7);
        setOnClickListener(R.id.contribute_charge_tv_8);
        setOnClickListener(R.id.contribute_charge_tv_9);
        setOnClickListener(R.id.contribute_charge_tv_0);
        setOnClickListener(R.id.contribute_charge_tv_back);
        setOnClickListener(R.id.contribute_charge_layout_clear);
        setOnClickListener(R.id.contribute_charge_tv_confirm);
        mCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mTvCharge.setTextColor(ResLoader.getColor(R.color.text_9699a2));
                mTvRMB.setTextColor(ResLoader.getColor(R.color.text_9699a2));
                mTvMoney.setTextColor(ResLoader.getColor(R.color.text_9699a2));
                mTvFree.setTextColor(ResLoader.getColor(R.color.text_9699a2));
            } else {
                mTvCharge.setTextColor(ResLoader.getColor(R.color.text_404356));
                mTvRMB.setTextColor(ResLoader.getColor(R.color.text_404356));
                mTvMoney.setTextColor(ResLoader.getColor(R.color.text_404356));
                mTvFree.setTextColor(ResLoader.getColor(R.color.text_404356));
            }
        });
    }

    @Override
    public void onClick(View v) {
        StringBuilder sb = new StringBuilder(mTvMoney.getText().toString().trim());
        String s = sb.toString();
        switch (v.getId()) {
            case R.id.contribute_charge_tv_1: {
                if (s.equals("0")) {
                    sb.deleteCharAt(0);
                }
                sb.append(1);
                mTvMoney.setText(sb.toString());
                if (mCb.isChecked()) {
                    mCb.setChecked(false);
                }
            }
            break;
            case R.id.contribute_charge_tv_2: {
                if (s.equals("0")) {
                    sb.deleteCharAt(0);
                }
                sb.append(2);
                mTvMoney.setText(sb.toString());
                if (mCb.isChecked()) {
                    mCb.setChecked(false);
                }
            }
            break;
            case R.id.contribute_charge_tv_3: {
                if (s.equals("0")) {
                    sb.deleteCharAt(0);
                }
                sb.append(3);
                mTvMoney.setText(sb.toString());
                if (mCb.isChecked()) {
                    mCb.setChecked(false);
                }
            }
            break;
            case R.id.contribute_charge_tv_4: {
                if (s.equals("0")) {
                    sb.deleteCharAt(0);
                }
                sb.append(4);
                mTvMoney.setText(sb.toString());
                if (mCb.isChecked()) {
                    mCb.setChecked(false);
                }
            }
            break;
            case R.id.contribute_charge_tv_5: {
                if (s.equals("0")) {
                    sb.deleteCharAt(0);
                }
                sb.append(5);
                mTvMoney.setText(sb.toString());
                if (mCb.isChecked()) {
                    mCb.setChecked(false);
                }
            }
            break;
            case R.id.contribute_charge_tv_6: {
                if (s.equals("0")) {
                    sb.deleteCharAt(0);
                }
                sb.append(6);
                mTvMoney.setText(sb.toString());
                if (mCb.isChecked()) {
                    mCb.setChecked(false);
                }
            }
            break;
            case R.id.contribute_charge_tv_7: {
                if (s.equals("0")) {
                    sb.deleteCharAt(0);
                }
                sb.append(7);
                mTvMoney.setText(sb.toString());
                if (mCb.isChecked()) {
                    mCb.setChecked(false);
                }
            }
            break;
            case R.id.contribute_charge_tv_8: {
                if (s.equals("0")) {
                    sb.deleteCharAt(0);
                }
                sb.append(8);
                mTvMoney.setText(sb.toString());
                if (mCb.isChecked()) {
                    mCb.setChecked(false);
                }
            }
            break;
            case R.id.contribute_charge_tv_9: {
                if (s.equals("0")) {
                    sb.deleteCharAt(0);
                }
                sb.append(9);
                mTvMoney.setText(sb.toString());
                if (mCb.isChecked()) {
                    mCb.setChecked(false);
                }
            }
            break;
            case R.id.contribute_charge_tv_0: {
                if (!mTvMoney.getText().toString().equals("0")) {
                    sb.append(0);
                    mTvMoney.setText(sb.toString());
                    if (mCb.isChecked()) {
                        mCb.setChecked(false);
                    }
                }
            }
            break;
            case R.id.contribute_charge_tv_back: {
                mBack = true;
                dismiss();
            }
            break;
            case R.id.contribute_charge_layout_clear: {
                if (mTvMoney.getText().toString().length() != 0) {
                    String str = sb.toString();
                    mTvMoney.setText(str.substring(0, str.length() - 1));
                }
            }
            break;
            case R.id.contribute_charge_tv_confirm: {
                dismiss();
            }
            break;
        }
    }

    public void setCharge(int charge) {
        mCharge = charge;
        if (mCharge == 0) {
            mCb.setChecked(true);
            mTvCharge.setTextColor(ResLoader.getColor(R.color.text_9699a2));
            mTvRMB.setTextColor(ResLoader.getColor(R.color.text_9699a2));
            mTvMoney.setTextColor(ResLoader.getColor(R.color.text_9699a2));
            mTvFree.setTextColor(ResLoader.getColor(R.color.text_9699a2));
        } else {
            mCb.setChecked(false);
            mTvMoney.setText(String.valueOf(mCharge));
            mTvCharge.setTextColor(ResLoader.getColor(R.color.text_404356));
            mTvRMB.setTextColor(ResLoader.getColor(R.color.text_404356));
            mTvMoney.setTextColor(ResLoader.getColor(R.color.text_404356));
            mTvFree.setTextColor(ResLoader.getColor(R.color.text_404356));
        }
    }

    public int getCharge() {
        if (!mBack) {
            if (mCb.isChecked()) {
                mCharge = 0;
            } else {
                if (TextUtil.isNotEmpty(mTvMoney.getText().toString())) {
                    mCharge = Integer.valueOf(mTvMoney.getText().toString());
                } else {
                    mCharge = 0;
                }
            }
        }
        return mCharge;
    }
}
