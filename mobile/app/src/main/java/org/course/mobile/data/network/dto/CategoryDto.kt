package org.course.mobile.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Mirrors org.course.ecommerce.dto.CategoryDto. Unlike Product/Order/OrderDetails/
// Payment/Employee (which all use "xId"), this one's id is "categoryID" (capital
// ID), so @SerialName pins it explicitly rather than relying on a naming coincidence.
@Serializable
data class CategoryDto(
    @SerialName("categoryID") val categoryId: Int? = null,
    val categoryName: String,
    val description: String? = null,
)
