package yy.doctor.model.form.edit;

import android.os.Build.VERSION_CODES;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import lib.ys.util.DeviceUtil;
import lib.yy.adapter.VH.FormVH;
import lib.yy.model.form.BaseForm;
import yy.doctor.Constants;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/17
 */
public class EditForm extends BaseForm {

    @Override
    public boolean check() {
        return checkInput();
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_edit;
    }

    @Override
    protected void init(FormVH holder) {
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
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        int d = getDrawable();
        ImageView iv = holder.getIv();
        if (iv != null) {
            if (d != Constants.KInvalidValue) {
                iv.setOnClickListener(this);
            }
            setIvIfValid(iv, d);
        }


        holder.getEt().setEnabled(isEnabled());
        holder.getEt().setText(getText());
        holder.getEt().setHint(getHint());
    }

    @Override
    protected boolean onViewClick(View v) {
        return super.onViewClick(v);
    }
}
