package com.sanwashoseki.bookskozuchi.books.readbooktxt.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class SWPageView extends AppCompatImageView {

    private int pageIndex;
    private VTextView vTextView;

    public SWPageView(Context context) {
        super(context);
    }

    public SWPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SWPageView(Context context, AttributeSet attrs , int def) {
        super(context, attrs, def);
    }

    public void setData(VTextView vTextView, int pageIndex) {
        this.pageIndex = pageIndex;
        this.vTextView = vTextView;
    }

    @Override
    public void onDraw(Canvas canvas) {
        vTextView.drawPage(canvas, pageIndex);
    }

}