package org.course.mobile.data.network.dto

import kotlinx.serialization.Serializable

// Mirrors org.course.ecommerce.dto.ProductDto. unitPrice is BigDecimal server-side;
// represented as Double here (see the mobile-app plan's BigDecimal note - this is
// demo data display/entry, not the financial source of truth, which stays server-side).
@Serializable
data class ProductDto(
    val productId: Int? = null,
    val productName: String,
    val supplierId: Int? = null,
    val categoryId: Int? = null,
    val categoryName: String? = null,
    val quantityPerUnit: String? = null,
    val unitPrice: Double? = null,
    val unitsInStock: Short? = null,
    val unitsOnOrder: Short? = null,
    val reorderLevel: Short? = null,
    val discontinued: Boolean = false,
)
