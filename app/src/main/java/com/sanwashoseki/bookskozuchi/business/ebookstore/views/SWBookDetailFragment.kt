package com.sanwashoseki.bookskozuchi.business.ebookstore.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.books.readbookpdf.SWPDFBookActivity
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWConstants
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.SWTextBookReaderActivity
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWDrmLicenseResponse
import com.sanwashoseki.bookskozuchi.business.ebookreader.services.SWDownloadBookInterface
import com.sanwashoseki.bookskozuchi.business.ebookreader.services.SWDownloadBookPresenter
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWViewPageAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.requests.SWAddShoppingCartWishListRequest
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWBookDetailResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWAddShoppingCartWishListInterface
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWAddShoppingCartWishListPresenter
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWBookDetailInterface
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.SWBookDetailPresenter
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWRemoveShoppingCartResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWShoppingCartInterface
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWShoppingCartPresenter
import com.sanwashoseki.bookskozuchi.business.shoppingcart.views.SWShoppingCartFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentBookDetailBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.others.SWDialogAuthors
import com.sanwashoseki.bookskozuchi.utilities.*
import kotlinx.android.synthetic.main.book_detail_layout.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class SWBookDetailFragment : SWBaseFragment(), SWMainActivity.OnHeaderClickedListener,
    SWAddShoppingCartWishListInterface, View.OnClickListener, SWBookDetailInterface,
    SWShoppingCartInterface, SWDownloadBookInterface {

    private var binding: FragmentBookDetailBinding? = null
    private var idBook: Int? = null
    private var idShoppingCartItem: Int? = null
    private var isWishList = false
    private var isAddCart = true
    private var isFirstLoad = true
    private var linkPurchase: String? = null
    private var isReview: Boolean? = null
    private var isTextFile: Boolean? = null
    private var updateTime: String? = null
    private var authors: List<SWBookDetailResponse.Data.Authors>? = null
    private var book: SWBookDetailResponse.Data? = null

    private var preBookDetail: SWBookDetailPresenter? = null
    private var preAddWishList: SWAddShoppingCartWishListPresenter? = null
    private var preGetShoppingCart: SWShoppingCartPresenter? = null
    private var preDownloadBook: SWDownloadBookPresenter? = null
    private var adapterTabLayout: SWViewPageAdapter? = null

    private var skeletonScreen: ViewSkeletonScreen? = null
    private var dowloadSuccess = false
    private var isTablet = false
    private var shoppingCart: SWGetShoppingCartResponse.Data? = null

    companion object {
        @JvmStatic
        fun newInstance(id: Int?, review: Boolean?) = SWBookDetailFragment().apply {
            arguments = Bundle().apply {
                idBook = id
                isReview = review
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_detail, container, false)

        initUI()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preBookDetail?.getBookDetail(context, idBook)
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        if (context?.let { SWUIHelper.isTablet(it) }!!) {
            isTablet = true
        }
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.book_detail_title),
            isSearch = false,
            isFilter = false)
        (activity as SWMainActivity).setWishListButton(false, isWishList)
        (activity as SWMainActivity).onCallBackSearchListener(this)
        binding?.let {
            it.btnGetItNow?.setOnClickListener(this)
            it.btnPurchase?.setOnClickListener(this)
            it.btnReadBook?.setOnClickListener(this)
            it.btnSample?.setOnClickListener(this)
            it.addToFavourites?.setOnClickListener(this) //for tablet
        }
        if (isTablet) {
            (activity as SWMainActivity).hideWishListFilter()
            setFavouritesLayout(false, isWishList)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preAddWishList = SWAddShoppingCartWishListPresenter()
        preBookDetail = SWBookDetailPresenter()
        preGetShoppingCart = SWShoppingCartPresenter()
        preDownloadBook = SWDownloadBookPresenter()
        preAddWishList?.attachView(this)
        preBookDetail?.attachView(this)
        preGetShoppingCart?.attachView(this)
        preDownloadBook?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preAddWishList?.detachView()
        preBookDetail?.detachView()
        preGetShoppingCart?.detachView()
        preDownloadBook?.detachView()
    }

    override fun onBackPressed(): Boolean {
        val file = File(SWApplication.cacheDir,
            "${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(context)}/${idBook}/${Const.FILE_BOOK_PDF}")
        if (file.exists()) {
            file.delete()
        }
        (activity as SWMainActivity).setWishListButton(true, isWishList)
        setFavouritesLayout(true, isWishList)
        return false
    }

    fun setFavouritesLayout(hideWishList: Boolean, isWishList: Boolean) {
        if (!isTablet) return
        if (hideWishList) {
            binding?.addToFavourites?.visibility = View.GONE
        } else {
            binding?.addToFavourites?.visibility = View.VISIBLE
            if (isWishList) {
                binding?.imgFavourites?.setImageDrawable(context?.getDrawable(R.drawable.ic_wish_list))
                binding?.addToFavouritesText?.text = context?.getString(R.string.delete_from_favourites)
            } else {
                binding?.imgFavourites?.setImageDrawable(context?.getDrawable(R.drawable.ic_un_wish_list))
                binding?.addToFavouritesText?.text = context?.getString(R.string.add_to_favourites)
            }
        }
    }

    override fun onSearchListener(content: String) {}

    override fun onShoppingCartClicked() {}

    override fun onWishListClicked(isWishList: Boolean) {
        if (Sharepref.getUserToken(context) != "") {
            isAddCart = false
            if (isWishList) {
                preAddWishList?.unWishList(context, idShoppingCartItem)
            } else {
                val model = SWAddShoppingCartWishListRequest(idBook, 2)
                preAddWishList?.addShoppingCartWishList(context, model)
            }
        } else {
            (activity as SWMainActivity).showSignIn()
        }
    }

    private fun addOrRemoveFavourites() {
        if (Sharepref.getUserToken(context) != "") {
            isAddCart = false
            if (isWishList) {
                preAddWishList?.unWishList(context, idShoppingCartItem)
            } else {
                val model = SWAddShoppingCartWishListRequest(idBook, 2)
                preAddWishList?.addShoppingCartWishList(context, model)
            }
        } else {
            (activity as SWMainActivity).showSignIn()
        }
    }

    override fun onFilterClicked() {}

    override fun addShoppingCartWishListSuccess(result: SWAddShoppingCartWishListResponse) {
        if (!isAddCart) {
            isWishList = !isWishList
            Log.d("TAG", "onWishListClicked: $isWishList")
            (activity as SWMainActivity).setWishListButton(false, isWishList)
            setFavouritesLayout(false, isWishList)
        } else {
            replaceFragment(SWShoppingCartFragment.newInstance(shoppingCart),
                R.id.container,
                false,
                null)
//            showLoading(false, getString(R.string.dialog_confirm), getString(R.string.dialog_add_shopping_cart_success))
        }
    }

    override fun unWishListSuccess(result: SWRemoveShoppingCartResponse) {
        //preBookDetail?.getBookDetail(context, idBook)
        (activity as SWMainActivity).setWishListButton(hideWishList = false, isWishList = false)
        setFavouritesLayout(false, false)
    }

    @SuppressLint("SetTextI18n")
    override fun getBookDetailSuccess(result: SWBookDetailResponse) {
        binding?.let { it ->
            isFirstLoad = false
            isTextFile = result.data?.contentType.equals(SWConstants.IS_TEXT_FILE)
            updateTime = result.data?.updatedOnUtc
            SWBookCacheManager.writeKey(result.toString(), Const.FILE_BOOK_DETAIL, idBook)

            linkPurchase = result.data?.offlineSellingLink
            if (linkPurchase == null || linkPurchase == "") {
                it.btnPurchase?.visibility = View.GONE
            }
            setDataInLayout(result)

            it.tvAuthor.setOnClickListener(this)
            it.btnReadBook.setOnClickListener(this)
        }
    }

    override fun getShoppingCardSuccess(result: SWGetShoppingCartResponse?) {
        shoppingCart = result?.data
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.dialog_title_failed), msg)
    }

    override fun validationButton(isValid: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getDrmLicenseSuccess(result: SWDrmLicenseResponse) {
        SWBookCacheManager.writeKey(result.data?.decryptionKey.toString(),
            Const.FILE_PRIVATE_KEY,
            idBook)
        SWBookCacheManager.writeKey(result.data?.encryptionKey.toString(),
            Const.FILE_PUBLIC_KEY,
            idBook)
        Log.d("TAG", "getDrmLicenseSuccess: $updateTime")
        SWBookCacheManager.writeKey(updateTime, Const.FILE_LOG, idBook)
        if (isTextFile == true) {
            SWBookDecryption.bookDecryptionTXT(idBook)
        } else {
            SWBookDecryption.bookDecryptionPDF(idBook)
        }
    }

    override fun downloadBookSuccess(result: Response<ResponseBody>) {
        SWBookCacheManager.writeBookToDisk(result.body(), idBook)
        if (isTextFile == true) {
            SWTextBookReaderActivity.start(requireActivity(), book)
        } else {
            SWPDFBookActivity.start(requireActivity(), book)
        }
        hideIndicator()
        hideLoading()
        dowloadSuccess = true
    }

    override fun downloadSampleBookSuccess(result: Response<ResponseBody>) {
        SWBookCacheManager.writeSampleToDisk(result.body(), idBook)
        hideLoading()
        if (isTextFile == true) {
            SWTextBookReaderActivity.start(requireActivity(), book)
        } else {
            SWPDFBookActivity.start(requireActivity(), book)
        }
        hideIndicator()
        hideLoading()
        dowloadSuccess = true
    }

    override fun showDrmMessageError(msg: String) {
        showLoading(false, "Drm License", msg)
    }

    override fun showDownloadMessageError(msg: String) {
        showLoading(false, "Download Book", msg)
    }

    override fun showDownloadSampleMessageError(msg: String) {
        showLoading(false, "Download Sample", msg)
    }

    override fun showIndicator() {
        if (isFirstLoad) {
            skeletonScreen = Skeleton.bind(binding?.rootView)
                .load(R.layout.skeleton_fragment_book_detail)
                .duration(1200)
                .color(R.color.colorSkeleton)
                .angle(0)
                .show()
        } else {
            if (!isAddCart) {
                showLoading()
            } else {
                showLoading()
            }
        }
    }

    override fun hideIndicator() {
        hideLoading()
        if (dowloadSuccess) {
            dowloadSuccess = false
        }
    }

    override fun showNetworkError() {
        if (SWBookCacheManager.checkExistFile(idBook, Const.FILE_DATA)) {
            val detail: SWBookDetailResponse =
                Gson().fromJson(SWBookCacheManager.readFileCache(SWBookCacheManager.fileConfigText(
                    idBook,
                    Const.FILE_BOOK_DETAIL)),
                    SWBookDetailResponse::class.java)
            isTextFile = detail.data?.contentType.equals(SWConstants.IS_TEXT_FILE)
            updateTime = detail.data?.updatedOnUtc
            setDataInLayout(detail)
        }
    }

    private fun fileBookDetail(): File {
        return File(SWApplication.cacheDir,
            "${Const.FOLDER_SANWA}/${Sharepref.getUserEmail(context)}/${idBook}/${Const.FILE_BOOK_DETAIL}")
    }

    fun checkFileBookDetail(): Boolean {
        return fileBookDetail().exists()
    }

    @SuppressLint("SetTextI18n")
    private fun setDataInLayout(detail: SWBookDetailResponse) {
        skeletonScreen?.hide()
        book = detail.data
        binding?.let { it ->
            if (detail.data?.isPurchased == true) {
                it.btnReadBook.visibility = View.VISIBLE
                it.btnSample.visibility = View.GONE
                it.btnGetItNow?.visibility = View.GONE
            }
            idShoppingCartItem = detail.data?.shoppingCartId
            it.tvName.text = detail.data?.name

            val rating =
                detail.data?.productReviewOverview?.ratingSum!! / detail.data?.productReviewOverview?.totalReviews!!
            if (detail.data?.productReviewOverview?.ratingSum!! < 1) {
                it.ratingBar.visibility = View.GONE
            }
            it.ratingBar.rating = rating
            var author = ""
            for (i in detail.data?.authors!!.indices) {
                if (i == 0) {
                    author = detail.data?.authors!![i].name.toString()
                } else {
                    if (author != "") {
                        author = author + "、 " + detail.data?.authors!![i].name.toString()
                    }
                }
            }
            it.tvAuthor.text = author
            if (detail.data?.shortDescription == "") {
                it.tvShortDescription.visibility = View.GONE
            } else {
                it.tvShortDescription.text = detail.data?.shortDescription
            }
            it.tvPrice.text =
                getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(detail.data?.productPrice?.priceValue)

            if (detail.data?.isWishList == true) {
                (activity as SWMainActivity).setWishListButton(hideWishList = false,
                    isWishList = true)
                setFavouritesLayout(false, true)
            }

            if (detail.data?.isPurchased == true) {
                (activity as SWMainActivity).hideWishListFilter()
                binding?.addToFavourites?.visibility = View.GONE //tablet
            }

            if(context?.let { SWUIHelper.isTablet(it) }!!) {
                setBookInformation(detail)
                parentFragmentManager?.beginTransaction()
                    ?.replace(R.id.bookDetailContentViewContainer, SWOverViewBookFragment.newInstance(detail))
                    ?.commitNow()
            } else {
                adapterTabLayout = SWViewPageAdapter(requireContext(), childFragmentManager, detail)
                it.viewPage?.adapter = adapterTabLayout
                it.tabLayout?.setupWithViewPager(it.viewPage)

                if (isReview == true) {
                    it.viewPage?.postDelayed({ it.viewPage?.currentItem = 1 }, 100)
                }

                adapterTabLayout?.notifyDataSetChanged()
            }

            Glide.with(this)
                .load(detail.data?.defaultPictureModel?.imageUrl)
                .into(it.imageView)
        }
    }

    private fun setBookInformation(detail: SWBookDetailResponse) {
        binding?.review?.text = String.format(resources.getString(R.string.review_count), detail.data?.productReviewOverview?.totalReviews)
        val bookDetails = detail.data?.productInfoBooks
        bookTypeKey.text = bookDetails?.get(0)?.key
        bookTypeValue.text = bookDetails?.get(0)?.value
        bookLanguageKey.text = bookDetails?.get(1)?.key
        bookLanguageValue.text = bookDetails?.get(1)?.value
        bookPublicationKey.text = bookDetails?.get(2)?.key
        bookPublicationValue.text = bookDetails?.get(2)?.value
        bookPageKey.text = bookDetails?.get(3)?.key
        bookPageValue.text = bookDetails?.get(3)?.value
        bookSizeKey.text = bookDetails?.get(5)?.key
        bookSizeValue.text = bookDetails?.get(5)?.value
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnGetItNow -> {
                if (NetworkUtil.isNetworkConnected(context)) {
                    if (Sharepref.getUserToken(context) != "") {
                        isAddCart = true
                        val model = SWAddShoppingCartWishListRequest(idBook, 1)
                        preAddWishList?.addShoppingCartWishList(context, model)
                    } else {
                        (activity as SWMainActivity).showSignIn()
                    }
                } else {
                    showLoading(
                        false,
                        getString(R.string.dialog_error_network_title),
                        getString(R.string.dialog_error_network_content)
                    )
                }
            }
            R.id.btnPurchase -> {
                if (NetworkUtil.isNetworkConnected(context)) {
                    if (linkPurchase != null) {
                        if (linkPurchase != "") {
                            val i = Intent(Intent.ACTION_VIEW, Uri.parse(linkPurchase))
                            startActivity(i)
                        }
                    }
                } else {
                    showLoading(
                        false,
                        getString(R.string.dialog_error_network_title),
                        getString(R.string.dialog_error_network_content)
                    )
                }
            }
            R.id.tvAuthor -> {
                val dialog = SWDialogAuthors(requireContext(), authors)
                dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                dialog.show()
            }
            R.id.btnReadBook -> {
                if (NetworkUtil.isNetworkConnected(context)) {
                    Log.d("TAG", "onClick: $updateTime")
                    if (SWBookCacheManager.checkUpdateTime(idBook, updateTime)) {
                        if (SWBookCacheManager.checkExistFile(idBook, Const.FILE_DATA)) {
                            if (SWBookCacheManager.checkExistFile(idBook, Const.FILE_BOOK_DETAIL)) {
                                val detail: SWBookDetailResponse = Gson().fromJson(
                                    SWBookCacheManager.readFileCache(SWBookCacheManager.fileConfigText(
                                        idBook,
                                        Const.FILE_BOOK_DETAIL)),
                                    SWBookDetailResponse::class.java)
                                book = detail.data
                            }
                            if (isTextFile == true) {
                                SWTextBookReaderActivity.start(requireActivity(), book)
                            } else {
                                SWBookDecryption.bookDecryptionPDF(idBook)
                                SWPDFBookActivity.start(requireActivity(), book)
                            }
                        } else {
                            showLoading()
                            preDownloadBook?.downloadBook(context, idBook)
                        }
                    } else {
                        showLoading()
                        preDownloadBook?.getDrmLicense(context, idBook)
                        preDownloadBook?.downloadBook(context, idBook)
                    }
                } else {
                    if (SWBookCacheManager.checkExistFile(idBook, Const.FILE_DATA)) {
                        val detail: SWBookDetailResponse =
                            Gson().fromJson(SWBookCacheManager.readFileCache(SWBookCacheManager.fileConfigText(
                                idBook,
                                Const.FILE_BOOK_DETAIL)),
                                SWBookDetailResponse::class.java)
                        book = detail.data
                        if (isTextFile == true) {
                            SWTextBookReaderActivity.start(requireActivity(), book)
                        } else {
                            SWBookDecryption.bookDecryptionPDF(idBook)
                            SWPDFBookActivity.start(requireActivity(), book)
                        }
                    }
                }
                Log.d("TAG", "onClick: " + SWBookCacheManager.progressDownload)
                //TODO: next fragment & call api updateCurrentReadingBook from SWUpdateCurrentReadingBookPresenter
            }
            R.id.btnSample -> {
                if (SWBookCacheManager.checkFileSample(idBook)) {
                    if (isTextFile == true) {
                        SWTextBookReaderActivity.start(requireActivity(), book)
                    } else {
                        SWPDFBookActivity.start(requireActivity(), book)
                    }
                } else {
                    preDownloadBook?.downloadSampleBook(context, idBook)
                }
            }
            R.id.addToFavourites -> {
                addOrRemoveFavourites()
            }
        }
    }
}