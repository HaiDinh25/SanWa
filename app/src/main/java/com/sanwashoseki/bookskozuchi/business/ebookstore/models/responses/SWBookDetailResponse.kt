package com.sanwashoseki.bookskozuchi.business.ebookstore.models.responses

import com.sanwashoseki.bookskozuchi.models.Entity
import java.io.Serializable

class SWBookDetailResponse : Entity(), Serializable {
    var success: Boolean? = null
    var data: Data? = null
    var messages: String? = null
    var total: Int? = null

    inner class Data : Entity(), Serializable {
        var defaultPictureModel: DefaultPictureModel? = null
        var name: String? = null
        var id: Int? = null
        var isWishList: Boolean? = null
        var isPurchased: Boolean? = null
        var offlineSellingLink: String? = null
        var shortDescription: String? = null
        var shoppingCartId: Int? = null
        var vendorModel: VendorModel? = null
        var productPrice: ProductPrice? = null
        var productInfoBooks: List<ProductInfoBooks>? = null
        var productReviewsModel: List<ProductReviewsModel>? = null
        var productReviewOverview: ProductReviewOverview? = null
        var authors: List<Authors>? = null
        var updatedOnUtc: String? = null
        var updatedOnUtcString: String? = null
        var contentType: String? = null
        var orientationId: Int? = null

        inner class DefaultPictureModel : Entity(), Serializable {
            var imageUrl: String? = null
        }

        inner class VendorModel : Entity(), Serializable {
            var name: String? = null
        }

        inner class ProductPrice : Entity(), Serializable {
            var priceValue: Double? = null
        }

        inner class ProductInfoBooks : Entity(), Serializable {
            var ids: String? = null
            var key: String? = null
            var value: String? = null
        }

        inner class ProductReviewsModel : Entity(), Serializable {
            var customerName: String? = null
            var customerAvatarUrl: String? = null
            var reviewText: String? = null
            var rating: Float? = null
            var writtenOnStr: String? = null
        }

        inner class ProductReviewOverview : Entity(), Serializable {
            var ratingSum: Float? = null
            var totalReviews: Int? = null
            var isbn: String? = null
            var isbnRelatedPrint: String? = null
            var fullDescription: String? = null
            var aboutAuthor: String? = null
        }

        inner class Authors : Entity(), Serializable {
            var name: String? = null
            var story: String? = null
            var id: Int? = null
        }
    }
}