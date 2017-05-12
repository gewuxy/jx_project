package lib.yy.test;

import android.view.View;
import android.widget.TextView;

import lib.ys.adapter.VH.ViewHolderEx;
import lib.yy.R;

/**
 * @author yuansui
 */
public class TestVH extends ViewHolderEx {

    public TestVH(View convertView) {
        super(convertView);
    }

    public TextView getBtn() {
        return (TextView) getView(R.id.test_btn);
    }
}
