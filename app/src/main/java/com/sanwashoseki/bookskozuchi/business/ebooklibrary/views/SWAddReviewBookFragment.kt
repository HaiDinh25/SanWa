package com.sanwashoseki.bookskozuchi.business.ebooklibrary.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.models.requests.SWAddReviewRequest
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.services.SWAddReviewInterface
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.services.SWAddReviewPresenter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookDetailFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentReviewBookBinding
import com.sanwashoseki.bookskozuchi.others.SWDialogThankYou
import com.bumptech.glide.Glide

class SWAddReviewBookFragment : SWBaseFragment(), View.OnClickListener, SWAddReviewInterface {

    private var binding: FragmentReviewBookBinding? = null
    private var presenter: SWAddReviewPresenter? = null
    private var book: SWBookDetailResponse? = null

    private var isPurchase: Boolean? = null

    companion object {
        @JvmStatic
        fun newInstance(mode: SWBookDetailResponse?, purchase: Boolean?) = SWAddReviewBookFragment().apply {
            arguments = Bundle().apply {
                book = mode
                isPurchase = purchase
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review_book, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.review_book_title), isSearch = false, isFilter = false)

        binding?.let { it ->
            it.btnSendReview.setOnClickListener(this)
            it.container.setOnClickListener(this)

            it.edtTitle.text = book?.data?.name
            var author = ""
            for (i in book?.data?.authors!!.indices) {
                if (i == 0) {
                    author = book?.data?.authors!![i].name.toString()
                } else {
                    if (author != "") {
                        author = author + "、 " + book?.data?.authors!![i].name.toString()
                    }
                }
            }
            it.edtAuthor.text = author
            it.edtUser.text = book?.data?.vendorModel?.name
            Glide.with(requireContext())
                .load(book?.data?.defaultPictureModel?.imageUrl)
                .into(it.imageView)

            it.edtReview.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                @SuppressLint("SetTextI18n")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding?.tvCount?.text = s?.length.toString() + "/500"
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = SWAddReviewPresenter()
        presenter?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSendReview -> {
                val rating = binding?.ratingBar?.rating?.toInt()
                val textReview = binding?.edtReview?.text.toString()

                if (rating == 0) {
                    showLoading(false, getString(R.string.review_book_title),  getString(R.string.review_book_rating_null))
                } else {
                    val request = SWAddReviewRequest(book?.data?.id, rating, textReview)
                    presenter?.sendReview(context, request)
                }
            }
            R.id.container -> hideSoftKeyBoard(context, binding?.container!!)
        }
    }

    override fun sendReviewSuccess(result: SWAddShoppingCartWishListResponse) {
        hideLoading()
        val dialog = SWDialogThankYou(context, getString(R.string.dialog_add_review_thanks), getString(R.string.dialog_add_review_content), "Ok")
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.show()
        dialog.onCallbackClicked(object : SWDialogThankYou.OnOKClickedListener {
            override fun onOKListener() {
                if (isPurchase == false) {
                    replaceFragment(SWBookDetailFragment.newInstance(book?.data?.id, true), R.id.container, true, null)
                } else {
                    (activity as SWMainActivity).onBackPressed()
                }
            }
        })
    }

    override fun showMessageError(msg: String) {
        hideLoading()
        showLoading(false, getString(R.string.review_book_title), msg)
    }

    override fun showIndicator() {
        showLoading()
//        showLoading(true, "Review", getString(R.string.dialog_loading_content))
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

}