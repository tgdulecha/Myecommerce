package org.course.mobile.data.network.dto

import kotlinx.serialization.Serializable

// Mirrors org.course.ecommerce.dto.EmployeeDto. Dates are kept as raw ISO strings
// (matching AccountDto's createdAt/lastLogin pattern) rather than pulling in
// kotlinx-datetime for a field that's only ever displayed, not computed on.
// `photo` (base64 in JSON server-side) is intentionally unused: create/edit forms
// never populate it (see the mobile-app plan's Employee scoping note - no image
// picker in v1), it's only here so a GET response with an existing photo still
// deserializes instead of failing.
@Serializable
data class EmployeeDto(
    val employeeId: Int? = null,
    val lastName: String,
    val firstName: String,
    val title: String? = null,
    val titleOfCourtesy: String? = null,
    val birthDate: String? = null,
    val hireDate: String? = null,
    val address: String? = null,
    val city: String? = null,
    val region: String? = null,
    val postalCode: String? = null,
    val country: String? = null,
    val homePhone: String? = null,
    val extension: String? = null,
    val photo: String? = null,
    val notes: String? = null,
    val photoPath: String? = null,
    val reportsToId: Int? = null,
    val reportsToName: String? = null,
)
