package yy.doctor.activity;

import android.view.View;

import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;

public class RegisterActivity extends BaseFormActivity {

    @Override
    public void initTitleBar() {

    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et_register)
                .hint(R.string.register_email)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .hint(R.string.register_name)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .hint(R.string.register_password)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .hint(R.string.register_password_marksure)
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et_register)
                .hint(R.string.register_location)
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .hint(R.string.register_hospital)
                .drawable(R.mipmap.ic_more_hospital)
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et_register)
                .hint(R.string.register_key)
                .build());
    }

    @Override
    protected View createFooterView() {
        return super.createFooterView();
    }





}
