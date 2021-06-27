package com.sanwashoseki.bookskozuchi.business.shoppingcart.views

import android.annotation.SuppressLint
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
import com.sanwashoseki.bookskozuchi.business.shoppingcart.adapters.SWShoppingCartAdapter
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.requests.SWShoppingCartRequest
import com.sanwashoseki.bookskozuchi.databinding.FragmentShoppingCartBinding
import com.sanwashoseki.bookskozuchi.others.Const
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import kotlin.math.roundToInt

class SWShoppingCartConfirmFragment : SWBaseFragment(), View.OnClickListener {

    private var binding: FragmentShoppingCartBinding? = null
    private var adapter: SWShoppingCartAdapter? = null
    private var books: ArrayList<SWShoppingCartRequest>? = null

    companion object {
        @JvmStatic
        fun newInstance(listBook: ArrayList<SWShoppingCartRequest>?) =
            SWShoppingCartConfirmFragment().apply {
                arguments = Bundle().apply {
                    books = listBook
                    Log.d("TAG", "getShoppingCardSuccess: $books")
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_shopping_cart, container, false)

        initUI()
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        (activity as SWMainActivity).hideBottomNavigationMenu(true)
        (activity as SWMainActivity).setHeaderInChildrenScreen(getString(R.string.shopping_title), isSearch = false, isFilter = false)

        adapter = SWShoppingCartAdapter(null, books, 2)
        getShoppingCartBook()

        binding?.let { it ->
            it.layoutCheckAll.visibility = View.GONE
            var amount = 0
            for (i in 0 until books?.size!!) {
                amount = (amount + books!![i].book.price!!).roundToInt()
            }
            it.tvAmount.text = getString(R.string.currency) + " " + SWUIHelper.formatTextPrice(amount.toDouble())
            it.tvTotalItem.text = books?.size.toString() + getString(R.string.book_number)

            it.btnNext.setBackgroundResource(R.drawable.bg_button_authentication)
            it.btnNext.isEnabled = true

            it.btnNext.setOnClickListener(this)
        }
    }

    private fun getShoppingCartBook() {
        binding?.let { it ->
            it.rcCart.layoutManager = LinearLayoutManager(context)
            it.rcCart.itemAnimator = DefaultItemAnimator()
            it.rcCart.adapter = adapter
        }
    }

    override fun onBackPressed(): Boolean {
        val frm = SWShoppingCartFragment()
        val bundle = Bundle()
        val bookIds = ArrayList<Int?>()
        for (i in 0 until books!!.size) {
            bookIds.add(books!![i].book.shoppingCartItemId)
        }
        bundle.putIntegerArrayList(Const.SHOPPING_CART_SELECTED_BOOK, bookIds)
        frm.arguments = bundle

        replaceFragment(frm, R.id.container, true, null)

        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnNext -> replaceFragment(
                SWMakeThePaymentFragment.newInstance(books),
                R.id.container,
                false,
                null
            )
        }
    }

}