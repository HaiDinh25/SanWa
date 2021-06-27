package com.sanwashoseki.bookskozuchi.books.readbooktxt.models

import android.graphics.Color
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWTextSettingModel : Entity(), Serializable {
    var fontSetting: FontSetting? = null
    var colorSetting: ColorSetting? = null

    class FontSetting : Entity(), Serializable {
        var fontName = "HiraginoSansGBW3.ttf"
        var fontSize = SWConstants.FONT_SIZE_22
        var spacing = SWConstants.SPACING_MEDIUM
        var padding = SWConstants.PADDING_MEDIUM
        var isVertical = false
    }

    class ColorSetting : Entity(), Serializable {
        var backgroundColor = Color.WHITE
        var textColor = Color.BLACK
        var highlightColor = Color.BLACK
    }

}