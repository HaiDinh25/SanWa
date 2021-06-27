package com.sanwashoseki.bookskozuchi.business.more.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.more.models.responses.SWAboutUsResponse
import com.sanwashoseki.bookskozuchi.business.more.services.SWAboutUsInterface
import com.sanwashoseki.bookskozuchi.business.more.services.SWAboutUsPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentAboutUsBinding
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import kotlinx.android.synthetic.main.fragment_book_request.*

class SWAboutUsFragment : SWBaseFragment(), SWAboutUsInterface, View.OnClickListener {

    private var binding: FragmentAboutUsBinding? = null
    private var presenter: SWAboutUsPresenter? = null

    private var skeletonScreen: ViewSkeletonScreen? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            SWAboutUsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about_us, container, false)

        initUI()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.getAboutUs(context)
    }

    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.more_about_us),
            isSearch = false,
            isFilter = false)

        binding?.let { it ->
            it.btnTerm.setOnClickListener(this)
            it.btnPrivacy.setOnClickListener(this)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWAboutUsPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun getAboutUsSuccess(result: SWAboutUsResponse) {
        Log.d("TAG", "getAboutUsSuccess: $result")
        binding?.let { it ->
            skeletonScreen?.hide()
            it.tvName.text = result.data?.versionApp?.name
            it.tvVersion.text = result.data?.versionApp?.version
            it.tvDescription.text = result.data?.versionApp?.description
            it.tvTerm.text = result.data?.termAnPrivacy!![0].body
            it.tvPrivacy.text = result.data?.termAnPrivacy!![1].body
        }
    }

    override fun showMessageError(msg: String) {
        //showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun showIndicator() {
        skeletonScreen = Skeleton.bind(binding?.rootView)
            .load(R.layout.skeleton_fragment_about_us)
            .duration(1200)
            .color(R.color.colorSkeleton)
            .angle(0)
            .show()
//        showLoading(true, getString(R.string.more_about_us), getString(R.string.dialog_loading_content))
    }

    override fun hideIndicator() {
        hideLoading()
    }

    override fun showNetworkError() {
        showLoading(
            false,
            getString(R.string.dialog_error_network_title),
            getString(R.string.dialog_error_network_content)
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnTerm -> {
                binding?.let { it ->
                    if (it.tvTerm.isShown) {
                        it.btnTerm.rotation = (0).toFloat()
                        it.tvTerm.visibility = View.GONE
                    } else {
                        it.btnTerm.rotation = (90).toFloat()
                        it.tvTerm.visibility = View.VISIBLE
                    }
                }
            }
            R.id.btnPrivacy -> {
                binding?.let { it ->
                    if (it.tvPrivacy.isShown) {
                        it.btnPrivacy.rotation = (0).toFloat()
                        it.tvPrivacy.visibility = View.GONE
                    } else {
                        it.btnPrivacy.rotation = (90).toFloat()
                        it.tvPrivacy.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

}