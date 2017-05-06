package lib.ys.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class LetterSpacingTextView extends TextView {

    private float mLetterSpacing;
    private CharSequence mOriginalText = "";

    public LetterSpacingTextView(Context context) {
        super(context);
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public float getLetterSpacing() {
        return mLetterSpacing;
    }

    public void setLetterSpacing(float letterSpacing) {
        mLetterSpacing = letterSpacing;
        applyLetterSpacing();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mOriginalText = text;
        applyLetterSpacing();
    }

    @Override
    public CharSequence getText() {
        return mOriginalText;
    }

    private void applyLetterSpacing() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mOriginalText.length(); i++) {
            builder.append(mOriginalText.charAt(i));
            if (i + 1 < mOriginalText.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if (builder.toString().length() > 1) {
            for (int i = 1; i < builder.toString().length(); i += 2) {
                finalText.setSpan(new ScaleXSpan((mLetterSpacing + 1) / 10), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);
    }
}
