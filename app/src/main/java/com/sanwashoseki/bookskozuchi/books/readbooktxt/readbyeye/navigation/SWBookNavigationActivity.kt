package com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.navigation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.fragments.SWBookmarksFragment
import com.sanwashoseki.bookskozuchi.books.fragments.SWContentsFragment
import com.sanwashoseki.bookskozuchi.books.models.SWBookmarksResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_saved.*
import kotlinx.android.synthetic.main.toolbar_center.*

class SWBookNavigationActivity : AppCompatActivity() {

    class SWBookMarkEvent(val book: SWBookmarksResponse.Data) { /* Additional fields if needed */ }

    private lateinit var adapter: ViewPagerAdapter
    private var contents: ArrayList<SWBookmarksResponse.Data> = arrayListOf()
    private var productId: Int? = null
    private var isBookmarks: Boolean? = null

    companion object {

        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, SWBookNavigationActivity::class.java))
        }

        fun start(
            activity: Activity,
            chapters: ArrayList<SWBookmarksResponse.Data>,
            productId: Int?,
            isBookmarks: Boolean?,
        ) {
            val intent = Intent(activity, SWBookNavigationActivity::class.java)
            val json = Gson().toJson(chapters)
            intent.putExtra(SWConstants.INTENT_CHAPTER, json)
            intent.putExtra(SWConstants.INTENT_PRODUCT_ID, productId)
            intent.putExtra(SWConstants.INTENT_BOOK_MARK, isBookmarks)
            activity.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved)
        tvTitle.text = getString(R.string.content_content)
        intent?.extras?.getString(SWConstants.INTENT_CHAPTER, "[]")?.let {
            val objectType = object : TypeToken<ArrayList<SWBookmarksResponse.Data>>() {}.type
            val array = Gson().fromJson<ArrayList<SWBookmarksResponse.Data>>(it, objectType)
            contents = array
        }
        intent?.extras?.getInt(SWConstants.INTENT_PRODUCT_ID)?.let { idBook->
            productId = idBook
        }
        intent?.extras?.getBoolean(SWConstants.INTENT_BOOK_MARK)?.let { bookmark ->
            isBookmarks = bookmark
        }
        setupViewpager()
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun listChapter(): ArrayList<SWBookmarksResponse.Data> {
        return contents
    }

    private fun setupViewpager() {
        try {
            adapter = ViewPagerAdapter(supportFragmentManager)
            adapter.addFragmentWithTitle(SWContentsFragment.newInstance(listChapter()),
                getString(R.string.content_content))
            adapter.addFragmentWithTitle(SWBookmarksFragment.newInstance(productId),
                getString(R.string.content_bookmark))

            viewPagerSaved.adapter = adapter
            tabs.setupWithViewPager(viewPagerSaved)
            if (isBookmarks == true) {
                viewPagerSaved.currentItem = 1
            }
        } catch (e: Exception) {

        }
    }

}