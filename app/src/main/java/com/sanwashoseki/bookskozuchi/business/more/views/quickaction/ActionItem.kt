package com.sanwashoseki.bookskozuchi.business.more.views.quickaction

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.annotation.DrawableRes

/**
 * Action item, displayed as menu with icon and text.
 */
class ActionItem(actionId: Int, title: String, @DrawableRes icon: Int) {
    /**
     * Get action title
     *
     * @return action title
     */
    /**
     * Set action title
     *
     * @param title action title
     */
    var title: String
    private var icon = -1
    private var iconDrawable: Drawable? = null

    /**
     * @return Our action id
     */
    var actionId = -1
    private var selected = false
    /**
     * @return true if button is sticky, menu stays visible after press
     */
    /**
     * Set sticky status of button
     *
     * @param sticky true for sticky, pop up sends event but does not disappear
     */
    var isSticky = false

    /**
     * @return true if title have been set
     */
    fun haveTitle(): Boolean {
        return !TextUtils.isEmpty(title)
    }

    fun haveIcon(): Boolean {
        return icon > 0 || iconDrawable != null
    }

    /**
     * Set selected flag;
     *
     * @param selected Flag to indicate the item is selected
     */
    fun setSelected(selected: Boolean) {
        this.selected = selected
    }

    fun getIconDrawable(context: Context): Drawable? {
        if (iconDrawable == null) iconDrawable = context.resources.getDrawable(icon)
        return iconDrawable
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ActionItem
        return actionId == that.actionId
    }

    override fun hashCode(): Int {
        return actionId
    }

    /**
     * Create Action Item with all attribute
     *
     * @param actionId Action id for case statements
     * @param title    Title
     * @param icon     Icon to use
     */
    init {
        this.actionId = actionId
        this.title = title
        this.icon = icon
    }
}