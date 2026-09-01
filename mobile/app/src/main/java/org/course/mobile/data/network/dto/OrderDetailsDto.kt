package org.course.mobile.data.network.dto

import kotlinx.serialization.Serializable

// Mirrors org.course.ecommerce.dto.OrderDetailsDto. orderId+productId together are
// the composite primary key server-side (OrderDetailsId) - both required to
// identify a specific line for the .../{orderId}/{productId} endpoints.
@Serializable
data class OrderDetailsDto(
    val orderId: Int,
    val productId: Int,
    val productName: String? = null,
    val unitPrice: Double? = null,
    val quantity: Short? = null,
    val discount: Float? = null,
)
