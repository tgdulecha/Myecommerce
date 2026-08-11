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

          <span> Page {{ currentPage  }} </span>

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
                <input type="text" :value="selectedOrder.orderDate" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Required Date</label>
                <input type="text" :value="selectedOrder.requiredDate" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Shipped Date</label>
                <input type="text" :value="selectedOrder.shippedDate || 'Not shipped'" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Freight</label>
                <input type="text" :value="selectedOrder.freight" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Ship Name</label>
                <input type="text" :value="selectedOrder.shipName" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Ship Address</label>
                <input type="text" :value="selectedOrder.shipAddress" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Ship City</label>
                <input type="text" :value="selectedOrder.shipCity" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Ship Region</label>
                <input type="text" :value="selectedOrder.shipRegion || '—'" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Postal Code</label>
                <input type="text" :value="selectedOrder.shipPostalCode" :readonly="editval" />
              </div>

              <div class="form-group">
                <label>Country</label>
                <input type="text" :value="selectedOrder.shipCountry" :readonly="editval" />
              </div>
            </div>

            <div class="form-actions">
              <button @click="goToNewOrder">New</button>
              <button>Upd</button>
              <button>Del</button>
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
                <button>New</button>
                <button>Upd</button>
                <button>Del</button>
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
import { fetchOrders, isShipped } from "@/services/orderService";
import { fetchOrderDetailsByOrderId } from "@/services/orderDetailService";
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
const pageSize = ref(10);
const editval = ref(true)

const selectedOrder = ref(null);
const selectedIndex = ref(null);
const orderDetails = ref([]);
const loadingDetails = ref(false);
const loadingOrders = ref(false);
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

  loadingDetails.value = true;
  try {
    orderDetails.value = await fetchOrderDetailsByOrderId(order.orderId);
  } catch {
    orderDetails.value = [];
  } finally {
    loadingDetails.value = false;
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

// reload when page size changes
watch(pageSize, () => {
  currentPage.value = 1;
  loadOrders();
});

onMounted(loadOrders);
</script>

<!-- External CSS -->
<style scoped src="../css/OrderManagement.css"></style>
