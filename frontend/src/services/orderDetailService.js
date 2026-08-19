import { authHeader } from "@/js/auth.js";

const BASE_URL = 'http://localhost:8083/api/orderdetails';

// ==================================================
// READ
// ==================================================

// ----------------------------------
// Get all OrderDetails (paginated)
// GET /api/orderdetails?page=1&size=15
// ----------------------------------
export async function fetchAllOrderDetails(page = 1, size = 15) {
  const res = await fetch(`${BASE_URL}?page=${page}&size=${size}`, {
    headers: { ...authHeader() },
  });

  if (!res.ok) {
    throw new Error('Failed to fetch order details page');
  }

  return res.json();
}

// ----------------------------------
// Get OrderDetails by OrderId
// GET /api/orderdetails?orderId=10248
// ----------------------------------
export async function fetchOrderDetailsByOrderId(orderId) {
const res = await fetch(`${BASE_URL}/${orderId}`, { headers: { ...authHeader() } });

  if (!res.ok) {
    throw new Error('Failed to fetch order details for order');
  }

  return res.json();

}

// ----------------------------------
// Get single OrderDetail
// GET /api/orderdetails/{orderId}/{productId}
// ----------------------------------
export async function fetchOrderDetail(orderId, productId) {
  const res = await fetch(`${BASE_URL}/${orderId}/${productId}`, {
    headers: { ...authHeader() },
  });

  if (!res.ok) {
    throw new Error('Order detail not found');
  }

  return res.json();
}

// ==================================================
// CREATE
// ==================================================

// ----------------------------------
// Create OrderDetail
// POST /api/orderdetails
// ----------------------------------
export async function createOrderDetail(detail) {
  const res = await fetch(BASE_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...authHeader(),
    },
    body: JSON.stringify(detail)
  });

  if (!res.ok) {
    throw new Error('Failed to create order detail');
  }

  return res.json();
}

// ==================================================
// UPDATE
// ==================================================

// ----------------------------------
// Update OrderDetail
// PUT /api/orderdetails/{orderId}/{productId}
// ----------------------------------
export async function updateOrderDetail(detail) {
  const res = await fetch(
    `${BASE_URL}/${detail.orderId}/${detail.productId}`,
    {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...authHeader(),
      },
      body: JSON.stringify(detail)
    }
  );

  if (!res.ok) {
    throw new Error('Failed to update order detail');
  }
}

// ==================================================
// DELETE
// ==================================================

// ----------------------------------
// Delete OrderDetail
// DELETE /api/orderdetails/{orderId}/{productId}
// ----------------------------------
export async function deleteOrderDetail(orderId, productId) {
  const res = await fetch(`${BASE_URL}/${orderId}/${productId}`, {
    method: 'DELETE',
    headers: { ...authHeader() },
  });

  if (!res.ok) {
    throw new Error('Failed to delete order detail');
  }
}
