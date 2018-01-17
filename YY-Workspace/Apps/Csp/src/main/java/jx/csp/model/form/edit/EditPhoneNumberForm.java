package jx.csp.model.form.edit;

import android.text.Editable;

import jx.csp.R;
import jx.csp.util.Util;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.ys.ConstantsEx;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;

/**
 * FIXME: 逻辑待调整. 有问题
 *
 * @auther WangLan
 * @since 2017/7/4
 */

public class EditPhoneNumberForm extends EditForm {

    private boolean mIsAdd;

    @Override
    public int getContentViewResId() {
        int layout = getLayoutId();
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
        return R.layout.form_edit_phone_number;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //after只能等于1和0，等于1是输入，等于0是删除
        if (after == 1) {
            mIsAdd = true;
        } else {
            mIsAdd = false;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        super.afterTextChanged(s);
        int length = s.length();
        if (mIsAdd) {
            String str = s.toString();
            if (length == 11 && Util.isMobileCN(str)) {
                Notifier.inst().notify(NotifyType.fetch_message_captcha);
            }
        } else {
            Notifier.inst().notify(NotifyType.disable_fetch_message_captcha);
        }

        if (TextUtil.isNotEmpty(s)) {
            ViewUtil.showView(getHolder().getIvClean());
        } else {
            ViewUtil.goneView(getHolder().getIvClean());
        }
    }
}
