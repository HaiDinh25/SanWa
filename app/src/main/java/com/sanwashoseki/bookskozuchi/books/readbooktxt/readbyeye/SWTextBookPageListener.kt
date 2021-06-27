package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye

interface SWTextBookPageListener {

    fun onTextBookLoaded(totalPage: Int)
    fun onTextBookChanged(pageIndex: Int)
    fun onTextBookFinished(pageIndex: Int)

}