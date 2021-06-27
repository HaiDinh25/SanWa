package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RotateView extends RelativeLayout {

    boolean isRotate = false;
    private int width = 0;
    private int height = 0;

    public void setRotate(boolean rotate) {
        isRotate = rotate;
    }

    public RotateView(Context context) {
        super(context);
    }

    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RotateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isRotate){
            if (width == 0 && height == 0) {
                width = widthMeasureSpec;
                height = heightMeasureSpec;
            }
            super.onMeasure(height, width);
            setTranslationX((width - height) / 2);
            setTranslationY((height - width) / 2);
        } else  {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setTranslationX(0);
            setTranslationY(0);
        }

    }
}
