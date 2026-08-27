import { authHeader } from "@/js/auth.js";

const BASE_URL = "http://localhost:8084/api/payments";

// ==================================================
// READ
// ==================================================

// ----------------------------------
// Get payments for an order
// GET /api/payments?orderId=10248
// ----------------------------------
export async function fetchPaymentsByOrderId(orderId) {
  const res = await fetch(`${BASE_URL}?orderId=${orderId}`, {
    headers: { ...authHeader() },
  });

  if (!res.ok) {
    throw new Error("Failed to fetch payments for order");
  }

  return res.json();
}

// ==================================================
// CREATE
// ==================================================

// ----------------------------------
// Create a payment - payment-service always starts it as PENDING,
// use updatePaymentStatus to move it forward from there.
// POST /api/payments
// ----------------------------------
export async function createPayment(payment) {
  const res = await fetch(BASE_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json", ...authHeader() },
    body: JSON.stringify(payment),
  });

  if (!res.ok) {
    throw new Error((await res.text()) || "Failed to create payment");
  }

  return res.json();
}

// ==================================================
// UPDATE
// ==================================================

// ----------------------------------
// Move a payment to its next status (PENDING -> COMPLETED/FAILED/REFUNDED).
// There is no endpoint to edit the amount/method or delete a payment -
// it's a financial record, not a CRUD resource.
// PATCH /api/payments/{id}/status
// ----------------------------------
export async function updatePaymentStatus(id, status) {
  const res = await fetch(`${BASE_URL}/${id}/status`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json", ...authHeader() },
    body: JSON.stringify({ status }),
  });

  if (!res.ok) {
    throw new Error((await res.text()) || "Failed to update payment status");
  }

  return res.json();
}

// ----------------------------------
// Badge class for a payment's status - mirrors isShipped's role in
// orderService.js (a pure, testable helper the view renders with).
// ----------------------------------
export function paymentStatusClass(payment) {
  if (!payment) return "";

  switch (payment.status) {
    case "COMPLETED":
      return "btn-green";
    case "FAILED":
      return "btn-red";
    case "REFUNDED":
      return "btn-grey";
    default:
      return "btn-yellow"; // PENDING
  }
}
