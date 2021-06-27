package com.sanwashoseki.bookskozuchi.books.readbooktxt.views;

import android.graphics.PointF;

public class CurrentState {
    String strPrev;
    String strCurr;
    String strNext;

    String rubyText = "";
    String bodyText = "";
    String bufferText = "";
    boolean isTitle = false;
    boolean isDrawEnable = true;
    boolean isRubyEnable = true;
    boolean isRuby = false;
    boolean isKanjiBlock = false;
    boolean isRubyBody = false;
    boolean lineChangeable = true;
    boolean isPageEnd = false;
    int textIndex = 0;

    PointF textPosition;
    PointF rubyPosition;

    PointF rubyStart;
    PointF rubyEnd ;

    CurrentState(){
        strPrev="";
        strNext ="";
        strCurr ="";
        textPosition = new PointF();
        rubyPosition = new PointF();
        rubyStart = new PointF();
        rubyEnd = new PointF();
    }

    public CurrentState clone() {
        CurrentState stt = new CurrentState();
        stt.textPosition = textPosition;
        stt.textIndex = textIndex;
        stt.bodyText = bodyText;
        return stt;
    }

    @Override
    public String toString() {
        return "CurrentState{" +
                "strPrev='" + strPrev + '\'' +
                ", strCurr='" + strCurr + '\'' +
                ", strNext='" + strNext + '\'' +
                ", rubyText='" + rubyText + '\'' +
                ", bodyText='" + bodyText + '\'' +
                ", bufferText='" + bufferText + '\'' +
                ", isTitle=" + isTitle +
                ", isDrawEnable=" + isDrawEnable +
                ", isRubyEnable=" + isRubyEnable +
                ", isRuby=" + isRuby +
                ", isKanjiBlock=" + isKanjiBlock +
                ", isRubyBody=" + isRubyBody +
                ", lineChangeable=" + lineChangeable +
                ", isPageEnd=" + isPageEnd +
                ", textIndex=" + textIndex +
                ", textPosition=" + textPosition +
                ", rubyPosition=" + rubyPosition +
                ", rubyStart=" + rubyStart +
                ", rubyEnd=" + rubyEnd +
                '}';
    }
}
