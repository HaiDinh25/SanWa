package com.sanwashoseki.bookskozuchi.business.shoppingcart.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.base.SWBaseFragment
import com.sanwashoseki.bookskozuchi.base.SWMainActivity
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookDetailFragment
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookStoreFragment
import com.sanwashoseki.bookskozuchi.business.shoppingcart.adapters.SWShoppingCartAdapter
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.requests.SWShoppingCartRequest
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWRemoveShoppingCartResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWRemoveShoppingCartInterface
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWRemoveShoppingCartPresenter
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWShoppingCartInterface
import com.sanwashoseki.bookskozuchi.business.shoppingcart.services.SWShoppingCartPresenter
import com.sanwashoseki.bookskozuchi.databinding.FragmentShoppingCartBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.SWRecyclerTouchListener
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import kotlinx.android.synthetic.main.fragment_shopping_cart.view.*
import kotlin.math.roundToInt

class SWShoppingCartFragment : SWBaseFragment(), View.OnClickListener,
    SWShoppingCartAdapter.OnItemClickListener, SWRemoveShoppingCartInterface,
    SWShoppingCartInterface, SWBaseFragment.OnConfirmListener {

    private var binding: FragmentShoppingCartBinding? = null
    private var adapter: SWShoppingCartAdapter? = null
    private var shoppingResponse: SWGetShoppingCartResponse.Data? = null
    private var bookOrders = ArrayList<SWShoppingCartRequest>()

    private var preRemove: SWRemoveShoppingCartPresenter? = null
    private var preGetCart: SWShoppingCartPresenter? = null
    private var idShoppingCartItem: Int? = null

    private var isRemoveAll: Boolean? = null

    private var skeletonScreen: ViewSkeletonScreen? = null

    companion object {
        @JvmStatic
        fun newInstance(result: SWGetShoppingCartResponse.Data?) =
            SWShoppingCartFragment().apply {
                arguments = Bundle().apply {
                    shoppingResponse = result
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_shopping_cart,
            container,
            false
        )

        initUI()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preGetCart?.getShoppingCart(context, true)
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        (activity as SWMainActivity).hideWishListFilter()
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.shopping_title), isSearch = false, isFilter = false)

        preGetCart?.validButton(bookOrders.size)

        binding?.let { it ->
            it.tvAmount.text = getString(R.string.currency) + " " + 0
            it.tvTotalItem.text = 0.toString() + getString(R.string.book_number)
            it.btnNext.setOnClickListener(this)
            it.btnEmpty.setOnClickListener(this)
            it.btnFindBook.setOnClickListener(this)
            val mTouchListener = SWRecyclerTouchListener(activity, it.rcCart)
            mTouchListener
                .setClickable(object : SWRecyclerTouchListener.OnRowClickListener {
                    override fun onRowClicked(position: Int) {}
                    override fun onIndependentViewClicked(independentViewID: Int, position: Int) {}
                })
                .setSwipeOptionViews(R.id.btnDelete)
                .setSwipeable(R.id.container, R.id.background) { viewID, position ->
                    when (viewID) {
                        R.id.btnDelete -> {
                            isRemoveAll = false
                            idShoppingCartItem = shoppingResponse?.items!![position].shoppingCartItemId
                            showDialogConfirm(getString(R.string.dialog_confirm_delete_item_shopping_cart), false)
                            onCallBackConfirm(this)
                        }
                    }
                }
            it.rcCart.addOnItemTouchListener(mTouchListener)

            it.checkAll.setOnClickListener {
                bookOrders.removeAll(bookOrders)
                if (it.checkAll.isChecked) {
                    for (i in shoppingResponse?.items!!.indices) {
                        if (shoppingResponse?.items!![i].published != false && shoppingResponse?.items!![i].deleted != true) {
                            val model = SWShoppingCartRequest(shoppingResponse?.items!![i])
                            bookOrders.add(model)
                        }
                    }
                }
                getShoppingCartBook()
                setTotal()
                preGetCart?.validButton(bookOrders.size)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setLayout() {
        binding?.let { it ->
            if (shoppingResponse?.items!!.isEmpty()) {
                it.layoutEmpty.visibility = View.VISIBLE
                it.layoutData.visibility = View.GONE
            } else {
                it.layoutEmpty.visibility = View.GONE
                it.layoutData.visibility = View.VISIBLE
            }
        }
    }

    private fun getShoppingCartBook() {
        adapter = SWShoppingCartAdapter(shoppingResponse, bookOrders, 1)
        binding?.let { it ->
            it.rcCart.layoutManager = LinearLayoutManager(context)
            it.rcCart.itemAnimator = DefaultItemAnimator()
            it.rcCart.adapter = adapter

            adapter?.onCallBackCheckBoxClicked(this)
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preRemove = SWRemoveShoppingCartPresenter()
        preGetCart = SWShoppingCartPresenter()
        preRemove?.attachView(this)
        preGetCart?.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preRemove?.detachView()
        preGetCart?.detachView()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnNext -> replaceFragment(
                SWShoppingCartConfirmFragment.newInstance(bookOrders),
                R.id.container,
                true,
                null
            )
            R.id.btnFindBook -> replaceFragment(
                SWBookStoreFragment.newInstance(),
                R.id.container,
                true,
                null
            )
            R.id.btnEmpty -> {
                isRemoveAll = true
                showDialogConfirm(getString(R.string.dialog_confirm_delete_all_shopping_cart), false)
                onCallBackConfirm(this)
            }
        }
    }

    override fun onCheckedListener(id: Int?) {
        for (i in shoppingResponse?.items!!.indices) {
            if (shoppingResponse?.items!![i].shoppingCartItemId == id) {
                val bookModel = SWShoppingCartRequest(shoppingResponse?.items!![i])
                if (!bookOrders.contains(bookModel)) {
                    bookOrders.add(bookModel)
                    break
                }
            }
        }
        binding?.checkAll?.isChecked = bookOrders.size == shoppingResponse!!.items?.size
        Log.d("TAG", "onCheckedListener: " + bookOrders.size)
        setTotal()
        preGetCart?.validButton(bookOrders.size)
    }

    override fun onUnCheckedListener(id: Int?) {
        for (i in 0 until bookOrders.size) {
            if (bookOrders[i].book.shoppingCartItemId == id) {
                bookOrders.removeAt(i)
                break
            }
        }
        binding?.checkAll?.isChecked = false
        setTotal()
        preGetCart?.validButton(bookOrders.size)
    }

    override fun onBookDetailListener(id: Int?) {
        replaceFragment(SWBookDetailFragment.newInstance(id, false), R.id.container, false, null)
    }

    override fun onSearchSimilarListener(id: Int?) {
        replaceFragment(SWSearchSimilarBookFragment.newInstance(id, null), R.id.container, true, null)
    }

    override fun onRemoveBookInShoppingCart(id: Int?) {
        preRemove?.removeItemShoppingCart(context, id)
    }

    @SuppressLint("SetTextI18n")
    fun setTotal() {
        binding?.let { it ->
            var amount = 0
            for (i in bookOrders.indices) {
                amount = (amount + bookOrders[i].book.price!!).roundToInt()
            }
            var countIsAvailable = 0
            for (i in shoppingResponse?.items!!.indices) {
                if (shoppingResponse?.items!![i].published != false && shoppingResponse?.items!![i].deleted != true) {
                    countIsAvailable++
                }
            }
            it.checkAll.isChecked = bookOrders.size == countIsAvailable
            it.tvAmount.text = getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(
                amount.toDouble()
            )
            it.tvTotalItem.text = bookOrders.size.toString() + getString(R.string.book_number)
        }
    }

    override fun removeItemSuccess(result: SWRemoveShoppingCartResponse?) {
        skeletonScreen?.hide()
        preGetCart?.getShoppingCart(context, true)
    }

    override fun removeAllSuccess(result: SWRemoveShoppingCartResponse?) {
//        bookOrders.clear()
        skeletonScreen?.hide()
        preGetCart?.getShoppingCart(context, true)
    }

    override fun getShoppingCardSuccess(result: SWGetShoppingCartResponse?) {
        bookOrders.clear()
        shoppingResponse = result?.data
        setLayout()
        for (i in shoppingResponse?.items!!.indices) {
            if (shoppingResponse?.items!![i].published != false && shoppingResponse?.items!![i].deleted != true) {
                val model = SWShoppingCartRequest(shoppingResponse?.items!![i])
                bookOrders.add(model)
            }
        }
        if (arguments?.getIntegerArrayList(Const.SHOPPING_CART_SELECTED_BOOK) != null) {
            bookOrders.removeAll(bookOrders)
            Log.d("TAG", "getShoppingCardSuccess: " + arguments?.getIntegerArrayList(Const.SHOPPING_CART_SELECTED_BOOK))
            for (i in shoppingResponse?.items!!.indices) {
                for (j in 0 until arguments?.getIntegerArrayList(Const.SHOPPING_CART_SELECTED_BOOK)!!.size) {
                    if (shoppingResponse?.items!![i].shoppingCartItemId == arguments?.getIntegerArrayList(Const.SHOPPING_CART_SELECTED_BOOK)!![j]) {
                        val model = SWShoppingCartRequest(shoppingResponse?.items!![i])
                        if (!bookOrders.contains(model)) {
                            bookOrders.add(model)
                        }
                    }
                }
            }
            preGetCart?.validButton(bookOrders.size)
        }

        setTotal()
        skeletonScreen?.hide()
        getShoppingCartBook()
    }

    override fun showMessageError(msg: String) {
        showLoading(false, getString(R.string.book_store_title), msg)
    }

    override fun validationButton(isValid: Boolean) {
        binding?.let { it ->
            if (isValid) {
                it.btnNext.setBackgroundResource(R.drawable.bg_button_authentication)
                it.btnNext.isEnabled = true
            } else {
                it.btnNext.setBackgroundResource(R.drawable.bg_button_inactive)
                it.btnNext.isEnabled = false
            }
        }
    }

    override fun showIndicator() {
         skeletonScreen = Skeleton.bind(binding?.rootView)
             .load(R.layout.skeleton_fragment_shopping_cart)
             .duration(1200)
             .color(R.color.colorSkeleton)
             .angle(0)
             .show()
//        showLoading(true, getString(R.string.shopping_remove_cart), getString(R.string.dialog_loading_content) )
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

    override fun onConfirmListener() {
        setLayout()
        if (isRemoveAll == true) {
            preRemove?.removeAllShoppingCart(context)
        } else {
            adapter?.notifyDataSetChanged()
            preRemove?.removeItemShoppingCart(context, idShoppingCartItem)
        }
    }

}