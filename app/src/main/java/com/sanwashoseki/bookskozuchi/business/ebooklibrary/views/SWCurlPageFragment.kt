package com.sanwashoseki.bookskozuchi.business.ebooklibrary.views

/*
 * Created by Dinh.Van.Hai on 17/11/2020
 * Mobile 0931670595
 */

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentCurlPageBinding


class SWCurlPageFragment : SWBaseFragment() {

    private var binding: FragmentCurlPageBinding? = null
    private var images: List<Int>? = null

    companion object {
        fun newInstance(): SWCurlPageFragment {
            val fragment = SWCurlPageFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_curl_page, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        images = ArrayList()
        for (i in 0..10) {
            (images as ArrayList<Int>).add(R.drawable.image)
        }

        binding?.pageCurlView?.setCurlView(images)
        binding?.pageCurlView?.setCurlSpeed(150)

//        Log.d("TAG", "initUI: " + loadBitmapFromView(binding?.layout!!))
    }

    private fun loadBitmapFromView(v: View): Bitmap? {
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(spec, spec)
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        val b = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        c.translate((-v.scrollX).toFloat(), (-v.scrollY).toFloat())
        v.draw(c)
        return b
    }

    override fun onBackPressed(): Boolean {
        requireActivity().finish()
        return false
    }

}