package yy.doctor.ui.activity.me.set;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.NetworkResp;
import lib.ys.util.RegexUtil;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
public class BindEmailActivity extends BaseSetActivity implements TextWatcher {

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
    protected void bind() {
        exeNetworkReq(NetFactory.bindEmail(Util.getEtString(mEtEmail)));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            showToast("已发送验证码");

        } else {
            showToast(r.getError());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setChanged(RegexUtil.isEmail(Util.getEtString(mEtEmail)));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}