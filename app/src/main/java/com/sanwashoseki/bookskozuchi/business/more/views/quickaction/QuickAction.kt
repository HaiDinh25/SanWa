package com.sanwashoseki.bookskozuchi.business.more.views.quickaction

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.DisplayMetrics
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.*
import com.sanwashoseki.bookskozuchi.R
import java.util.*

/**
 * QuickAction popup, shows action list as icon and text in Tooltip
 * popup. Currently supports vertical and horizontal layout.
 */
class QuickAction(context: Context, private val orientation: Int) : PopupWindows(context),
    PopupWindow.OnDismissListener {
    private val shadowSize: Int
    private val shadowColor: Int
    private val windowManager: WindowManager
    private var rootView: View? = null
    private var arrowUp: View? = null
    private var arrowDown: View? = null
    private val inflater: LayoutInflater
    private val resource: Resources
    private var track: LinearLayout? = null
    private var scroller: ViewGroup? = null
    private var mItemClickListener: OnActionItemClickListener? = null
    private val dismissListener: OnDismissListener? = null
    private val actionItems: MutableList<ActionItem> = ArrayList()
    private val animation = Animation.AUTO
    private var didAction = false
    private var rootWidth = 0
    private val dividerColor = defaultDividerColor
    private var textColor = defaultTextColor
    private fun setRootView(@LayoutRes id: Int) {
        rootView = inflater.inflate(id, null)
        rootView?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        track = rootView?.findViewById(R.id.tracks)
        track?.orientation = orientation
        arrowDown = rootView?.findViewById(R.id.arrow_down)
        arrowUp = rootView?.findViewById(R.id.arrow_up)
        scroller = rootView?.findViewById(R.id.scroller)
        setContentView(rootView)
        setColor(defaultColor)
    }

    /**
     * Set color of QuickAction
     *
     * @param popupColor Color to fill QuickAction
     * @see Color
     */
    fun setColor(@ColorInt popupColor: Int) {
        val drawable = GradientDrawable()
        drawable.setColor(popupColor)
        drawable.setStroke(shadowSize, shadowColor)
        drawable.cornerRadius = resource.getDimension(R.dimen.quick_action_corner)
        scroller!!.background = drawable
    }

    /**
     * Set color of QuickAction by color define in xml resource
     *
     * @param popupColor Color resource id to fill QuickAction
     */
    fun setColorRes(@ColorRes popupColor: Int) {
        setColor(resource.getColor(popupColor))
    }

    /**
     * Set color for text of each action item. MUST call this before add [ActionItem],
     * sorry I'm just too lazy.
     *
     * @param textColorRes Color resource id to use
     */
    fun setTextColorRes(@ColorRes textColorRes: Int) {
        setTextColor(resource.getColor(textColorRes))
    }

    /**
     * Set color for text of each action item. MUST call this before add [ActionItem], sorry
     * I'm just too lazy.
     *
     * @param textColor Color to use
     */
    fun setTextColor(@ColorInt textColor: Int) {
        this.textColor = textColor
    }

    fun setOnActionItemClickListener(listener: OnActionItemClickListener?) {
        mItemClickListener = listener
    }

    /**
     * Add action item
     *
     * @param action [ActionItem]
     */
    fun addActionItem(action: ActionItem) {
        val position = actionItems.size
        actionItems.add(action)
        addActionView(position, createViewFrom(action))
    }

    private fun addActionView(position: Int, actionView: View) {
        var position = position
        if (position != 0) {
            position *= 2
            val separatorPos = position - 1
            val separator = View(context)
            separator.setBackgroundColor(dividerColor)
            val width = resource.getDimensionPixelOffset(R.dimen.quick_action_separator_width)
            var layoutParams: ViewGroup.LayoutParams? = null
            when (orientation) {
                VERTICAL -> layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width)
                HORIZONTAL -> layoutParams =
                    ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            track!!.addView(separator, separatorPos, layoutParams)
        }
        track!!.addView(actionView, position)
    }

    private fun createViewFrom(action: ActionItem): View {
        val actionView: View
        if (action.haveTitle()) {
            val textView = inflater.inflate(R.layout.quick_action_item, track, false) as TextView
            textView.setTextColor(textColor)
            textView.text = String.format(" %s ", action.title)
            if (action.haveIcon()) {
                val iconSize = resource.getDimensionPixelOffset(R.dimen.quick_action_icon_size)
                val icon = action.getIconDrawable(context)
                icon!!.setBounds(0, 0, iconSize, iconSize)
                if (orientation == HORIZONTAL) {
                    textView.setCompoundDrawables(null, icon, null, null)
                } else {
                    textView.setCompoundDrawables(icon, null, null, null)
                }
            }
            actionView = textView
        } else {
            val imageView =
                inflater.inflate(R.layout.quick_action_image_item, track, false) as ImageView
            imageView.id = action.actionId
            imageView.setImageDrawable(action.getIconDrawable(context))
            actionView = imageView
        }
        actionView.id = action.actionId
        actionView.setOnClickListener { v: View? ->
            action.setSelected(true)
            if (mItemClickListener != null) {
                mItemClickListener!!.onItemClick(action)
            }
            if (!action.isSticky) {
                didAction = true
                dismiss()
            }
        }
        actionView.isFocusable = true
        actionView.isClickable = true
        return actionView
    }

    /**
     * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
     *
     * @param activity contain view to be anchor
     * @param anchorId id of view to use as anchor of QuickAction's popup
     */
    fun show(activity: Activity, @IdRes anchorId: Int) {
        show(activity.findViewById(anchorId))
    }

    /**
     * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
     *
     * @param anchor view to use as anchor of QuickAction's popup
     */
    fun show(anchor: View) {
        checkNotNull(context) { "Why context is null? It shouldn't be." }
        preShow()
        var xPos: Int
        val yPos: Int
        val arrowPos: Int
        didAction = false
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        val anchorRect = Rect(location[0], location[1], location[0] + anchor.width,
            location[1] + anchor.height)
        rootView?.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val rootHeight = rootView!!.measuredHeight
        if (rootWidth == 0) {
            rootWidth = rootView!!.measuredWidth
        }
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val screenWidth = displaymetrics.widthPixels
        val screenHeight = displaymetrics.heightPixels

        // automatically get X coord of quick_action_vertical (top left)
        if (anchorRect.left + rootWidth > screenWidth) {
            xPos = anchorRect.left - (rootWidth - anchor.width)
            xPos = Math.max(xPos, 0)
        } else {
            xPos = if (anchor.width > rootWidth) {
                anchorRect.centerX() - rootWidth / 2
            } else {
                anchorRect.left
            }
        }
        arrowPos = anchorRect.centerX() - xPos
        val dyTop = anchorRect.top
        val dyBottom = screenHeight - anchorRect.bottom
        val onTop = dyTop > dyBottom
        if (onTop) {
            if (rootHeight > dyTop) {
                yPos = 15
                val l = scroller!!.layoutParams
                l.height = dyTop - anchor.height
            } else {
                yPos = anchorRect.top - rootHeight
            }
        } else {
            yPos = anchorRect.bottom
            if (rootHeight > dyBottom) {
                val l = scroller!!.layoutParams
                l.height = dyBottom
            }
        }
        showArrow(if (onTop) R.id.arrow_down else R.id.arrow_up, arrowPos)
        setAnimationStyle(screenWidth, anchorRect.centerX(), onTop)
        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos)
    }

    /**
     * Show arrow
     *
     * @param whichArrow arrow type resource id
     * @param requestedX distance from left screen
     */
    private fun showArrow(@IdRes whichArrow: Int, requestedX: Int) {
        val showArrow = if (whichArrow == R.id.arrow_up) arrowUp else arrowDown
        val hideArrow = if (whichArrow == R.id.arrow_up) arrowDown else arrowUp
        val arrowWidth = arrowUp!!.measuredWidth
        showArrow!!.visibility = View.VISIBLE
        val param = showArrow.layoutParams as MarginLayoutParams
        param.leftMargin = requestedX - arrowWidth / 2
        hideArrow!!.visibility = View.GONE
    }

    /**
     * Set animation style
     *
     * @param screenWidth screen width
     * @param requestedX  distance from left edge
     * @param onTop       flag to indicate where the popup should be displayed. Set TRUE if displayed on top
     * of anchor view and vice versa
     */
    private fun setAnimationStyle(screenWidth: Int, requestedX: Int, onTop: Boolean) {
        val arrowPos = requestedX - arrowUp!!.measuredWidth / 2
        when (animation) {
            Animation.AUTO -> if (arrowPos <= screenWidth / 4) mWindow.animationStyle =
                Animation.GROW_FROM_LEFT[onTop] else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) mWindow.animationStyle =
                Animation.GROW_FROM_CENTER[onTop] else mWindow.animationStyle =
                Animation.GROW_FROM_RIGHT[onTop]
            else -> mWindow.animationStyle = animation[onTop]
        }
    }

    override fun onDismiss() {
        if (!didAction && dismissListener != null) {
            dismissListener.onDismiss()
        }
    }

    enum class Animation {
        GROW_FROM_LEFT {
            override operator fun get(onTop: Boolean): Int {
                return if (onTop) R.style.Animation_PopUpMenu_Left else R.style.Animation_PopDownMenu_Left
            }
        },
        GROW_FROM_RIGHT {
            override operator fun get(onTop: Boolean): Int {
                return if (onTop) R.style.Animation_PopUpMenu_Right else R.style.Animation_PopDownMenu_Right
            }
        },
        GROW_FROM_CENTER {
            override operator fun get(onTop: Boolean): Int {
                return if (onTop) R.style.Animation_PopUpMenu_Center else R.style.Animation_PopDownMenu_Center
            }
        },
        REFLECT {
            override operator fun get(onTop: Boolean): Int {
                return if (onTop) R.style.Animation_PopUpMenu_Reflect else R.style.Animation_PopDownMenu_Reflect
            }
        },
        AUTO {
            override operator fun get(onTop: Boolean): Int {
                throw UnsupportedOperationException("Can't use this")
            }
        };

        @StyleRes
        abstract operator fun get(onTop: Boolean): Int
    }

    /**
     * Listener for item click
     */
    interface OnActionItemClickListener {
        fun onItemClick(item: ActionItem?)
    }

    /**
     * Listener for window dismiss
     */
    interface OnDismissListener {
        fun onDismiss()
    }

    companion object {
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL
        private var defaultColor = Color.WHITE
        private var defaultTextColor = Color.BLACK
        private val defaultDividerColor = Color.argb(32, 0, 0, 0)
        fun setDefaultTextColor(defaultTextColor: Int) {
            Companion.defaultTextColor = defaultTextColor
        }

        fun setDefaultColor(defaultColor: Int) {
            Companion.defaultColor = defaultColor
        }
    }

    /**
     * Constructor allowing orientation override QuickAction.HORIZONTAL or QuickAction.VERTICAL
     *
     * @param context     Context
     * @param orientation Layout orientation, can be vartical or horizontal
     */
    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        resource = context.resources
        shadowSize = resource.getDimensionPixelSize(R.dimen.quick_action_shadow_size)
        shadowColor = resource.getColor(R.color.colorSkeleton)
        setRootView(R.layout.quick_action_vertical)
    }
}