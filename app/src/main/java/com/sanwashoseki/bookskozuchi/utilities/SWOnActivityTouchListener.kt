package com.sanwashoseki.bookskozuchi.utilities

import android.view.MotionEvent

interface SWOnActivityTouchListener {
    fun getTouchCoordinates(ev: MotionEvent?)
}