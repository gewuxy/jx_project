package jx.doctor.ui.activity.me.set;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.text.Editable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.RegexUtil;
import jx.doctor.R;
import jx.doctor.model.form.Form;
import jx.doctor.model.form.FormType;
import jx.doctor.network.NetworkApiDescriptor.UserAPI;

/**
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
public class BindEmailActivity extends BaseSetActivity {

    @IntDef({
            RelatedId.email,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int email = 0;
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.et)
                .related(RelatedId.email)
                .hint("输入邮箱地址")
                .textWatcher(this)
                .layout(R.layout.form_edit_bind_email));

        addItem(Form.create(FormType.divider));
    }

    @Override
    public CharSequence getNavBarText() {
        return "绑定邮箱号";
    }

    @Override
    public CharSequence getSetText() {
        return "发送验证邮件";
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        String email = getRelatedString(RelatedId.email);
        exeNetworkReq(UserAPI.bindEmail(email.trim()).build());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (r.isSucceed()) {
            showToast("成功发送");
            finish();
        } else {
            showToast(r.getMessage());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(RegexUtil.isEmail(getRelatedString(RelatedId.email)));
    }
}