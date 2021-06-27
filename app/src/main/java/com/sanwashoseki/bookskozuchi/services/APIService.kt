package com.sanwashoseki.bookskozuchi.services

import com.sanwashoseki.bookskozuchi.books.models.SWBookReadHistoryResponse
import com.sanwashoseki.bookskozuchi.books.models.SWEditDictionaryResponse
import com.sanwashoseki.bookskozuchi.books.models.SWUpdateCurrentReadingModel
import com.sanwashoseki.bookskozuchi.business.authentication.forgotpassword.models.requests.SWConfirmEmailRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginFacebookRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginLineRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWLoginRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.requests.SWRefreshTokenRequest
import com.sanwashoseki.bookskozuchi.business.authentication.login.models.responses.SWLoginResponse
import com.sanwashoseki.bookskozuchi.business.authentication.logout.models.requests.SWLogoutRequest
import com.sanwashoseki.bookskozuchi.business.authentication.logout.models.responses.SWLogoutResponse
import com.sanwashoseki.bookskozuchi.business.authentication.register.models.requests.SWRegisterRequest
import com.sanwashoseki.bookskozuchi.business.authentication.register.models.responses.SWRegisterResponse
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.models.requests.SWAddReviewRequest
import com.sanwashoseki.bookskozuchi.business.ebooklibrary.models.responses.SWMyBookResponse
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWDrmLicenseResponse
import com.sanwashoseki.bookskozuchi.business.ebookreader.models.response.SWReadingNowResponse
import com.sanwashoseki.bookskozuchi.business.ebookrequest.models.responses.SWRequestBookContentLibraryResponse
import com.sanwashoseki.bookskozuchi.business.ebookrequest.models.responses.SWRequestBookLibraryResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.entitys.SWBookStoreResponse
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.requests.SWAddShoppingCartWishListRequest
import com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses.*
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWFilterBookResponse
import com.sanwashoseki.bookskozuchi.business.filter.models.responses.SWPublisherResponse
import com.sanwashoseki.bookskozuchi.business.more.models.requests.SWChangePasswordRequest
import com.sanwashoseki.bookskozuchi.business.more.models.requests.SWSendContactRequest
import com.sanwashoseki.bookskozuchi.business.more.models.requests.SWUpdateProfileRequest
import com.sanwashoseki.bookskozuchi.business.more.models.responses.*
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWNotificationResponse
import com.sanwashoseki.bookskozuchi.business.search.models.responses.SWSearchBookResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.requests.SWMakeThePaymentRequest
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWGetShoppingCartResponse
import com.sanwashoseki.bookskozuchi.business.shoppingcart.models.response.SWRemoveShoppingCartResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWBookmarkRequestModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticRequestModel
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.models.SWPhoneticModel
import com.sanwashoseki.bookskozuchi.books.models.SWBookmarksResponse
import com.sanwashoseki.bookskozuchi.books.readbooktxt.readbyeye.utils.SWCommonResponse
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWSetStatusNotification
import com.sanwashoseki.bookskozuchi.business.notifications.models.responses.SWUnreadNotificationResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface APIService {

    @POST("api/Authentication/login")
    fun loginEmail(@Body request: SWLoginRequest?): Observable<SWLoginResponse?>?

    @POST("api/ExternalAuth/Facebook/login")
    fun loginFacebook(@Body request: SWLoginFacebookRequest?): Observable<SWLoginResponse?>?

    @POST("api/ExternalAuth/Line/login")
    fun loginLine(@Body request: SWLoginLineRequest?): Observable<SWLoginResponse?>?

    @POST("api/Authentication/logout")
    fun logout(
        @Header("Authorization") token: String?,
        @Body request: SWLogoutRequest?,
    ): Observable<SWLogoutResponse?>?

    @POST("api/Authentication/register")
    fun register(@Body request: SWRegisterRequest?): Observable<SWRegisterResponse?>?

    @POST("api/Authentication/refresh-token")
    fun refreshToken(
        @Header("Authorization") accessToken: String?,
        @Body request: SWRefreshTokenRequest?,
    ): Observable<SWLoginResponse?>?

    @POST("api/Customer/passwordrecoverysendemail")
    fun confirmEmail(@Body request: SWConfirmEmailRequest?): Observable<SWRegisterResponse>

    @GET("api/Customer")
    fun getUserInfo(@Header("Authorization") token: String?): Observable<SWProfileResponse?>?

    @POST("api/Customer")
    fun setUserInfo(
        @Header("Authorization") token: String?,
        @Body request: SWUpdateProfileRequest?,
    ): Observable<SWUpdateProfileResponse?>?

    @Multipart
    @POST("/api/Customer/uploadAvatar")
    fun uploadAvatar(
        @Header("Authorization") token: String?,
        @Part uploadedFile: MultipartBody.Part?,
    ): Observable<SWUploadAvatarResponse?>?

    @POST("api/Customer/changepassword")
    fun changePassword(
        @Header("Authorization") token: String?,
        @Body request: SWChangePasswordRequest?,
    ): Observable<SWChangePasswordResponse?>?

    @GET("api/Category")
    fun getCategory(): Observable<SWCategoriesResponse?>?

    @GET("api/Category/listProductByCategory")
    fun getProductCategoryBook(
        @Query("CategoryId") CategoryId: Int?,
        @Query("Start") Start: Int?,
        @Query("Length") Length: Int?,
    ): Observable<SWChildCategoriesBookResponse?>?

    @GET("api/Product")
    fun getBookStoreHighLight(
        @Query("IsHighlightBooks") IsHighlightBooks: Boolean?,
        @Query("Start") Start: Int?,
        @Query("Length") Length: Int?,
    ): Observable<SWBookStoreResponse?>?

    @GET("/api/Product/best-seller")
    fun getBookStoreTopSeller(
        @Query("Start") Start: Int?,
        @Query("Length") Length: Int?,
    ): Observable<SWBookStoreResponse?>?

    @GET("api/Product")
    fun getBookStoreLatest(
        @Query("IsLastestBooks") IsLastestBooks: Boolean?,
        @Query("Start") Start: Int?,
        @Query("Length") Length: Int?,
    ): Observable<SWBookStoreResponse?>?

    @GET("api/Product")
    fun searchBook(
        @Query("bookOrAuthorName") bookOrAuthorName: String?,
        @Query("Start") Start: Int?,
        @Query("Length") Length: Int?,
    ): Observable<SWSearchBookResponse?>?

    @GET("api/Product")
    fun filterInCategory(
        @Query("categoryIds") categoryIds: Int?,
        @Query("Start") Start: Int?,
        @Query("Length") Length: Int?,
        @Query("isAudioReading") isAudioReading: Boolean?,
        @Query("priceMin") priceMin: String?,
        @Query("priceMax") priceMax: String?,
        @Query("publisherIds") publisherIds: List<Int>?,
    ): Observable<SWFilterBookResponse?>?

    @GET("api/Product")
    fun filterHighLightBook(
        @Query("IsHighlightBooks") IsHighlightBooks: Boolean?,
        @Query("categoryIds") categoryIds: List<Int>?,
        @Query("Start") Start: Int?,
        @Query("Length") Length: Int?,
        @Query("isAudioReading") isAudioReading: Boolean?,
        @Query("priceMin") priceMin: String?,
        @Query("priceMax") priceMax: String?,
        @Query("publisherIds") publisherIds: List<Int>?,
    ): Observable<SWFilterBookResponse?>?

    @GET("api/Product/best-seller")
    fun filterTopSellerBook(
        @Query("categoryIds") categoryIds: List<Int>?,
        @Query("Start") Start: Int?,
        @Query("Length") Length: Int?,
        @Query("isAudioReading") isAudioReading: Boolean?,
        @Query("priceMin") priceMin: String?,
        @Query("priceMax") priceMax: String?,
        @Query("publisherIds") publisherIds: List<Int>?,
    ): Observable<SWFilterBookResponse?>?

    @GET("api/Product")
    fun filterLatestBook(
        @Query("IsLastestBooks") IsLastestBooks: Boolean?,
        @Query("categoryIds") categoryIds: List<Int>?,
        @Query("Start") Start: Int?,
        @Query("Length") Length: Int?,
        @Query("isAudioReading") isAudioReading: Boolean?,
        @Query("priceMin") priceMin: String?,
        @Query("priceMax") priceMax: String?,
        @Query("publisherIds") publisherIds: List<Int>?,
    ): Observable<SWFilterBookResponse?>?

    @GET("api/Product")
    fun filterInSearch(
        @Query("bookOrAuthorName") bookOrAuthorName: String?,
        @Query("categoryIds") categoryIds: List<Int>?,
        @Query("Start") Start: Int?,
        @Query("Length") Length: Int?,
        @Query("isAudioReading") isAudioReading: Boolean?,
        @Query("priceMin") priceMin: String?,
        @Query("priceMax") priceMax: String?,
        @Query("publisherIds") publisherIds: List<Int>?,
    ): Observable<SWFilterBookResponse?>?

    @GET("api/Product")
    fun getSameCategory(
        @Query("categoryIds") categoryIds: List<Int>?,
        @Query("Start") Start: Int?,
        @Query("Length") Length: Int?,
        @Query("publisherIds") publisherIds: List<Int>?,
    ): Observable<SWFilterBookResponse?>?

    @GET("api/Vendor")
    fun getPublisher(): Observable<SWPublisherResponse?>?

    @GET("api/ShoppingCart")
    fun getShoppingCart(@Header("Authorization") token: String?): Observable<SWGetShoppingCartResponse?>?

    //shoppingCartTypeId == (1 -> add shopping cart) else if (2 -> add wish list)
    @FormUrlEncoded
    @POST("api/ShoppingCart")
    fun getAddShoppingCartWishList(
        @Header("Authorization") token: String?,
        @Field("productId") productId: Int?,
        @Field("shoppingCartTypeId") shoppingCartTypeId: Int?,
    ): Observable<SWGetShoppingCartResponse?>?

    //1 -> Xóa all giỏ hàng, 2-> xóa all wish list
    @DELETE("api/ShoppingCart/deleteAllCartOrWishList/{cartType}")
    fun removeAllCartOrWishList(
        @Header("Authorization") token: String?,
        @Path("cartType") cartType: Int?,
    ): Observable<SWRemoveShoppingCartResponse?>?

    //1 -> Xóa item giỏ hàng, 2-> xóa item wish list
    @DELETE("api/ShoppingCart/{id}")
    fun removeShoppingCart(
        @Header("Authorization") token: String?,
        @Path("id") id: Int?,
    ): Observable<SWRemoveShoppingCartResponse?>?

    @POST("api/ShoppingCart")
    fun addShoppingCartWishList(
        @Header("Authorization") token: String?,
        @Body request: SWAddShoppingCartWishListRequest?,
    ): Observable<SWAddShoppingCartWishListResponse?>?

    @GET("api/ShoppingCart/wishlist")
    fun getWishList(
        @Header("Authorization") token: String?,
    ): Observable<SWGetShoppingCartResponse?>?

    @POST("api/checkout/confirmOrder")
    fun makeThePayment(
        @Header("Authorization") token: String?,
        @Body request: SWMakeThePaymentRequest?,
    ): Observable<SWAddShoppingCartWishListResponse?>?

    @GET("api/product/{productId}")
    fun getDetailBook(
        @Header("Authorization") token: String?,
        @Path("productId") productId: Int?,
    ): Observable<SWBookDetailResponse?>?

    @GET("api/Product/similar-book")
    fun getSimilar(
        @Query("start") start: Int?,
        @Query("length") length: Int?,
        @Query("productIdSimilar") productIdSimilar: Int?,
    ): Observable<SWBookStoreResponse>

    @GET("api/BookReadHistory/unread")
    fun getMyBook(
        @Header("Authorization") token: String?,
        @Query("start") start: Int?,
        @Query("length") length: Int?,
    ): Observable<SWMyBookResponse?>?


    @GET("api/Review")
    fun getMyReview(@Header("Authorization") token: String?): Observable<SWMyReviewResponse?>?

    @DELETE("api/Review")
    fun deleteReview(
        @Header("Authorization") token: String?,
        @Query("id") id: Int?,
    ): Observable<SWRegisterResponse?>?

    @POST("api/Review")
    fun sendReview(
        @Header("Authorization") token: String?,
        @Body request: SWAddReviewRequest?,
    ): Observable<SWAddShoppingCartWishListResponse?>?

    @GET("api/BookRequestTopic/getBookRequestTopicsLibrary")
    fun getRequestBookLibrary(
        @Query("start") start: Int?,
        @Query("length") length: Int?,
    ): Observable<SWRequestBookLibraryResponse?>?

    @GET("api/BookRequestTopic")
    fun getMyRequestBook(
        @Header("Authorization") token: String?,
        @Query("start") start: Int?,
        @Query("length") length: Int?,
        @Query("keySearch") keySearch: String?,
    ): Observable<SWRequestBookLibraryResponse?>?

    @GET("api/BookRequestTopic/{bookRequestTopicId}")
    fun getRequestBookContentLibrary(
        @Header("Authorization") token: String?,
        @Path("bookRequestTopicId") bookRequestTopicId: Int?,
    ): Observable<SWRequestBookContentLibraryResponse?>?

    @Multipart
    @POST("api/BookRequestTopic")
    fun addRequestBook(
        @Header("Authorization") token: String?,
        @Part("name") name: RequestBody?,
        @Part("authorName") authorName: RequestBody?,
        @Part("body") body: RequestBody?,
        @Part files: ArrayList<MultipartBody.Part>,
    ): Observable<SWAddShoppingCartWishListResponse?>?

    @Multipart
    @POST("api/BookRequestPost")
    fun replyRequestBook(
        @Header("Authorization") token: String?,
        @Part("bookRequestTopicId") bookRequestTopicId: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part file: ArrayList<MultipartBody.Part>,
    ): Observable<SWAddShoppingCartWishListResponse?>?

    @DELETE("api/BookRequestTopic/{bookRequestTopicId}")
    fun deleteRequest(
        @Header("Authorization") token: String?,
        @Path("bookRequestTopicId") bookRequestTopicId: Int?,
    ): Observable<SWAddShoppingCartWishListResponse?>?

    @GET("api/common/aboutUs?systemName=TermAndCondition,PrivacyPolicy")
    fun getAboutUs(): Observable<SWAboutUsResponse?>?

    @GET("api/Faq")
    fun getFAQs(): Observable<SWFAQsResponse?>?

    @POST("api/Common/contactUsSendNoAuth")
    fun sendContact(@Body request: SWSendContactRequest?): Observable<SWAddShoppingCartWishListResponse?>?

    @GET("api/Order?orderId=0&start=0&length=10&bookNameOrAuthorName")
    fun getPurchaseHistory(
        @Header("Authorization") token: String?,
        @Query("start") start: Int?,
        @Query("length") length: Int?,
    ): Observable<SWPurchaseHistoryResponse?>?

    @GET("api/Customer/getNotificationConfig")
    fun getStatusNotification(@Header("Authorization") token: String?): Observable<SWPushNotificationResponse?>?

    @PUT("api/Customer")
    fun pushStatusNotification(
        @Header("Authorization") token: String?,
        @Query("IsMobileAppNotificationEnable") IsMobileAppNotificationEnable: Boolean?,
    ): Observable<SWAddShoppingCartWishListResponse?>?

    @GET("api/Notification/countUnread")
    fun getUnreadNotifications(
        @Header("Authorization") token: String?
    ): Observable<SWUnreadNotificationResponse?>?

    @GET("api/Notification")
    fun getNotifications(
        @Header("Authorization") token: String?,
        @Query("Start") start: Int?,
        @Query("Length") length: Int?,
    ): Observable<SWNotificationResponse?>?

    @PUT("api/Notification/{id}")
    fun setStatusNotification(
        @Header("Authorization") token: String?,
        @Path("id") id: Int?,
    ): Observable<SWSetStatusNotification?>?

    @GET("api/BookReadHistory")
    fun getReadingNow(@Header("Authorization") token: String?): Observable<SWReadingNowResponse?>?

    @GET("api/BookReadHistory/{productId}")
    fun getReadHistory(
        @Header("Authorization") token: String?,
        @Path("productId") productId: Int?,
    ): Observable<SWBookReadHistoryResponse?>?

    //Bookmark
    @POST("api/Bookmark")
    fun addBookmark(
        @Header("Authorization") token: String?,
        @Body requestModel: SWBookmarkRequestModel?,
    ): Observable<SWCommonResponse<Any>?>?

    @GET("api/Bookmark")
    fun listBookmark(
        @Header("Authorization") token: String?,
        @Query("productId") productId: Int?,
        @Query("page") page: Int?,
        @Query("pageSize") pageSize: Int?,
    ): Observable<SWBookmarksResponse?>?

    @DELETE("api/Bookmark/{bookmarkId}")
    fun deleteBookmark(
        @Header("Authorization") token: String?,
        @Path("bookmarkId") bookmarkId: Int?,
    ): Observable<SWCommonResponse<Any>?>?

    //Dictionary
    @GET("api/CustomerDictionary")
    fun listDictionary(
        @Header("Authorization") token: String?,
        @Query("productId") productId: Int?,
        @Query("page") page: Int?,
        @Query("pageSize") pageSize: Int?,
    ): Observable<SWCommonResponse<MutableList<SWPhoneticModel>>?>?

    @POST("api/CustomerDictionary")
    fun addDictionary(
        @Header("Authorization") token: String?,
        @Body requestModel: SWPhoneticRequestModel?,
    ): Observable<SWCommonResponse<Any>?>?

    @PUT("api/CustomerDictionary/{id}")
    fun editDictionary(
        @Header("Authorization") token: String?,
        @Path("id") id: Int?,
        @Body requestModel: SWPhoneticRequestModel?,
    ): Observable<SWEditDictionaryResponse?>?

    @DELETE("api/CustomerDictionary/{customerDictionaryId}")
    fun deleteDictionary(
        @Header("Authorization") token: String?,
        @Path("customerDictionaryId") customerDictionaryId: Int?,
    ): Observable<SWCommonResponse<Any>?>?

    @GET("api/Product/download/{productId}")
    @Streaming
    fun downloadBook(
        @Header("Authorization") token: String,
        @Path("productId") productId: Int?,
    ): Observable<retrofit2.Response<ResponseBody>?>?

    @GET("api/download/sample/{productId}")
    @Streaming
    fun downloadSampleBook(
        @Header("Authorization") token: String,
        @Path("productId") productId: Int?,
    ): Observable<retrofit2.Response<ResponseBody>?>?

    @GET("api/Product/drm-license/{productId}")
    fun getDrmLicense(
        @Header("Authorization") token: String,
        @Path("productId") productId: Int?,
    ): Observable<SWDrmLicenseResponse?>?

    @PUT("/api/BookReadHistory/{productId}")
    fun updateCurrentReadingBook(
        @Header("Authorization") token: String,
        @Path("productId") productId: Int?,
        @Body request: SWUpdateCurrentReadingModel?,
    ): Observable<SWCommonResponse<Any>>?
}