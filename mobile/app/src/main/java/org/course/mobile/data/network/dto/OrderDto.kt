package org.course.mobile.data.network.dto

import kotlinx.serialization.Serializable

// Mirrors org.course.ecommerce.dto.OrderDto. Dates kept as raw ISO strings, freight
// as Double - same reasoning as ProductDto/EmployeeDto.
@Serializable
data class OrderDto(
    val orderId: Int? = null,
    val customerId: String? = null,
    val employeeId: Int? = null,
    val orderDate: String? = null,
    val requiredDate: String? = null,
    val shippedDate: String? = null,
    val shipVia: Int? = null,
    val freight: Double? = null,
    val shipName: String? = null,
    val shipAddress: String? = null,
    val shipCity: String? = null,
    val shipRegion: String? = null,
    val shipPostalCode: String? = null,
    val shipCountry: String? = null,
)
