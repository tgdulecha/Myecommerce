<template>
  <div class="app-container">
    <header class="main-header">Order Management</header>

    <div class="layout-body">
      <form class="order-form">
        <h2>Order Registration</h2>

        <fieldset>
          <legend>Order Info</legend>

          <label>
            Order Date
            <input type="date" name="OrderDate" />
          </label>

          <label>
            Required Date
            <input type="date" name="RequiredDate" />
          </label>

          <label>
            Shipped Date
            <input type="date" name="ShippedDate" />
          </label>
        </fieldset>

        <!-- Shipping -->
        <fieldset>
          <legend>Shipping Details</legend>

          <label>
            Freight
            <input type="number" step="0.01" name="Freight" />
          </label>

          <label>
            Ship Name
            <input type="text" name="ShipName" />
          </label>

          <label>
            Ship Address
            <input type="text" name="ShipAddress" />
          </label>

          <label>
            City
            <input type="text" name="ShipCity" />
          </label>

          <label>
            Region
            <input type="text" name="ShipRegion" />
          </label>

          <label>
            Postal Code
            <input type="text" name="ShipPostalCode" />
          </label>

          <label>
            Country
            <input type="text" name="ShipCountry" />
          </label>
        </fieldset>

        <!-- Submit -->
        <button type="submit">Create Order</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { createOrder } from '@/services/orderService';


</script>

<!-- External CSS -->
<style scoped src="../css/OrderManagement.css"></style>

<style scoped>
.order-form {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  box-sizing: border-box;
}

.order-form h2 {
  margin-bottom: 1rem;
}

.order-form fieldset {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 1rem;
  margin: 0 0 1.5rem;
}

.order-form legend {
  grid-column: 1 / -1;
  font-weight: 600;
  padding: 0 0.5rem;
}

.order-form label {
  display: flex;
  flex-direction: column;
  font-weight: 600;
  font-size: 0.9rem;
}

.order-form label input {
  margin-top: 0.35rem;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-weight: normal;
  width: 100%;
  box-sizing: border-box;
}

.order-form button[type="submit"] {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  background-color: #28a745;
  color: white;
  cursor: pointer;
  width: 100%;
}

@media (min-width: 480px) {
  .order-form button[type="submit"] {
    width: auto;
  }
}
</style>
