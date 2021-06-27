package com.sanwashoseki.bookskozuchi.business.ebookstore.views

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWLoginActivity
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWCategoriesBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWHighlightBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWLatestBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.adapters.SWTopSellerBooksAdapter
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookInfoResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWCategoriesResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.services.*
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWNotificationResponse
import com.sanwashoseki.bookskozuchi.business.notifications.services.SWNotificationsInterface
import com.sanwashoseki.bookskozuchi.business.notifications.services.SWNotificationsPresenter
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWShoppingCartInterface
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWShoppingCartPresenter
import com.sanwashoseki.bookskozuchi.business.shoppingcart.views.SWShoppingCartFragment
import com.sanwashoseki.bookskozuchi.databinding.FragmentBookStoreBinding
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.gson.Gson
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWSetStatusNotification
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWUnreadNotificationResponse
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.SWApplication
import com.sanwashoseki.bookskozuchi.utilities.SWBookCacheManager
import java.io.File

class SWBookStoreFragment : SWBaseFragment(), SWHighlightBooksAdapter.OnItemClickListener,
    View.OnClickListener, SWLatestBooksAdapter.OnItemClickListener,
    SWTopSellerBooksAdapter.OnItemClickListener,
    SWCategoriesBooksAdapter.OnCategoriesItemClickedListener, SWBookStoreCategoriesInterface,
    SWBookStoreHighLightInterface, SWBookStoreTopSellerInterface, SWBookStoreLatestInterface,
    SWShoppingCartInterface, SWMainActivity.OnHeaderClickedListener, SWNotificationsInterface,
    SWBaseFragment.OnConfirmListener {

    private val TAG = "SWBookStoreFragment"
    private var binding: FragmentBookStoreBinding? = null

    private var categoriesAdapter = SWCategoriesBooksAdapter(null)
    private var highlightAdapter = SWHighlightBooksAdapter(1, null)
    private var latestAdapter = SWLatestBooksAdapter(1, null)
    private var topSellerAdapter = SWTopSellerBooksAdapter(1, null)

    private var preCategories: SWBookStoreCategoriesPresenter? = null
    private var preHighLight: SWBookStoreHighLightPresenter? = null
    private var preTopSeller: SWBookStoreTopSellerPresenter? = null
    private var preLatest: SWBookStoreLatestPresenter? = null
    private var preGetShoppingCart: SWShoppingCartPresenter? = null
    private var preNotifications: SWNotificationsPresenter? = null

    private var shoppingCart: SWGetShoppingCartResponse.Data? = null

    private var skeletonScreen: SkeletonScreen? = null
    private var isSwipe = false
    private var itemGetSuccess = 0
    private val API_REQUEST_NUMBER = 4

    companion object {
        fun newInstance(): SWBookStoreFragment {
            val fragment = SWBookStoreFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: Book Store")
        preCategories = SWBookStoreCategoriesPresenter()
        preHighLight = SWBookStoreHighLightPresenter()
        preTopSeller = SWBookStoreTopSellerPresenter()
        preLatest = SWBookStoreLatestPresenter()
        preGetShoppingCart = SWShoppingCartPresenter()
        preNotifications = SWNotificationsPresenter()

        preCategories?.attachView(this)
        preHighLight?.attachView(this)
        preTopSeller?.attachView(this)
        preLatest?.attachView(this)
        preGetShoppingCart?.attachView(this)
        preNotifications?.attachView(this)

        initData()
    }

    private fun initData() {
        preCategories?.getCategories(context, false)
        preHighLight?.getBookStoreHighLight(context, 10,false)
        preTopSeller?.getBookStoreTopSeller(context, 10,false)
        preLatest?.getBookStoreLatest(context, 10,false)
        if (Sharepref.getUserToken(context) != "") {
            preGetShoppingCart?.getShoppingCart(context, false)
            preNotifications?.getUnreadNotifications(context, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        preCategories?.detachView()
        preHighLight?.detachView()
        preTopSeller?.detachView()
        preLatest?.detachView()
        preGetShoppingCart?.detachView()
        preNotifications?.detachView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_store, container, false)

        initUI()
        return binding?.root
    }

    private fun initUI() {
        if (itemGetSuccess < 4) showIndicator()
        (activity as SWMainActivity).hideBottomNavigationMenu(false)
        (activity as SWMainActivity).setHeaderInMainMenu(getString(R.string.navigation_book_store), hideButton = false)

        if (Sharepref.getUserToken(context) == "") {
            (activity as SWMainActivity).setShoppingCart(0, false)
        }
        (activity as SWMainActivity).onCallBackSearchListener(this)

        binding?.let { it ->
            it.swipeRefresh.setOnRefreshListener {
                isSwipe = true
                preCategories?.getCategories(context, true)
                preLatest?.getBookStoreLatest(context, 10,false)
                preTopSeller?.getBookStoreTopSeller(context, 10,false)
                preHighLight?.getBookStoreHighLight(context, 10,false)
                if (Sharepref.getUserToken(context) != "") {
                    preGetShoppingCart?.getShoppingCart(context, false)
                    preNotifications?.getUnreadNotifications(context, false)
                }
            }
            it.btnMoreHighlight.setOnClickListener(this)
            it.btnMoreSellers.setOnClickListener(this)
            it.btnMoreLatest.setOnClickListener(this)
        }
        getRecycleCategories()
        getRecycleHighlight()
        getRecycleSellers()
        getRecycleLatest()
    }

    private fun getRecycleCategories() {
        val manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.let { it ->
            it.rcCategories.layoutManager = manager
            it.rcCategories.itemAnimator = DefaultItemAnimator()
            it.rcCategories.adapter = categoriesAdapter
        }
        categoriesAdapter?.onCallBackCategoriesClicked(this)
    }

    private fun getRecycleHighlight() {
        val manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.let { it ->
            it.rcHighlight.layoutManager = manager
            it.rcHighlight.itemAnimator = DefaultItemAnimator()
            it.rcHighlight.adapter = highlightAdapter
        }
        highlightAdapter?.setOnCallBackListener(this)
    }

    private fun getRecycleSellers() {
        binding?.let { it ->
            it.rcSellers.layoutManager = GridLayoutManager(context, 2)
            it.rcSellers.itemAnimator = DefaultItemAnimator()
            it.rcSellers.adapter = topSellerAdapter
        }
        topSellerAdapter?.setOnCallBackListener(this)
    }

    private fun getRecycleLatest() {
        val manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding?.let { it ->
            it.rcLatest.layoutManager = manager
            it.rcLatest.itemAnimator = DefaultItemAnimator()
            it.rcLatest.adapter = latestAdapter
        }
        latestAdapter?.setOnCallBackListener(this)
    }

    override fun onBackPressed(): Boolean {
        requireActivity().finish()
        return false
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnMoreHighlight -> replaceFragment(SWHighlightBookFragment.newInstance(false, null), R.id.container, false, null)
            R.id.btnMoreSellers -> replaceFragment(SWTopSellersFragment.newInstance(false, null), R.id.container, false, null)
            R.id.btnMoreLatest -> replaceFragment(SWLatestBookFragment.newInstance(false, null), R.id.container, false, null)
        }
    }

    override fun onCategoriesClickedListener(id: Int?) {
        Log.d(TAG, "onCategoriesClickedListener: $id")
        replaceFragment(SWCategoriesBookFragment.newInstance(id), R.id.container, false, null)
    }

    override fun getCategoriesSuccess(result: SWCategoriesResponse) {
        Log.d(TAG, "getCategoriesSuccess: ")
        (activity as SWMainActivity).setHeaderInMainMenu(getString(R.string.navigation_book_store), hideButton = false)
        (activity as SWMainActivity).setShoppingCart(0, false)
        itemGetSuccess++
        if (itemGetSuccess == API_REQUEST_NUMBER) hideIndicator()
        categoriesAdapter?.setData(result.data)
        SWBookCacheManager.cacheBookHome(result.toString(), Const.FOLDER_CACHE, Const.FILE_CATEGORY)
    }

    override fun getHighLightSuccess(result: SWBookStoreResponse) {
        Log.d(TAG, "getHighLightSuccess: ")
        itemGetSuccess++
        if (itemGetSuccess == API_REQUEST_NUMBER) hideIndicator()
        result.data?.let { highlightAdapter?.setData(it) }
        SWBookCacheManager.cacheBookHome(result.toString(), Const.FOLDER_CACHE, Const.FILE_HIGH_LIGHT)
    }

    override fun onHighlightItemClicked(model: SWBookInfoResponse?, read: Boolean) {
        replaceFragment(SWBookDetailFragment.newInstance(model?.id, false), R.id.container, false, null)
    }

    override fun getTopSellerSuccess(result: SWBookStoreResponse) {
        Log.d(TAG, "getTopSellerSuccess: ")
        itemGetSuccess++
        if (itemGetSuccess == API_REQUEST_NUMBER) hideIndicator()
        topSellerAdapter?.setData(result.data)
        SWBookCacheManager.cacheBookHome(result.toString(), Const.FOLDER_CACHE, Const.FILE_TOP_SELLER)
    }

    override fun onTopSellerItemClicked(model: SWBookInfoResponse?) {
        replaceFragment(SWBookDetailFragment.newInstance(model?.id, false), R.id.container, false, null)
    }

    override fun getLatestSuccess(result: SWBookStoreResponse) {
        Log.d(TAG, "getLatestSuccess: ")
        itemGetSuccess++
        if (itemGetSuccess == API_REQUEST_NUMBER) {
            hideIndicator()
        }
        result.data?.let { latestAdapter?.setData(it) }
        SWBookCacheManager.cacheBookHome(result.toString(), Const.FOLDER_CACHE, Const.FILE_LATEST)
    }

    override fun onLatestItemClicked(model: SWBookInfoResponse?) {
        replaceFragment(SWBookDetailFragment.newInstance(model?.id, false), R.id.container, false, null)
    }

    override fun getShoppingCardSuccess(result: SWGetShoppingCartResponse?) {
        Log.d(TAG, "getShoppingCardSuccess: ")
        Handler(Looper.getMainLooper()).postDelayed({
            (activity as SWMainActivity).setShoppingCart(result?.data?.items?.size, true)
            shoppingCart = result?.data
        }, 0)
    }

    override fun getUnreadNotificationsSuccess(result: SWUnreadNotificationResponse?) {
        Log.d(TAG, "getUnreadNotificationsSuccess: ")
        (activity as SWMainActivity).setNotification(result?.data)
    }

    override fun getNotificationsSuccess(result: SWNotificationResponse?) {}

    override fun setStatusSuccess(result: SWSetStatusNotification?) {

    }

    override fun showMessageError(msg: String) {
        //hideIndicator()
    }

    override fun onLoadMoreSuccess(result: SWBookStoreResponse) {
    }

    override fun expiredToken() {
        showDialogConfirm(getString(R.string.dialog_expired_token), true)
        onCallBackConfirm(this)
        Sharepref.removeUserInfo(context)
    }

    override fun validationButton(isValid: Boolean) {

    }

    override fun showIndicator() {
        Log.d(TAG, "showIndicator: ")
        if (!isSwipe) {
            skeletonScreen = Skeleton.bind(binding?.rootView)
            .load(R.layout.skeleton_fragment_book_store)
            .duration(1200)
            .color(R.color.colorSkeleton)
            .angle(0)
            .show()
            showLoading(true, getString(R.string.book_store_title), getString(R.string.dialog_loading_content))
        }
    }

    override fun hideIndicator() {
        hideLoading()
        binding?.swipeRefresh?.isRefreshing = false
        skeletonScreen?.hide()
        Log.d(TAG, "hideIndicator: ")
    }

    override fun showNetworkError() {
//        showLoading(
//            false,
//            getString(R.string.dialog_error_network_title),
//            getString(R.string.dialog_error_network_content)
//        )
        val fCategory = File(SWApplication.cacheDir, "${Const.FOLDER_SANWA}/${Const.FOLDER_CACHE}/${Const.FILE_CATEGORY}")
        val fHighLight = File(SWApplication.cacheDir, "${Const.FOLDER_SANWA}/${Const.FOLDER_CACHE}/${Const.FILE_HIGH_LIGHT}")
        val fTopSeller = File(SWApplication.cacheDir, "${Const.FOLDER_SANWA}/${Const.FOLDER_CACHE}/${Const.FILE_TOP_SELLER}")
        val fLatest = File(SWApplication.cacheDir, "${Const.FOLDER_SANWA}/${Const.FOLDER_CACHE}/${Const.FILE_LATEST}")

        if (fCategory.exists()) {
            val categoryModel: SWCategoriesResponse = Gson().fromJson(SWBookCacheManager.readFileCache(fCategory), SWCategoriesResponse::class.java)
            categoriesAdapter = SWCategoriesBooksAdapter(categoryModel.data)
            getRecycleCategories()
        }
        if (fHighLight.exists()) {
            val highLightModel: SWBookStoreResponse = Gson().fromJson(SWBookCacheManager.readFileCache(fHighLight), SWBookStoreResponse::class.java)
            highlightAdapter = SWHighlightBooksAdapter(1, highLightModel.data)
            getRecycleHighlight()
        }
        if (fTopSeller.exists()) {
            val topSellerModel: SWBookStoreResponse = Gson().fromJson(SWBookCacheManager.readFileCache(fTopSeller), SWBookStoreResponse::class.java)
            topSellerAdapter = SWTopSellerBooksAdapter(1, topSellerModel.data)
            getRecycleSellers()
        }
        if (fCategory.exists()) {
            val latestModel: SWBookStoreResponse = Gson().fromJson(SWBookCacheManager.readFileCache(fLatest), SWBookStoreResponse::class.java)
            latestAdapter = SWLatestBooksAdapter(1, latestModel.data)
            getRecycleLatest()
        }
        binding?.swipeRefresh?.isEnabled = false
        skeletonScreen?.hide()
    }

    override fun onSearchListener(content: String) {}

    override fun onShoppingCartClicked() {
        isSwipe = false
        replaceFragment(SWShoppingCartFragment.newInstance(shoppingCart), R.id.container, false, null)
    }

    override fun onWishListClicked(isWishList: Boolean) {}

    override fun onFilterClicked() {

    }

    override fun onConfirmListener() {
        (activity as SWMainActivity).replaceActivity(SWLoginActivity::class.java)
    }

}