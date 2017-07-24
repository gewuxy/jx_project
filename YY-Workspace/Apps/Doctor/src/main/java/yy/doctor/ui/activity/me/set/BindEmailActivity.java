package yy.doctor.ui.activity.me.set;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import lib.ys.util.RegexUtil;
import yy.doctor.R;
import yy.doctor.model.form.Builder;

import yy.doctor.model.form.FormType;
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
  public CharSequence getNavBarText()
  {
    return "账号绑定";
  }

  @Override
  public CharSequence getSetText()
  {
    return "发送邮箱验证";
  }

  @Override
  public void initData() {
    super.initData();

    addItem(new Builder(FormType.et_email)
            .related(RelatedId.email)
            .hint("请输入邮箱地址")
            .layout(R.layout.form_edit_bind_email)
            .build());

    addItem(new Builder(FormType.divider)
            .build());
  }

  @Override
  public void setViews() {
    super.setViews();

    mEtEmail = getRelatedItem(RelatedId.email).getHolder().getEt();
    mEtEmail.addTextChangedListener(this);
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