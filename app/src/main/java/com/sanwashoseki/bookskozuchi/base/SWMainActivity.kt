package com.sanwashoseki.bookskozuchi.base

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sanwashoseki.bookskozuchi.R
import com.sanwashoseki.bookskozuchi.books.readbookpdf.SWPDFBookActivity
import com.sanwashoseki.bookskozuchi.business.authentication.dialogs.SWDialogFingerAuthentication
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWRefreshTokenRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.business.authentication.login.services.SWRefreshTokenInterface
import com.sanwashoseki.bookskozuchi.business.authentication.login.services.SWRefreshTokenPresenter
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.views.SWLibraryFragment
import com.sanwashoseki.bookskozuchi.business.ebookreader.views.SWReadingNowFragment
import com.sanwashoseki.bookskozuchi.business.ebookrequest.views.SWBookRequestFragment
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.requests.SWAddShoppingCartWishListRequest
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.SWAddShoppingCartWishListResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.views.SWBookStoreFragment
import com.sanwashoseki.bookskozuchi.business.more.dialogs.SWDialogInputPassCode
import com.sanwashoseki.bookskozuchi.business.more.views.SWMoreFragment
import com.sanwashoseki.bookskozuchi.business.notifications.views.SWNotificationFragment
import com.sanwashoseki.bookskozuchi.business.search.views.SWSearchBookFragment
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.views.SWShoppingCartFragment
import com.sanwashoseki.bookskozuchi.databinding.ActivityMainBinding
import com.sanwashoseki.bookskozuchi.others.SWDialogSignIn
import com.sanwashoseki.bookskozuchi.utilities.SWApplication.Companion.context
import com.sanwashoseki.bookskozuchi.utilities.SWUIHelper
import com.sanwashoseki.bookskozuchi.utilities.Sharepref
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.concurrent.Executor

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class SWMainActivity : SWBaseActivity(), SWRefreshTokenInterface {

    companion object {
        val TAG: String = SWMainActivity::class.java.simpleName
    }

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var binding: ActivityMainBinding? = null
    private var header: View? = null

    //header in main menu
    private var tvTitle: TextView? = null
    private var headerHome: View? = null
    private var layoutButton: View? = null
    private var btnGotoSearch: ImageView? = null
    private var layoutCart: View? = null
    private var layoutNotification: View? = null
    private var tvShoppingCart: TextView? = null
    private var tvNotification: TextView? = null
    private var layoutTitleHome: View? = null

    //header in children screen
    private var headerChild: View? = null
    private var tvTitleChild: TextView? = null
    private var btnBack: ImageView? = null
    private var btnFilter: ImageView? = null
    private var btnWishList: ImageView? = null
    private var layoutSearch: View? = null
    private var btnSearch: ImageView? = null
    private var edtSearchContent: EditText? = null
    private var btnRemoveContent: ImageView? = null

    private var dialogSignIn: SWDialogSignIn? = null
    private var presenter: SWRefreshTokenPresenter? = null

    private var listener: OnHeaderClickedListener? = null

    private var menuPos = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        replaceFragment(R.id.container, SWBookStoreFragment(), true, null)
        initUI()

        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detachView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: SWPDFBookActivity.SWPurchaseEvent) {
        event.book.let { book ->
            Log.d(SWPDFBookActivity.TAG, "onMessageEvent: $book")
            if (Sharepref.getUserToken(context) != "") {
                presenter?.request = SWAddShoppingCartWishListRequest(book?.id, 1)
                presenter?.addShoppingCart(this)
            } else {
                showSignIn()
            }
        }
    }

    private fun initUI() {
        checkExternalStoragePermission()
        presenter = SWRefreshTokenPresenter()
        presenter?.attachView(this)

        //Hàm này dùng để xó cacheDir
        try {
//            trimCache(this)
        } catch (e: java.lang.Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        val window: Window = this.window

        // Change the status bar color
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorStatusBar)

        val requestRefreshToken = SWRefreshTokenRequest(Sharepref.getUserRefreshToken(this))
//        presenter?.refreshToken(this, requestRefreshToken)
        setupHeader()
        val navigationItemSelected = BottomNavigationView.OnNavigationItemSelectedListener {
            var isLogin = true
            when (it.itemId) {
                R.id.action_bookstore -> {
                    if (menuPos != 1) {
                        setHeaderInMainMenu(
                            getString(R.string.navigation_book_store),
                            hideButton = false
                        )
                        setBottomNavigationMenu(1)
                    }
                }
                R.id.action_library -> {
                    if (menuPos != 2) {
                        if (Sharepref.getUserToken(applicationContext) != "") {
                            setHeaderInMainMenu(
                                getString(R.string.navigation_library),
                                hideButton = true
                            )
                            setBottomNavigationMenu(2)
                        } else {
                            isLogin = false
                            showSignIn()
                        }
                    }
                }
                R.id.action_reading_now -> {
                    if (menuPos != 3) {
                        if (Sharepref.getUserToken(applicationContext) != "") {
                            setHeaderInMainMenu(getString(R.string.navigation_reading_now), true)
                            setBottomNavigationMenu(3)
                        } else {
                            isLogin = false
                            showSignIn()
                        }
                    }
                }
                R.id.action_request_book -> {
                    if (menuPos != 4) {
                        setHeaderInMainMenu(getString(R.string.navigation_request_book), true)
                        setBottomNavigationMenu(4)
                    }
                }
                R.id.action_more -> {
                    if (menuPos != 5) {
                        setHeaderInMainMenu(getString(R.string.navigation_more), true)
                        setBottomNavigationMenu(5)
                    }
                }
            }
            return@OnNavigationItemSelectedListener isLogin
        }
        binding?.bottomNavigation?.setOnNavigationItemSelectedListener(navigationItemSelected)
        val userToken = Sharepref.getUserToken(context)
        val passcodeIsEnabled = Sharepref.getPassCodeAuth(this)
        if (!userToken.equals("") && passcodeIsEnabled) {
            if (Sharepref.getBiometricAuth(this)) {
                showBiometricPrompt()
            } else {
                SWDialogInputPassCode(this, true).show()
            }
        }
    }

    private fun trimCache(context: Context) {
        try {
            val dir = context.cacheDir
            if (dir != null && dir.isDirectory) {
                deleteDir(dir)
            }
        } catch (e: Exception) {
            // TODO: handle exception
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }

    private fun showDialogFinger() {
        if (applicationContext.getSystemService(Context.FINGERPRINT_SERVICE) == null) {
            SWDialogInputPassCode(this, true).show()
        } else {
            val fingerPrintManager =
                applicationContext.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            if (!fingerPrintManager.isHardwareDetected || !fingerPrintManager.hasEnrolledFingerprints()) {
                SWDialogInputPassCode(this, true).show()
            } else {
                SWDialogFingerAuthentication(this).show()
            }
        }
    }

    private fun showBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Biometric authentication error : $errString", Toast.LENGTH_LONG
                    )
                        .show()
                    SWDialogInputPassCode(this@SWMainActivity, true).show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setAllowedAuthenticators(BIOMETRIC_WEAK)
            .setNegativeButtonText("Using pass code in stead")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }


    private val bookStoreFragment = SWBookStoreFragment.newInstance()
    private val libraryFragment = SWLibraryFragment.newInstance()
    private val readingNowFragment = SWReadingNowFragment.newInstance()
    private val bookRequestFragment = SWBookRequestFragment.newInstance(false)
    private val moreFragment = SWMoreFragment.newInstance()
    fun setBottomNavigationMenu(num: Int?) {
        when (num) {
            1 -> {
                setTitleHome(true)
                replaceFragment(R.id.container, bookStoreFragment, true, null)
                menuPos = 1
            }
            2 -> {
                setTitleHome(false)
                replaceFragment(R.id.container, libraryFragment, true, null)
                menuPos = 2
            }
            3 -> {
                setTitleHome(false)
                replaceFragment(R.id.container, readingNowFragment, true, null)
                menuPos = 3
            }
            4 -> {
                setTitleHome(false)
                replaceFragment(R.id.container, bookRequestFragment, true, null)
                menuPos = 4
            }
            5 -> {
                setTitleHome(false)
                replaceFragment(R.id.container, moreFragment, true, null)
                menuPos = 5
            }
        }
    }

    private fun setTitleHome(isHome: Boolean) {
        if (isHome) {
            layoutTitleHome?.visibility = View.VISIBLE
            tvTitle?.visibility = View.GONE
        } else {
            layoutTitleHome?.visibility = View.GONE
            tvTitle?.visibility = View.VISIBLE
        }
    }

    private fun setupHeader() {
        header = binding?.header

        //header in main menu
        headerHome = header?.findViewById(R.id.headerHome)
        layoutTitleHome = headerHome?.findViewById(R.id.layoutTitleHome)
        tvTitle = header?.findViewById(R.id.tvTitle)
        tvShoppingCart = header?.findViewById(R.id.tvShoppingCart)
        tvNotification = header?.findViewById(R.id.tvNotification)
        layoutButton = header?.findViewById(R.id.layoutButton)
        btnGotoSearch = header?.findViewById(R.id.btnGotoSearch)
        layoutCart = header?.findViewById(R.id.layoutCart)
        layoutNotification = header?.findViewById(R.id.layoutNotification)

        //header in children screen
        headerChild = header?.findViewById(R.id.headerChild)
        tvTitleChild = header?.findViewById(R.id.tvTitleChild)
        btnFilter = header?.findViewById(R.id.btnFilter)
        btnWishList = header?.findViewById(R.id.btnWishList)
        btnBack = header?.findViewById(R.id.btnBack)
        layoutSearch = header?.findViewById(R.id.layoutSearch)
        btnSearch = header?.findViewById(R.id.btnSearch)
        edtSearchContent = header?.findViewById(R.id.edtSearchContent)
        btnRemoveContent = header?.findViewById(R.id.btnRemoveContent)
    }

    //setting button in header in main
    fun setHeaderInMainMenu(title: String?, hideButton: Boolean) {
        tvTitle?.text = title
        headerHome?.visibility = View.VISIBLE
        headerChild?.visibility = View.GONE
        if (Sharepref.getUserToken(this) == "") {
            layoutNotification?.visibility = View.GONE
        }
        if (hideButton) {
            layoutButton?.visibility = View.GONE
        } else {
            layoutButton?.visibility = View.VISIBLE
            layoutCart?.visibility = View.VISIBLE
            tvShoppingCart?.visibility = View.VISIBLE
            btnFilter?.visibility = View.GONE

            layoutCart?.setOnClickListener {
                if (Sharepref.getUserToken(this) == "") {
                    showSignIn()
                } else {
                    listener?.onShoppingCartClicked()
                }
            }
            btnGotoSearch?.setOnClickListener {
                replaceFragment(
                    R.id.container,
                    SWSearchBookFragment.newInstance(false, null),
                    false,
                    null
                )
            }
            layoutNotification?.setOnClickListener {
                replaceFragment(R.id.container, SWNotificationFragment.newInstance(), false, null)
            }
        }
    }

    //setting header in children screen
    fun setHeaderInChildrenScreen(title: String, isSearch: Boolean, isFilter: Boolean) {
        setLayoutSearch(isSearch)
        headerHome?.visibility = View.GONE
        headerChild?.visibility = View.VISIBLE
        tvTitleChild?.text = title
        btnBack?.setOnClickListener { onBackPressed() }

        if (isFilter) {
            btnFilter?.visibility = View.VISIBLE
            btnFilter?.setOnClickListener { listener?.onFilterClicked() }
        } else {
            btnFilter?.visibility = View.GONE
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setWishListButton(hideWishList: Boolean, isWishList: Boolean) {
        if (hideWishList) {
            btnFilter?.visibility = View.VISIBLE
            btnWishList?.visibility = View.GONE
            btnFilter?.setOnClickListener { listener?.onFilterClicked() }
        } else {
            btnFilter?.visibility = View.GONE
            if (!SWUIHelper.isTablet(context)) {
                btnWishList?.visibility = View.VISIBLE
                if (isWishList) {
                    btnWishList?.setImageDrawable(getDrawable(R.drawable.ic_wish_list))
                } else {
                    btnWishList?.setImageDrawable(getDrawable(R.drawable.ic_un_wish_list))
                }
                btnWishList?.setOnClickListener { listener?.onWishListClicked(isWishList) }
            }
        }
    }

    //setting search
    private fun setLayoutSearch(search: Boolean) {
        if (search) {
            tvTitleChild?.visibility = View.GONE
            btnFilter?.visibility = View.GONE
            btnWishList?.visibility = View.GONE
            layoutSearch?.visibility = View.VISIBLE
            btnRemoveContent?.setOnClickListener { edtSearchContent?.text?.clear() }
            btnSearch?.setOnClickListener { listener?.onSearchListener(edtSearchContent?.text.toString()) }

            edtSearchContent?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (event.action === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    listener?.onSearchListener(edtSearchContent?.text.toString())
                    return@OnKeyListener true
                }
                false
            })
        } else {
            tvTitleChild?.visibility = View.VISIBLE
            layoutSearch?.visibility = View.GONE
        }
    }

    fun hideBottomNavigationMenu(hide: Boolean) {
        if (hide) {
            binding?.bottomNavigation?.visibility = View.GONE
        } else {
            binding?.bottomNavigation?.visibility = View.VISIBLE
        }
    }

    fun hideHeader() {
        headerHome?.visibility = View.GONE
        headerChild?.visibility = View.GONE
    }

    fun hideWishListFilter() {
        btnFilter?.visibility = View.GONE
        btnWishList?.visibility = View.GONE
    }

    fun removeSearch() {
        edtSearchContent?.text?.clear()
    }

    fun setShoppingCart(num: Int?, isLogin: Boolean) {
        if (isLogin) {
            tvShoppingCart?.visibility = View.VISIBLE
            if (num == 0) {
                tvShoppingCart?.visibility = View.GONE
            } else {
                tvShoppingCart?.visibility = View.VISIBLE
            }
            tvShoppingCart?.text = num.toString()
        } else {
            tvShoppingCart?.visibility = View.GONE
        }
    }

    fun setNotification(num: Int?) {
        tvNotification?.text = num.toString()
    }

    fun onCallBackSearchListener(listener: OnHeaderClickedListener) {
        this.listener = listener
    }

    interface OnHeaderClickedListener {
        fun onSearchListener(content: String)

        fun onShoppingCartClicked()

        fun onWishListClicked(isWishList: Boolean)

        fun onFilterClicked()
    }

    override fun refreshSuccess(result: SWLoginResponse?) {
        Sharepref.removeUserInfo(this)
        val email = result?.data?.customer?.email.toString()
        val password = Sharepref.getUserPassWord(this).toString()
        val token = result?.data?.accessToken?.tokenString.toString()
        val refreshToken = result?.data?.refreshToken?.tokenString.toString()
        Sharepref.saveUserInfo(this, email, password, token, refreshToken)
    }

    override fun addShoppingCartSuccess(result: SWAddShoppingCartWishListResponse?) {
        replaceFragment(
            R.id.container,
            SWShoppingCartFragment.newInstance(null),
            false,
            null
        )
    }

    override fun getShoppingCartSuccess(result: SWGetShoppingCartResponse) {
//        Handler().post {
//            replaceFragment(R.id.container,
//                SWShoppingCartFragment.newInstance(result.data),
//                false,
//                null)
//        }
    }

    override fun showMessageError(msg: String) {
        Sharepref.removeUserInfo(this)
    }

    override fun showIndicator() {}

    override fun hideIndicator() {}

    override fun showNetworkError() {}

}