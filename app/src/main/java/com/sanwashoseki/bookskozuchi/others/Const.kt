package com.sanwashoseki.bookskozuchi.others

import com.sanwashoseki.bookskozuchi.BuildConfig
import com.sanwashoseki.bookskozuchi.utilities.SWApplication
import com.sanwashoseki.bookskozuchi.utilities.Sharepref

object Const {
    const val BASE_URL = BuildConfig.HOST_API_URL
    const val URL_PRIVACY = BuildConfig.TERMS_URL

    val BEARER: String
        get() = "Bearer ${Sharepref.getUserToken(SWApplication.context)}"

    const val LOGIN_FACEBOOK = "LOGIN_FACEBOOK"
    const val LOGIN_GOOGLE = "LOGIN_GOOGLE"
    const val LOGIN_EMAIL = "LOGIN_EMAIL"

    const val PUBLISHER_ID = "PUBLISHER_ID"
    const val PUBLISHER_NAME = "PUBLISHER_NAME"
    const val CATEGORY_ID = "CATEGORY_ID"
    const val SHOPPING_CART_SELECTED_BOOK = "SHOPPING_CART_SELECTED_BOOK"

    const val LINE_CHANGE_ID = "1655102903"
    const val CLIENT_ID = "5b762631-67fe-4980-930a-a16524c425b4"
    const val CLIENT_SECRET = "4G2Hbd34"

    const val FILTER_HIGH_LIGHT = "FILTER_HIGH_LIGHT"
    const val FILTER_TOP_SELLER = "FILTER_TOP_SELLER"
    const val FILTER_LATEST = "FILTER_LATEST"
    const val FILTER_LIBRARY = "FILTER_LIBRARY"
    const val FILTER_CATEGORY = "FILTER_CATEGORY"
    const val FILTER_SEARCH_AUTHOR_NAME = "FILTER_SEARCH_AUTHOR_NAME"
    const val FILTER_SIMILAR = "FILTER_SIMILAR"
    const val FILTER_SAME_CATEGORY = "FILTER_SAME_CATEGORY"
    const val FILTER_SAME_PUBLISHER = "FILTER_SAME_PUBLISHER"

    const val FOLDER_SANWA = "Sanwa"
    const val FOLDER_SAMPLE = "Sample"
    const val FOLDER_CACHE = "Cache"

    const val FILE_PRIVATE_KEY = "PRIVATEKEY.HPK"
    const val FILE_PUBLIC_KEY = "PUBLICKEY.HPK"

    const val FILE_BOOK_DETAIL = "BOOKDETAIL.HSD"
    const val FILE_SIMILAR = "SIMILAR.HSS"

    const val FILE_DATA = "BOOKDATA.HSB"
    const val FILE_SAMPLE = "SAMPLE.HSS"
    const val FILE_LOG = "LOG.HSL"
    const val FILE_BOOK_PDF = "FILE_BOOK.pdf"
    const val FILE_DICTIONARY = "DICTIONARY.HSD"

    const val FILE_CATEGORY = "CATEGORY.HSC"
    const val FILE_HIGH_LIGHT = "HIGHLIGHT.HSH"
    const val FILE_TOP_SELLER = "TOPSELLER.HST"
    const val FILE_LATEST = "LATEST.HSL"
    const val FILE_MY_BOOK = "MYBOOK.HSM"
    const val FILE_WISH_LIST = "WISHLIST.HSW"
    const val FILE_READING_NOW = "READING.HSR"
    const val FILE_REQUEST = "REQUEST.HSR"

    const val FILE_SETTING_VOICE = "SETTINGVOICE.HSS"
    const val FILE_SETTING_FONT = "TEXTFONT.HSS"
    const val FILE_SETTING_COLOR = "TEXTCOLOR.HSS"
    const val FILE_CURRENT_PAGE = "CURRENT.HSC"

}