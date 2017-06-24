package yy.doctor.model.form.edit;

import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import lib.ys.util.DeviceUtil;
import lib.yy.adapter.VH.FormItemVH;
import lib.yy.model.form.BaseForm;
import yy.doctor.Constants;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/4/17
 */
public class EditForm extends BaseForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.et;
    }

    @Override
    public boolean check() {
        return checkInput();
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_edit;
    }

    @Override
    protected void init(FormItemVH holder) {
        super.init(holder);

        holder.getEt().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                save(s.toString(), s.toString());
            }
        });

        // 5.0以上的版本设置水波纹点击背景
        if (DeviceUtil.getSDKVersion() >= VERSION_CODES.LOLLIPOP) {
            if (holder.getIv() != null) {
                holder.getIv().setBackgroundResource(R.drawable.item_selector_unbound);
            }
        }
    }

    @Override
    protected void refresh(FormItemVH holder) {
        super.refresh(holder);

        int d = getInt(TFormElem.drawable);
        if (d != Constants.KInvalidValue) {
            holder.getIv().setOnClickListener(this);
        }
        setIvIfValid(holder.getIv(), d);

        holder.getEt().setEnabled(getBoolean(TFormElem.enable));
        holder.getEt().setText(getString(TFormElem.text));
        holder.getEt().setHint(getString(TFormElem.hint));
    }

    @Override
    protected boolean onViewClick(View v) {
        return super.onViewClick(v);
    }
}