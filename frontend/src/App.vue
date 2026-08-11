<script setup>
// Import the external logic
import { useApp } from "./js/App.js";

// Import your components

import Navbar from "./components/Navbar.vue";
// Destructure the variables and functions for use in the template

</script>

<template>
  <div>
    <Navbar />
      <router-view />
   
  </div>
</template>