package yy.doctor.model.form.text;

import android.graphics.Color;
import android.view.View;

import yy.doctor.dialog.BottomDialog;
import yy.doctor.model.config.Configs;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class TextDialogForm extends TextForm {

    private static final int KColorNormal = Color.parseColor("#666666");

    @Override
    public boolean onItemClick(Object host, View v) {
        Configs data = getData();
        BottomDialog dialog = new BottomDialog(v.getContext(), position -> {
            save(data.get(position).getName(), data.get(position).getVal());
            refresh();
        });

        for (int i = 0; i < data.size(); ++i) {
            dialog.addItem(data.get(i).getName(), KColorNormal);
        }
        dialog.show();
        return true;
    }

}
