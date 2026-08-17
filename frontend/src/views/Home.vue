<template>
  <div class="app-container">
    <header class="main-header">Order Management</header>

    <div class="layout-body">
      <!-- Sidebar -->
      <aside class="sidebar">
        <h3 class="section-title">List of Orders</h3>

        <div>
          <div v-for="(order, index) in orders.content" :key="order.orderId"
            :class="['order-item', { active: index === selectedIndex }]" @click="selectOrder(order, index)">

            <p>Destination country: {{ order.shipCountry }}</p>
            <button :class="isShipped(order) ? 'btn-green' : 'btn-yellow'">
              {{ isShipped(order) ? "Shipped" : "Not Shipped" }}
            </button>
          </div>
        </div>

        <div>
          <button :disabled="currentPage === 1 || loadingOrders" @click="prevPage">
            ◀ Prev
          </button>

          <span>
            Page
            <input
              type="number"
              class="page-input"
              min="1"
              :max="orders.totalPages || undefined"
              v-model.number="pageInput"
              :disabled="loadingOrders"
              @change="goToPage"
              @keyup.enter="goToPage"
            />
          </span>

          <button :disabled="disableNext" @click="nextPage">
            Next ▶
          </button>

          <select v-model="pageSize">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="15">15</option>
            <option :value="20">20</option>
          </select>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="content">
        <!-- Order Details -->

        <section>
          <div v-if="selectedOrder" class="order-form">
            <div class="form-grid">
              <div class="form-group">
                <label>Order Date</label>
                <input type="date" v-model="selectedOrder.orderDate" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Required Date</label>
                <input type="date" v-model="selectedOrder.requiredDate" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Shipped Date</label>
                <input type="date" v-model="selectedOrder.shippedDate" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Freight</label>
                <input type="text" v-model.number="selectedOrder.freight" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Ship Name</label>
                <input type="text" v-model="selectedOrder.shipName" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Ship Address</label>
                <input type="text" v-model="selectedOrder.shipAddress" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Ship City</label>
                <input type="text" v-model="selectedOrder.shipCity" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Ship Region</label>
                <input type="text" v-model="selectedOrder.shipRegion" :readonly="editval" placeholder="—" />
              </div>

              <div class="form-group">
                <label>Postal Code</label>
                <input type="text" v-model="selectedOrder.shipPostalCode" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Country</label>
                <input type="text" v-model="selectedOrder.shipCountry" :readonly="editval" />
              </div>
            </div>

            <div class="form-actions">
              <button @click="goToNewOrder">New</button>
              <button @click="toggleEditOrder">{{ editval ? "Upd" : "Save" }}</button>
              <button v-if="!editval" @click="cancelEditOrder">Cancel</button>
              <button @click="deleteSelectedOrder">Del</button>
            </div>
          </div>

          <!-- EMPTY STATE -->
          <div v-else class="box-content text-center">
            <h3>No order selected</h3>
            <button class="btn-green">Add order</button>
          </div>
        </section>

        <!-- Order Form -->
        <section class="order-form-box">
          <div v-if="loadingDetails" class="empty-state">
            Loading order details…
          </div>

          <div v-else-if="orderDetails.length === 0" class="empty-state">
            No order details
          </div>

          <div v-else class="details-grid">
            <div v-for="detail in orderDetails" :key="detail.orderId + '-' + detail.productId" class="detail-card">
              <template v-if="editingDetailKey === detailKey(detail)">
                <div class="detail-row">
                  <span class="label">Product</span>
                  <span class="value">{{ detail.productName }}</span>
                </div>

                <div class="detail-row">
                  <span class="label">Unit Price</span>
                  <input type="number" step="0.01" min="0" class="detail-input" v-model.number="detailDraft.unitPrice" />
                </div>

                <div class="detail-row">
                  <span class="label">Quantity</span>
                  <input type="number" min="1" class="detail-input" v-model.number="detailDraft.quantity" />
                </div>

                <div class="detail-row">
                  <span class="label">Discount %</span>
                  <input type="number" min="0" max="100" class="detail-input" v-model.number="detailDraft.discountPercent" />
                </div>

                <div class="card-actions">
                  <button @click="saveDetail(detail)">Save</button>
                  <button @click="cancelDetailEdit">Cancel</button>
                </div>
              </template>

              <template v-else>
                <div class="detail-row">
                  <span class="label">Product</span>
                  <span class="value">{{ detail.productName }}</span>
                </div>

                <div class="detail-row">
                  <span class="label">Unit Price</span>
                  <span class="value">
                    {{
                      detail.unitPrice ? Number(detail.unitPrice).toFixed(2) : "—"
                    }}
                  </span>
                </div>

                <div class="detail-row">
                  <span class="label">Quantity</span>
                  <span class="value">{{ detail.quantity }}</span>
                </div>

                <div class="detail-row">
                  <span class="label">Discount</span>
                  <span class="value">{{ (detail.discount * 100).toFixed(0) }}%</span>
                </div>

                <div class="detail-row total">
                  <span class="label">Total</span>
                  <span class="value">
                    {{
                      (
                        detail.unitPrice *
                        detail.quantity *
                        (1 - detail.discount)
                      ).toFixed(2)
                    }}
                  </span>
                </div>

                <div class="card-actions">
                  <button @click="startAddDetail">New</button>
                  <button @click="startEditDetail(detail)">Upd</button>
                  <button @click="deleteDetail(detail)">Del</button>
                </div>
              </template>
            </div>

            <div v-if="isAddingDetail" class="detail-card">
              <div class="detail-row">
                <span class="label">Product ID</span>
                <input type="number" min="1" class="detail-input" v-model.number="newDetailDraft.productId" placeholder="Product ID" />
              </div>

              <div class="detail-row">
                <span class="label">Unit Price</span>
                <input type="number" step="0.01" min="0" class="detail-input" v-model.number="newDetailDraft.unitPrice" />
              </div>

              <div class="detail-row">
                <span class="label">Quantity</span>
                <input type="number" min="1" class="detail-input" v-model.number="newDetailDraft.quantity" />
              </div>

              <div class="detail-row">
                <span class="label">Discount %</span>
                <input type="number" min="0" max="100" class="detail-input" v-model.number="newDetailDraft.discountPercent" />
              </div>

              <div class="card-actions">
                <button @click="saveNewDetail">Save</button>
                <button @click="cancelAddDetail">Cancel</button>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { fetchOrders, isShipped, updateOrder, deleteOrder } from "@/services/orderService";
import {
  fetchOrderDetailsByOrderId,
  createOrderDetail,
  updateOrderDetail,
  deleteOrderDetail,
} from "@/services/orderDetailService";
import { useRouter } from "vue-router";

const router = useRouter();

const goToNewOrder = () => {
  router.push("/new-order");
};

const orders = ref({
  content: [],
  page: 1,
  pageSize: 10,
  totalPages: 0,
});

const currentPage = ref(1);
const pageInput = ref(1);
const pageSize = ref(10);
const editval = ref(true)

const selectedOrder = ref(null);
const selectedIndex = ref(null);
const orderDetails = ref([]);
const loadingDetails = ref(false);
const loadingOrders = ref(false);

const editingDetailKey = ref(null);
const detailDraft = ref({ unitPrice: 0, quantity: 1, discountPercent: 0 });
const isAddingDetail = ref(false);
const newDetailDraft = ref({ productId: null, unitPrice: 0, quantity: 1, discountPercent: 0 });
const disableNext = computed(() => {
  return (
    loadingOrders.value || orders.value.content.length<pageSize.value
  );
});
async function loadOrders() {
  loadingOrders.value = true;
  try {
    const pageData = await fetchOrders(currentPage.value, pageSize.value);
    orders.value = pageData;
    console.log(orders.value)
  } finally {
    loadingOrders.value = false;
  }
}

async function selectOrder(order, index) {
  selectedIndex.value = index;
  selectedOrder.value = { ...order };
  editval.value = true;
  editingDetailKey.value = null;
  isAddingDetail.value = false;

  loadingDetails.value = true;
  try {
    orderDetails.value = await fetchOrderDetailsByOrderId(order.orderId);
  } catch {
    orderDetails.value = [];
  } finally {
    loadingDetails.value = false;
  }
}

function toggleEditOrder() {
  if (editval.value) {
    editval.value = false;
  } else {
    saveOrderEdits();
  }
}

async function saveOrderEdits() {
  try {
    await updateOrder(selectedOrder.value);
    editval.value = true;
    await loadOrders();
  } catch (e) {
    alert(e.message);
  }
}

function cancelEditOrder() {
  if (selectedIndex.value !== null && orders.value.content[selectedIndex.value]) {
    selectedOrder.value = { ...orders.value.content[selectedIndex.value] };
  }
  editval.value = true;
}

async function deleteSelectedOrder() {
  if (!selectedOrder.value) return;
  if (!confirm(`Delete order #${selectedOrder.value.orderId}? This cannot be undone.`)) return;

  try {
    await deleteOrder(selectedOrder.value.orderId);
    selectedOrder.value = null;
    selectedIndex.value = null;
    orderDetails.value = [];
    editval.value = true;
    await loadOrders();
  } catch (e) {
    alert(e.message);
  }
}

function detailKey(detail) {
  return `${detail.orderId}-${detail.productId}`;
}

function startEditDetail(detail) {
  isAddingDetail.value = false;
  editingDetailKey.value = detailKey(detail);
  detailDraft.value = {
    unitPrice: detail.unitPrice,
    quantity: detail.quantity,
    discountPercent: Math.round(detail.discount * 100),
  };
}

function cancelDetailEdit() {
  editingDetailKey.value = null;
}

async function saveDetail(detail) {
  try {
    await updateOrderDetail({
      orderId: detail.orderId,
      productId: detail.productId,
      unitPrice: detailDraft.value.unitPrice,
      quantity: detailDraft.value.quantity,
      discount: detailDraft.value.discountPercent / 100,
    });
    editingDetailKey.value = null;
    orderDetails.value = await fetchOrderDetailsByOrderId(selectedOrder.value.orderId);
  } catch (e) {
    alert(e.message);
  }
}

async function deleteDetail(detail) {
  if (!confirm(`Remove ${detail.productName} from this order?`)) return;

  try {
    await deleteOrderDetail(detail.orderId, detail.productId);
    orderDetails.value = await fetchOrderDetailsByOrderId(selectedOrder.value.orderId);
  } catch (e) {
    alert(e.message);
  }
}

function startAddDetail() {
  editingDetailKey.value = null;
  isAddingDetail.value = true;
  newDetailDraft.value = { productId: null, unitPrice: 0, quantity: 1, discountPercent: 0 };
}

function cancelAddDetail() {
  isAddingDetail.value = false;
}

async function saveNewDetail() {
  if (!newDetailDraft.value.productId) {
    alert("Product ID is required");
    return;
  }

  try {
    await createOrderDetail({
      orderId: selectedOrder.value.orderId,
      productId: newDetailDraft.value.productId,
      unitPrice: newDetailDraft.value.unitPrice,
      quantity: newDetailDraft.value.quantity,
      discount: newDetailDraft.value.discountPercent / 100,
    });
    isAddingDetail.value = false;
    orderDetails.value = await fetchOrderDetailsByOrderId(selectedOrder.value.orderId);
  } catch (e) {
    alert(e.message);
  }
}

function nextPage() {
  
    currentPage.value++;
    loadOrders();
  
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--;
    loadOrders();
  }
}

function goToPage() {
  let page = Math.floor(pageInput.value);
  if (!page || page < 1) page = 1;

  const maxPage = orders.value.totalPages;
  if (maxPage && page > maxPage) page = maxPage;

  pageInput.value = page;
  if (page !== currentPage.value) {
    currentPage.value = page;
    loadOrders();
  }
}

// reload when page size changes
watch(pageSize, () => {
  currentPage.value = 1;
  loadOrders();
});

// keep the input in sync when the page changes elsewhere (Prev/Next)
watch(currentPage, (page) => {
  pageInput.value = page;
});

onMounted(loadOrders);
</script>

<!-- External CSS -->
<style scoped src="../css/OrderManagement.css"></style>
