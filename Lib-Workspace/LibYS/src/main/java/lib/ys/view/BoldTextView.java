package lib.ys.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class BoldTextView extends TextView {

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getPaint().setFakeBoldText(true);
    }

}
