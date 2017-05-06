package lib.ys.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class UnderLineTextView extends TextView {

    public UnderLineTextView(Context context) {
        super(context);
    }

    public UnderLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UnderLineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        super.setText(content, type);
    }
}
