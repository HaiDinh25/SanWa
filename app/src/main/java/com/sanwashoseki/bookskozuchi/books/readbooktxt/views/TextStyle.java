package com.sanwashoseki.bookskozuchi.books.readbooktxt.views;

import android.graphics.Paint;
import android.graphics.Typeface;

public class TextStyle {

    public Paint paint;
    public float fontSpace;
    public float lineSpace;

    TextStyle(int size, int color, Typeface typeface, float spacing) {
        this.paint = new Paint();
        this.paint.setTextSize(size);
        this.paint.setColor(color);
        this.paint.setTypeface(typeface);
        this.paint.setAntiAlias(true);
        this.paint.setSubpixelText(true);
        this.fontSpace = size;
        this.lineSpace = this.fontSpace * spacing;
    }

}
