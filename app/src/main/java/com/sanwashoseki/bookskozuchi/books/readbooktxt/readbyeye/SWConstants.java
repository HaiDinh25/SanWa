package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SWConstants {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE.NAME, TYPE.AUTHOR, TYPE.CHAP, TYPE.CONTENT})
    public @interface TYPE {
        int NAME = 1;
        int AUTHOR = 2;
        int CHAP = 3;
        int CONTENT = 4;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({COLOR.BACKGROUND, COLOR.TEXT, COLOR.HIGHLIGHT})
    public @interface COLOR {
        int BACKGROUND = 0;
        int TEXT = 1;
        int HIGHLIGHT = 2;
    }

//    @Retention(RetentionPolicy.SOURCE)
//    @IntDef({TypeList.VIEW, TypeList.LOADING})
//    public @interface TypeList {
//        int VIEW = 0;
//        int LOADING = 1;
//    }

    public static final Float SPACING_SMALL = 1.5f;
    public static final Float SPACING_MEDIUM = 1.75f;
    public static final Float SPACING_LAGER = 2.0f;

    public static final Float PADDING_SMALL = 0.5f;
    public static final Float PADDING_MEDIUM = 0.75f;
    public static final Float PADDING_LAGER = 1.0f;

    public static final Integer FONT_SIZE_14 = 14;
    public static final Integer FONT_SIZE_16 = 16;
    public static final Integer FONT_SIZE_18 = 18;
    public static final Integer FONT_SIZE_20 = 20;
    public static final Integer FONT_SIZE_22 = 22;
    public static final Integer FONT_SIZE_24 = 24;
    public static final Integer FONT_SIZE_26 = 26;
    public static final Integer FONT_SIZE_28 = 28;
    public static final Integer FONT_SIZE_30 = 30;
    public static final Integer FONT_SIZE_32 = 32;

    public static final Integer PROGRESS_TEXT_SIZE_1 = 1;
    public static final Integer PROGRESS_TEXT_SIZE_2 = 2;
    public static final Integer PROGRESS_TEXT_SIZE_3 = 3;
    public static final Integer PROGRESS_TEXT_SIZE_4 = 4;
    public static final Integer PROGRESS_TEXT_SIZE_5 = 5;
    public static final Integer PROGRESS_TEXT_SIZE_6 = 6;
    public static final Integer PROGRESS_TEXT_SIZE_7 = 7;
    public static final Integer PROGRESS_TEXT_SIZE_8 = 8;
    public static final Integer PROGRESS_TEXT_SIZE_9 = 9;
    public static final Integer PROGRESS_TEXT_SIZE_10 = 10;

    public static final String BACKGROUND_COLOR_LIGHT = "#FFFFFF";
    public static final String BACKGROUND_COLOR_CLASSIC = "#FFF5EB";
    public static final String BACKGROUND_COLOR_DARK = "#303030";
    public static final String BACKGROUND_COLOR_CUSTOM = "#F5F5F5";

    public static final String TEXT_COLOR_LIGHT = "#2F2F2F";
    public static final String TEXT_COLOR_CLASSIC = "#5F0E07";
    public static final String TEXT_COLOR_DARK = "#FFFFFF";
    public static final String TEXT_COLOR_CUSTOM = "#212121";

    public static final String HIGHLIGHT_COLOR_LIGHT = "#8ae58a";
    public static final String HIGHLIGHT_COLOR_CLASSIC = "#d197db";
    public static final String HIGHLIGHT_COLOR_DARK = "#5E5E5E";
    public static final String HIGHLIGHT_COLOR_CUSTOM = "#FFFFFF";

    public static final String NAME_REQUEST_TOPPIC = "BookRequestTopic";
    public static final String NAME_ORDER = "Order";
    public static final String NAME_PRODUCT_REVIEW = "ProductReview";
    public static final String NAME_PRODUCT = "Product";

    public static final int LIMIT = Integer.MAX_VALUE;
    public static final String IS_TEXT_FILE = "text/plain";

    public static final String CHAP_REGEX = "［＃７.*］.*［＃.*］";
    public static final String FURIGANA_REGEX = "｜.*《.*》";
    public static final String PACKAGE_TEXT_TO_SPEECH = "com.google.android.tts";
    public static final String FORMAT_REGEX = "-*\n.*\n-*";
    public static final String CODE_LINE = "!@#$%^&*";

    public static final String INTENT_CHAPTER = "intent_chapter";
    public static final String INTENT_PRODUCT_ID = "intent_product_id";
    public static final String INTENT_BOOK_MARK = "intent_book_mark";

    public static final int MAX_VALUE = 20;
    public static final int INDEX_TITLE = 0;
    public static final int INDEX_SUBTITLE = 1;
    public static final String STYLE_PREFIX = "--------";
    public static final String STYLE_SUFFIX = "--------";
    public static final String SOURCE_NEW_LINE = "\r\n";
    public static final String TARGET_NEW_LINE = "\n";
    public static final String PREFIX_NEW_LINE = "　";
    public static final String[] ENDING_PREFIXES = new String[] {"翻訳の底本：", "底本：", "底本:"};

}
