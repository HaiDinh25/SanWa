package com.sanwashoseki.bookskozuchi.utilities

import android.text.Editable

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)