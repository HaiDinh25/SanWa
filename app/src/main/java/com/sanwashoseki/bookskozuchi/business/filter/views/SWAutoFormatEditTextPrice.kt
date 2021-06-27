package com.sanwashoseki.bookskozuchi.business.filter.views

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper

/**
 * Some note <br></br>
 *  * Always use locale US instead of default to make DecimalFormat work well in all language
 */
class SWAutoFormatEditTextPrice : AppCompatEditText {
    private val currencyTextWatcher = CurrencyTextWatcher(this)

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {//        this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//        this.setHint(prefix);
//        this.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_LENGTH) });
    }

    protected override fun onFocusChanged(
        focused: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            this.addTextChangedListener(currencyTextWatcher)
        } else {
            this.removeTextChangedListener(currencyTextWatcher)
        }
    }

    private class CurrencyTextWatcher internal constructor(editText: EditText) : TextWatcher {
        private val editText: EditText = editText
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // do nothing
        }

        override fun afterTextChanged(editable: Editable) {
            editText.removeTextChangedListener(this)
            try {
                var originalString: String = editText.text.toString()
                originalString = originalString.replace("[($,. )]".toRegex(), "")
                val price = originalString.toDouble()
                val formattedString: String = SWUIHelper.formatTextPrice(price).toString()

                //setting text after format to EditText
                editText.setText(formattedString)
                editText.setSelection(editText.getText().length)
            } catch (nfe: NumberFormatException) {
                nfe.printStackTrace()
            }
            editText.addTextChangedListener(this)
        }

    }

    companion object {
        private const val MAX_LENGTH = 15
        private const val MAX_DECIMAL = 3
    }
}