package yy.doctor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动补全邮箱后缀
 */
public class AutoCompleteEditText extends EditText implements TextWatcher {

    private Bitmap mBitmap;
    private int mHeight;
    private int mWidth;
    private Paint mPaint;
    private int mBaseLine;
    private Canvas mCanvas;
    private BitmapDrawable mDrawable;
    private boolean mFlag;
    private String mAddedText;

    private ImageView clearImage;

    /**
     * 邮箱存储列表
     */
    private List<String> emailList = new ArrayList<String>();

    public AutoCompleteEditText(Context context) {
        super(context);
        initEmail();
        addTextChangedListener(this);
    }

    public AutoCompleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEmail();
        addTextChangedListener(this);
    }

    public AutoCompleteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initEmail();
        addTextChangedListener(this);
    }

    public void setClearImage(ImageView image) {
        this.clearImage = image;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (clearImage == null) {
            return;
        }
        if (s.length() != 0) {    //有输入数据的时候，清除按钮才显示出来
            clearImage.setVisibility(View.VISIBLE);
        } else {
            clearImage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (!focused) {
            if (mAddedText != null) {
                append(mAddedText);
            }
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        mFlag = true;
        //匹配邮箱字符串的方法
        if (text.contains("@")) {    //是否有@
            int start = text.indexOf("@");
            if (start == 0) {    //如果第一个为@，则不添加后缀
                return;
            }
            String startStr = text.substring(start);    //找出以@开头的字符串
            for (int i = 0; i < emailList.size(); i++) {    //匹配出默认的邮箱地址
                String emailSuffix = emailList.get(i);
                if (emailSuffix.startsWith(startStr)) {    //能匹配出第一个邮箱地址的话，就退出查询
                    mAddedText = emailSuffix.substring(startStr.length());
                    drawAddedText(mAddedText);
                    mFlag = false;
                    break;
                }
            }
        }
        // 如果没有匹配，就画一个空  
        if (mFlag) {
            drawAddedText("");
            mAddedText = "";
        }
    }

    /**
     * 画出后缀字符串
     *
     * @param addedText
     */
    private void drawAddedText(String addedText) {
        // 如果字符串为空，画空  
        if (addedText.equals("")) {
            setCompoundDrawables(null, null, null, null);
            return;
        }
        // 只需要初始化一次  
        if (mBitmap == null) {
            mHeight = getHeight();
            mWidth = getWidth();
            // 初始化画笔  
            mPaint = new Paint();
            mPaint.setColor(Color.GRAY);
            mPaint.setAntiAlias(true);// 去除锯齿  
            mPaint.setFilterBitmap(true);// 对位图进行滤波处理  
            mPaint.setTextSize(getTextSize());
        }
        // 计算baseLine  
        Rect rect = new Rect();
        int baseLineLocation = getLineBounds(0, rect);
        mBaseLine = baseLineLocation - rect.top;

        // 添加的字符窜的长度  
        int addedTextWidth = (int) (mPaint.measureText(addedText) + 1);

        // 创建bitmap  
        mBitmap = Bitmap.createBitmap(addedTextWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // 绘制后缀字符串  
        mCanvas.drawText(addedText, 0, mBaseLine, mPaint);
        // bitmap转化为Drawable  
        mDrawable = new BitmapDrawable(mBitmap);

        String text = getText().toString();
        // 计算后缀字符串在输入框中的位置  
        int addedTextLeft = (int) (mPaint.measureText(text) - mWidth + addedTextWidth);
        int addedTextRight = addedTextLeft + addedTextWidth;
        int addedTextTop = 0;
        int addedTextBottom = addedTextTop + mHeight;
        // 设置后缀字符串位置  
        mDrawable.setBounds(addedTextLeft, addedTextTop, addedTextRight, addedTextBottom);
        // 显示后缀字符串  
        setCompoundDrawables(null, null, mDrawable, null);
    }

    /**
     * 初始化常用的邮箱
     */
    private void initEmail() {
        emailList.add("@qq.com");
        emailList.add("@aliyun.com");
        emailList.add("@bellsouth.net");
        emailList.add("@china.com");
        emailList.add("@comcast.net");
        emailList.add("@earthlink.net");
        emailList.add("@gmail.com");
        emailList.add("@googlemail.com");
        emailList.add("@hanmail.net");
        emailList.add("@hotmail.com");
        emailList.add("@outlook.com");
        emailList.add("@medcn.cn");
        emailList.add("@sina.com");
        emailList.add("@sina.cn");
        emailList.add("@sohu.com");
        emailList.add("@vip.sina.com");
        emailList.add("@wo.com.cn");
        emailList.add("@126.com");
        emailList.add("@139.com");
        emailList.add("@163.com");
        emailList.add("@189.com");
    }
}  