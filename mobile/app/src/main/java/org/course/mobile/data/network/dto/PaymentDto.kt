package org.course.mobile.data.network.dto

import kotlinx.serialization.Serializable

// Mirrors org.course.paymentservice.dto.PaymentDto. Status values are constrained
// server-side to these four (inferred from the web frontend's paymentStatusClass
// helper - not a formal enum in the backend DTO, which just types it as String).
enum class PaymentStatus {
    PENDING, COMPLETED, FAILED, REFUNDED
}

@Serializable
data class PaymentDto(
    val paymentId: Int? = null,
    val orderId: Int,
    val customerEmail: String,
    val amount: Double,
    val method: String,
    val status: String = PaymentStatus.PENDING.name,
    val transactionDate: String? = null,
)
