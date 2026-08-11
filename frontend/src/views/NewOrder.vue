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
      <main class="content"></main>
    </div>
  </div>
</template>

<script setup>
import { createOrder } from '@/services/orderService';


</script>

<!-- External CSS -->
<style scoped src="../css/OrderManagement.css"></style>
