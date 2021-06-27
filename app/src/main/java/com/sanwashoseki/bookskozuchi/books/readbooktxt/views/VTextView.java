package com.sanwashoseki.bookskozuchi.books.readbooktxt.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants;

import java.lang.Character.UnicodeBlock;

public class VTextView extends View {

    public static final int MAX_PAGE = 10000;
    public static String TAG = VTextView.class.getSimpleName();
    public static String NEW_LINE = "\n";
    public static String NEW_PARAGRAPH = "\r\n";
    public static String DOT_CHAR = "。";
    private final int[] pageIndices = new int[MAX_PAGE];
    private final int[] sentenceIndicies = new int[MAX_PAGE];
    OnPageCalculatorListener onPageCalculatorListener;
    private int totalPage = -1;
    private int totalSentence = -1;
    private boolean isVertical = true;
    private int bodySize = 36;
    private int width;
    private int height;
    private float lineSpaceRate = 1.0f;
    private float padding = SWConstants.PADDING_MEDIUM;
    private float spacing = SWConstants.SPACING_MEDIUM;
    public Typeface typeface;
    public TextStyle titleStyle;
    public TextStyle bodyStyle;
    public TextStyle highlightStyle;
    public TextStyle rubyStyle;
    private String text = "";
    private int textColor = Color.BLACK;
    private int highlightColor = Color.GREEN;
    private int currentIndex = 0;
    private int currentSentence = 0;
    private boolean isUpdatingSentences = true;
    private int focusedSentenceIndex = 0;
    private int startSentenceIndex = 0;
    private int endSentenceIndex = 0;

    public int getTotalSentence() {
        return totalSentence;
    }

    public int getCurrentSentenceIndex() {
        return focusedSentenceIndex;
    }

    public int getSentenceIndexFromLocation(int location) {
        int pageIndex = getCurrentPageIndexFromTextIndex(location);
        return getSentenceIndexFromPageIndex(pageIndex);
    }

    public int getSentenceIndexFromPageIndex(int pageIndex) {
        int sentenceIndex = 0;
        int locationIndex = pageIndices[pageIndex];
        for (int i = 0; i < totalSentence; i++) {
            int sentenceTextIndex = sentenceIndicies[i];
            if (sentenceTextIndex >= locationIndex - 1) {
                sentenceIndex = i;
                break;
            }
        }
        return sentenceIndex;
    }

    public int getStartPageIndex(int sentenceIndex) {
        if (sentenceIndex >= 0 && sentenceIndex < totalSentence) {
            int textIndex = sentenceIndicies[sentenceIndex];
            return getCurrentPageIndexFromTextIndex(textIndex);
        }
        return 0;
    }

    public String setCurrentSentenceIndex(int index) {
        focusedSentenceIndex = index;
        if (focusedSentenceIndex >= 0 && focusedSentenceIndex < totalSentence + 1) {
            startSentenceIndex = sentenceIndicies[focusedSentenceIndex];
            endSentenceIndex = sentenceIndicies[focusedSentenceIndex + 1];
            if (startSentenceIndex >= 0 && startSentenceIndex <= endSentenceIndex && endSentenceIndex <= text.length()) {
                String subStr = text.substring(startSentenceIndex, endSentenceIndex);
                invalidate();
                return subStr;
            }
        }
        invalidate();
        return "";
    }

    public VTextView(Context context) {
        this(context, null);
        init(context);
    }

    public VTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        setFontSize(bodySize);
        setPadding(SWConstants.PADDING_MEDIUM);
        setSpacing(SWConstants.SPACING_MEDIUM);
    }

    public void setText(String text) {
        this.text = text;
        this.reset(true);
    }

    public void setPage(int page) {
        this.currentIndex = page;
        this.invalidate();
    }

    public int getPageWidth() {
        return width;
    }

    public int getPageHeight() {
        return height;
    }

    public int getCurrentTextIndexFromPageIndex(int pageIndex) {
        return pageIndices[pageIndex];
    }

    public int getCurrentPageIndexFromTextIndex(int textIndex) {
        if (totalPage < 1) {
            return 0;
        }
        int pageIndex = -1;
        for (int currentPageIndex = 0; currentPageIndex < totalPage - 1; currentPageIndex++) {
            int minTextIndex = pageIndices[currentPageIndex];
            int maxTextIndex = pageIndices[currentPageIndex + 1];
            if (minTextIndex <= textIndex && maxTextIndex > textIndex) {
                pageIndex = currentPageIndex;
                break;
            }
        }
        if (pageIndex == -1) {
            pageIndex = totalPage - 1;
        }
        return pageIndex;
    }

    public int getCurrentPage() {
        return this.currentIndex;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setColor(int paperColor, int textColor, int highlightColor) {
        Log.e(TAG, "setColor paperColor = " + paperColor + " textColor = " + textColor + " rubyColor = " + highlightColor);
        this.textColor = textColor;
        this.highlightColor = highlightColor;
        titleStyle.paint.setColor(this.textColor);
        bodyStyle.paint.setColor(this.textColor);
        rubyStyle.paint.setColor(this.textColor);
        highlightStyle.paint.setColor(this.highlightColor);
        setBackgroundColor(paperColor);
        reset(true);
    }

    public void setFontName(Typeface typeface) {
        this.typeface = typeface;
        setFontSize(bodySize);
        reset(true);
    }

    public void setFontSize(int size) {
        bodySize = size;
        int titleSize = (int) (bodySize * 1.5);
        int rubySize = (int) (bodySize * 0.5);
        Log.e(TAG, "setFontSize");
        Log.e(TAG, "titleSize = " + titleSize);
        Log.e(TAG, "bodySize = " + bodySize);
        Log.e(TAG, "rubySize = " + rubySize);
        titleStyle = new TextStyle(titleSize, textColor, typeface, spacing);
        bodyStyle = new TextStyle(bodySize, textColor, typeface, spacing);
        rubyStyle = new TextStyle(rubySize, textColor, typeface, spacing);
        highlightStyle = new TextStyle(bodySize, highlightColor, typeface, spacing);
        reset(true);
    }

    /**
     * @param spacing 0.5 1.0 1.5
     */
    public void setSpacing(float spacing) {
        this.spacing = spacing;
        titleStyle.lineSpace = titleStyle.fontSpace * this.spacing;
        bodyStyle.lineSpace = bodyStyle.fontSpace * this.spacing;
        rubyStyle.lineSpace = rubyStyle.fontSpace * this.spacing;
        highlightStyle.lineSpace = highlightStyle.fontSpace * this.spacing;
        Log.e(TAG, "setSpacing spacing = " + this.spacing);
        reset(true);
    }

    /**
     * @param padding 0.05 0.1 0.2
     */
    public void setPadding(float padding) {
        if (width <= 0 || height <= 0) {
            width = Resources.getSystem().getDisplayMetrics().widthPixels;
            height = Resources.getSystem().getDisplayMetrics().heightPixels;
        }
        this.padding = padding * Math.min(width, height) * 0.1f;
        Log.e(TAG, "setPadding padding = " + this.padding + " width = " + width + " height = " + height);
        reset(true);
    }

    public void reset(boolean isReDraw) {
        if (text.isEmpty()) {
            return;
        }
        this.pageIndices[0] = 0;
        this.sentenceIndicies[0] = 0;
        this.currentIndex = 0;
        this.currentSentence = 0;
        this.totalPage = -1;
        if (isReDraw) this.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(width, height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
        Log.e(TAG, "setVertical isVertical = " + isVertical);
        this.reset(true);
    }

    boolean checkHalf(String s) {
        CharSetting setting = CharSetting.getSetting(s);
        if (setting == null) {
            return false;
        } else return s.getBytes().length < 2 && setting.angle == 90.0f;
    }

    public void drawChar(Canvas canvas, String s, PointF pos, TextStyle style, boolean drawEnable) {
        CharSetting setting = CharSetting.getSetting(s);
        float fontSpacing = style.fontSpace;
        float halfOffset = 0;
        if (isVertical && checkHalf(s)) {
            pos.y -= fontSpacing / 2;
        }
        if (isVertical) {
            if (setting == null && s.getBytes().length < 2) {
                halfOffset = 0.2f;
            }
        }
        if (drawEnable) {
            if (setting == null || !isVertical) {
                canvas.drawText(s, pos.x + fontSpacing * halfOffset, pos.y, style.paint);
            } else {
                canvas.save();
                canvas.rotate(setting.angle, pos.x, pos.y);
                canvas.drawText(s,
                        pos.x + fontSpacing * setting.x, pos.y + fontSpacing * setting.y,
                        style.paint);
                canvas.restore();
            }
        }
        if (!isVertical && checkHalf(s)) {
            pos.x -= fontSpacing / 2;
        }
    }

    public boolean drawString(Canvas canvas, String s, PointF pos, TextStyle style, boolean drawEnable) {
        for (int i = 0; i < s.length(); i++) {
            drawChar(canvas, s.charAt(i) + "", pos, style, drawEnable);
            if (!goNext(s, pos, style, true)) {
                return false;
            }
        }
        return true;
    }

    boolean goNextLine(PointF pos, TextStyle type, float spaceRate) {
        if (isVertical) {
            pos.x -= type.lineSpace * spaceRate;
            pos.y = padding + type.fontSpace;
            return pos.x > padding;
        } else {
            pos.y += type.lineSpace * spaceRate;
            pos.x = padding;
            return pos.y < height - padding;
        }
    }

    boolean goNext(String s, PointF pos, TextStyle type, boolean lineChangeable) {
        boolean newLine = false;
        if (isVertical) {
            if (pos.y + type.fontSpace > height - padding - type.fontSpace) {
                newLine = true;
            }
        } else {
            if (pos.x + type.fontSpace > width - padding - type.fontSpace) {
                newLine = true;
            }
        }

        if (newLine && lineChangeable) {
            return goNextLine(pos, type, lineSpaceRate);
        } else {
            float fontSpace = type.fontSpace;
//            if (checkHalf(s)) fontSpace /= 2; // if uncomment the romaji will be narrow
            if (isVertical) {
                pos.y += fontSpace;
            } else {
                pos.x += fontSpace;
            }
        }
        return true;
    }

    void initPos(PointF pos) {
        if (isVertical) {
            pos.x = width - padding - bodyStyle.lineSpace;
            pos.y = padding + bodyStyle.fontSpace;
        } else {
            pos.x = padding;
            pos.y = padding + bodyStyle.lineSpace;
        }
    }

    PointF getHeadPos(PointF pos, TextStyle style) {
        PointF res = new PointF();
        res.x = pos.x;
        res.y = pos.y;
        return res;
    }

    PointF getRubyPos(CurrentState state) {
        PointF res = new PointF();
        if (isVertical) {
            res.x = state.rubyStart.x + bodyStyle.fontSpace;
            res.y = state.rubyStart.y - rubyStyle.fontSpace;
            if (state.textPosition.y - state.rubyStart.y > 0) {
                res.y -= 0.5 * (state.rubyText.length() * rubyStyle.fontSpace - (state.textPosition.y - state.rubyStart.y));
            }
            if (res.y < padding) res.y = padding;
        } else {
            res.x = state.rubyStart.x;
            res.y = state.rubyStart.y - bodyStyle.fontSpace;
            if (state.textPosition.x - state.rubyStart.x > 0) {
                res.x -= 0.5 * (state.rubyText.length() * rubyStyle.fontSpace - (state.textPosition.x - state.rubyStart.x));
            }
        }
        return res;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textDraw(canvas, currentIndex, true);
        if (totalPage < 0) {
            calculatePages();
        }
    }

    public void calculatePages() {
        Log.e(TAG, "calculatePages");
        SWPageCalculator task = new SWPageCalculator();
        task.execute(10L, 20L, 30L);
    }

    public boolean drawPage(Canvas canvas, int page) {
        this.currentIndex = page;
        return textDraw(canvas, page, true);
    }

    public boolean textDraw(Canvas canvas, int pageIndex, boolean enable) {
        if (pageIndex < 0 || pageIndex >= pageIndices.length) {
            return true;
        }
        CurrentState state = new CurrentState();
        initPos(state.textPosition);
        initPos(state.rubyPosition);
        boolean endFlag = true;
        state.isDrawEnable = enable;
        int textIndex = pageIndices[pageIndex];
        for (; textIndex < text.length(); textIndex++) {
            state.textIndex = textIndex;
            state.lineChangeable = true;
            state.strPrev = state.strCurr;
            state.strCurr = text.charAt(textIndex) + "";
            state.strNext = (textIndex + 1 < text.length()) ? text.charAt(textIndex + 1) + "" : "";
            int lastSentenceTextIndex = sentenceIndicies[currentSentence];
            if (isUpdatingSentences && lastSentenceTextIndex < textIndex + 1 && (state.strCurr.equals(DOT_CHAR) || state.strCurr.equals(NEW_PARAGRAPH))) {
                currentSentence += 1;
                sentenceIndicies[currentSentence] = textIndex + 1;
                Log.e(TAG, String.format("currentSentence = %d textIndex = %d", currentSentence, textIndex + 1));
            }
            if (!charDrawProcess(canvas, state)) {
                endFlag = false;
                break;
            }
        }
        pageIndices[pageIndex + 1] = textIndex + 1;
        return endFlag;
    }

    private void drawHighlight(Canvas canvas, CurrentState state) {
        if (!state.strCurr.trim().isEmpty() && state.textIndex >= startSentenceIndex && state.textIndex < endSentenceIndex && highlightStyle != null && canvas != null) {
            Log.e(TAG, "+ " + state.strCurr + " - " + state.textPosition);
            float x = state.textPosition.x;
            float y = state.textPosition.y;
            float hScale = 1.0f;
            float yScale = 0.0f;
            if (isVertical) {
                y -= highlightStyle.fontSpace;
                hScale = 1.1f;
                yScale = -0.1f;
            } else {
                y -= highlightStyle.fontSpace;
                hScale = 1.3f;
            }
            canvas.drawRect(new RectF(x, y - yScale * highlightStyle.fontSpace, x + highlightStyle.fontSpace, y + highlightStyle.fontSpace * hScale), highlightStyle.paint);
        }
    }

    boolean charDrawProcess(Canvas canvas, CurrentState state) {
        drawHighlight(canvas, state);
        if (state.isRubyEnable) {
            if (state.isRubyBody && (state.bodyText.length() > 20 || state.strCurr.equals(NEW_LINE))) {
                drawString(canvas, state.bufferText + state.bodyText, state.textPosition, bodyStyle, state.isDrawEnable);
                state.bodyText = "";
                state.bufferText = "";
                state.isRubyBody = false;
            }
            if (state.strCurr.equals("|") || state.strCurr.equals("｜")) {
                if (state.bodyText.length() > 0) {
                    drawString(canvas, state.bufferText + state.bodyText, state.textPosition, bodyStyle, state.isDrawEnable);
                    state.bodyText = "";
                    state.bufferText = "";
                }
                state.bodyText = "";
                state.bufferText = state.strCurr;
                state.isRubyBody = true;
                state.rubyStart = getHeadPos(state.textPosition, bodyStyle);
                return true;
            }
            if (state.strCurr.equals("《") && (state.isRubyBody || state.isKanjiBlock)) {
                state.isRuby = true;
                state.isRubyBody = false;
                state.rubyText = "";
                return true;
            }
            if (state.strCurr.equals("》") && state.isRuby) {
                drawString(canvas, state.bodyText, state.textPosition, bodyStyle, state.isDrawEnable);
                state.rubyPosition = getRubyPos(state);
                drawString(canvas, state.rubyText, state.rubyPosition, rubyStyle, state.isDrawEnable);
                state.isRuby = false;
                state.bodyText = "";
                state.bufferText = "";
                return !state.isPageEnd;
            }
            boolean isKanji = (UnicodeBlock.of(state.strCurr.charAt(0)) == UnicodeBlock.CJK_COMPATIBILITY) || (UnicodeBlock.of(state.strCurr.charAt(0)) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || state.strCurr.equals("々") || state.strCurr.equals("〆") || state.strCurr.equals("仝") || state.strCurr.equals("〇") || state.strCurr.equals("ヶ");
            if (isKanji && !state.isKanjiBlock) {
                if (!state.isRubyBody) {
                    state.rubyStart = getHeadPos(state.textPosition, bodyStyle);
                }
            }
            state.isKanjiBlock = isKanji;

            if (state.isRuby) {
                state.rubyText += state.strCurr;
                return true;
            }
            if (state.isRubyBody) {
                state.bodyText += state.strCurr;
                return true;
            }
        }
        TextStyle style = state.isTitle ? titleStyle : bodyStyle;
        if (state.strCurr.equals(NEW_LINE)) {
            if (state.strPrev.equals(NEW_LINE)) {
                return this.goNextLine(state.textPosition, style, lineSpaceRate);
            } else {
                return this.goNextLine(state.textPosition, style, lineSpaceRate);
            }
        }
        this.drawChar(canvas, state.strCurr, state.textPosition, style, state.isDrawEnable);
        if (!this.goNext(state.strCurr, state.textPosition, style, checkLineChangeable(state))) {
            state.isPageEnd = true;
            return state.isRubyBody;
        }
        return true;
    }

    boolean checkLineChangeable(CurrentState state) {
        if (!state.lineChangeable) {
            state.lineChangeable = true;
        } else if (state.strNext.equals("。") || state.strNext.equals("、")
                || state.strNext.equals("」") || state.strNext.equals("』")
                || state.strNext.equals(")") || state.strNext.equals("）")
                || state.strNext.equals("]") || state.strNext.equals("］")
                || state.strNext.equals("}") || state.strNext.equals("｝")
                || state.strNext.equals("〉") || state.strNext.equals("】")
                || state.strNext.equals("〕")
                || state.strNext.equals("，") || state.strNext.equals("．")
                || state.strNext.equals(".") || state.strNext.equals(",")) {
            state.lineChangeable = false;
        }
        return state.lineChangeable;
    }

    public void setOnPageCalculatorListener(OnPageCalculatorListener onPageCalculatorListener) {
        this.onPageCalculatorListener = onPageCalculatorListener;
    }

    public interface OnPageCalculatorListener {
        void onStartedCalculatingPages(int textLength);

        void onFinishedCalculatingPages(int totalPage);
    }

    class SWPageCalculator extends AsyncTask<Long, Integer, Double> {

        @Override
        protected void onPreExecute() {
            int textLength = text.length();
            Log.e(TAG, "onPreExecute textLength = " + textLength);
            isUpdatingSentences = true;
            if (onPageCalculatorListener != null) {
                onPageCalculatorListener.onStartedCalculatingPages(textLength);
            }
        }

        @Override
        protected Double doInBackground(Long... params) {
            int current = 0;
            Log.e(TAG, "doInBackground current = " + current);
            while (!textDraw(null, current, false)) {
                current++;
            }
            Log.e(TAG, "doInBackground current = " + current);
            totalPage = current + 1;
            Log.e(TAG, "doInBackground totalPage = " + totalPage);
            Log.e(TAG, "isUpdatingSentences " + isUpdatingSentences + " totalSentence = " + totalSentence + " text length = " + text.length());
            totalSentence = currentSentence;
            if (totalSentence > 1) {
                for (int i = 0; i < totalSentence; i++) {
                    int start = sentenceIndicies[i];
                    int end = sentenceIndicies[i + 1];
                    String sentence = "";
                    if (start >= 0 && end >= start) {
                        sentence = text.substring(start, end);
                    }
                    Log.e(TAG, "sentence " + i + " " + start + " - " + end + " - " + sentence);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Double result) {
            Log.e(TAG, "onPostExecute totalPage = " + totalPage + " totalSentence = " + totalSentence);
            isUpdatingSentences = false;
            if (onPageCalculatorListener != null) {
                onPageCalculatorListener.onFinishedCalculatingPages(totalPage);
            }
        }
    }

}
