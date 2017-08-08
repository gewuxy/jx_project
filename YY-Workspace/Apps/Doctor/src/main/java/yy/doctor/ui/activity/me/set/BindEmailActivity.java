package yy.doctor.ui.activity.me.set;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.RegexUtil;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

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

    private EditText mEtEmail;

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.et_email)
                .related(RelatedId.email)
                .hint("请输入邮箱地址")
                .layout(R.layout.form_edit_bind_email));

        addItem(Form.create(FormType.divider));
    }

    @Override
    public void setViews() {
        super.setViews();

        mEtEmail = getRelatedItem(RelatedId.email).getHolder().getEt();
        mEtEmail.addTextChangedListener(this);
    }

    @Override
    public CharSequence getNavBarText() {
        return "账号绑定";
    }

    @Override
    public CharSequence getSetText() {
        return "发送邮箱验证";
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(NetFactory.bindEmail(Util.getEtString(mEtEmail)));
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        Result r = (Result) result;
        if (r.isSucceed()) {
            showToast("成功发送验证邮件, 请查收");
        } else {
            showToast(r.getError());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(RegexUtil.isEmail(Util.getEtString(mEtEmail)));
    }
}