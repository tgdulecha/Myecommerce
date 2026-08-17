const BASE_URL = "http://localhost:8083/api/orders";

export async function fetchOrders(page = 1, size = 10) {
  const res = await fetch(`${BASE_URL}?page=${page}&size=${size}`);
  if (!res.ok) throw new Error("Failed to fetch orders");
  return await res.json();
  // { content, page, pageSize, totalElements, totalPages }
}

export async function fetchOrderById(id) {
  const res = await fetch(`${BASE_URL}/${id}`);
  if (!res.ok) throw new Error("Order not found");
  return res.json();
}

export async function createOrder(order) {
  const res = await fetch(BASE_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(order),
  });
  if (!res.ok) throw new Error("Create failed");
  return res;
}

export async function updateOrder(order) {
  const res = await fetch(`${BASE_URL}/${order.orderId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(order),
  });
  if (!res.ok) throw new Error("Update failed");
}

export async function deleteOrder(id) {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "DELETE",
  });
  if (!res.ok) throw new Error("Delete failed");
}
export function isShipped(order) {
  if (!order.shippedDate) return false;

  const today = new Date();
  const shippedDate = new Date(order.shippedDate);

  return shippedDate < today;
}

