package com.sanwashoseki.bookskozuchi.utilities

interface MVP<IView> {
    fun attachView(view: IView)

    fun detachView()
}