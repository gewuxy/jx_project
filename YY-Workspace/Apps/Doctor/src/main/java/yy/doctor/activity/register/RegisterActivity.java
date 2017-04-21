package yy.doctor.activity.register;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;

/**
 * 注册界面
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class RegisterActivity extends BaseFormActivity {

    private EditText mActivationCode;
    private TextView mGetActivationCode;
    private TextView mRegister;
    public static final int KFromRegister = 1;

    @IntDef({
            RelatedId.email,
            RelatedId.name,
            RelatedId.pwd,
            RelatedId.marksure_pwd,
            RelatedId.location,
            RelatedId.hospital,
            RelatedId.activation_code,
    })
    private @interface RelatedId {
        int email = 0;
        int name = 1;
        int pwd = 2;
        int marksure_pwd = 3;
        int location = 4;
        int hospital = 5;
        int activation_code = 6;
    }

    @Override
    public void initTitleBar() {

    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.email)
                .hint("邮箱")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.name)
                .hint("姓名")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.pwd)
                .hint("密码")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.marksure_pwd)
                .hint("确认密码")
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.location)
                .hint("广东 广州")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.hospital)
                .hint("医院名称")
                .drawable(R.mipmap.ic_more)
                .build());

    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_register_footer);
    }

    @Override
    public void findViews() {
        super.findViews();

        mActivationCode = findView(R.id.register_et_activation_code);
        mGetActivationCode = findView(R.id.register_get_activation_code);
        mRegister = findView(R.id.register);

        mGetActivationCode.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_et_activation_code:
                break;
            case R.id.register_get_activation_code:
                startActivity(ActivationCodeExplainActivity.class);
                break;
            case R.id.register:
                break;
        }
    }


    @Override
    protected void onFormViewClick(View v, int position, Object related) {

        if (v instanceof ImageView) {
            @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
            switch (relatedId) {
                case RelatedId.hospital:
                    startActivityForResult(HospitalActivity.class, KFromRegister);
                    break;
            }
        }

    }
}
