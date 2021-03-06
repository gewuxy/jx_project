package jx.csp.model.form.text;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.Extra;
import lib.jx.adapter.VH.FormVH;

/**
 * @author HuoXuYu
 * @since 2017/9/25
 */
public class IntentForm extends TextForm {

    @IntDef({
            IntentType.un_know,
            IntentType.name,
            IntentType.intro,
            IntentType.common,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface IntentType {
        int un_know = 0;
        int name = 1;
        int intro = 2;
        int common = 100; // 通用
    }

    private int mCurrType = IntentType.un_know;

    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        mCurrType = getType();
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);
        holder.getTvText().setText(getText());
    }

    @Override
    public boolean onItemClick(Object host, View v) {
        if (mCurrType == IntentType.un_know) {
            // 不处理
            return false;
        }

        Intent i = getIntent();
        if (i != null) {
            i.putExtra(Extra.KLimit, getLimit());
            startActivityForResult(i, getPosition());
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int position, Intent data) {
        switch (mCurrType) {
            case IntentType.name: {
                String name = data.getStringExtra(Extra.KData);
                save(name, name);
            }
            break;
            case IntentType.intro: {
                String intro = data.getStringExtra(Extra.KData);
                save(intro, intro);
            }
            break;
            default: {
                // location不处理
            }
            break;
        }
    }
}
