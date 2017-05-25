package yy.doctor.model.form.text;

import android.graphics.Color;
import android.view.View;

import java.util.List;

import yy.doctor.dialog.BottomDialog;
import yy.doctor.dialog.BottomDialog.OnDialogItemClickListener;
import yy.doctor.model.config.Config;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class FITextDialog extends FIText {

    private static final int KColorNormal = Color.parseColor("#666666");

    @Override
    public boolean onItemClick(Object host, View v) {

        final List<Config> data = getList(TFormElem.data);
        BottomDialog dialog = new BottomDialog(v.getContext(), new OnDialogItemClickListener() {

            @Override
            public void onDialogItemClick(int position) {

                put(TFormElem.text, data.get(position).getName());
                put(TFormElem.val, data.get(position).getVal());
                refresh();
            }
        });
        for (int i = 0; i < data.size(); ++i) {
            dialog.addItem(data.get(i).getName(), KColorNormal);
        }
        dialog.show();
        return true;
    }

}
