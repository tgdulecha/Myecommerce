<template>
  <nav class="navbar">
    <div class="navbar-top">
      <h1>Inventory management system</h1>

      <button
        class="hamburger"
        :class="{ open: menuOpen }"
        type="button"
        aria-label="Toggle menu"
        :aria-expanded="menuOpen"
        @click="menuOpen = !menuOpen"
      >
        <span></span>
        <span></span>
        <span></span>
      </button>
    </div>

    <ul :class="{ open: menuOpen }" @click="menuOpen = false">
      <li><router-link to="/">Home</router-link></li>
      <template v-if="currentAccount">
        <li>{{ currentAccount.email }}</li>
        <li><a href="#" @click.prevent="handleLogout">Sign Out</a></li>
      </template>
      <template v-else>
        <li><router-link to="/sign-in">Sign In</router-link></li>
        <li><router-link to="/sign-up">Sign Up</router-link></li>
      </template>
    </ul>
  </nav>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useAuth } from "@/js/auth.js";

const { currentAccount, checkSession, logout } = useAuth();
const menuOpen = ref(false);

onMounted(checkSession);

function handleLogout() {
  logout();
  // Hard navigation so every view (e.g. the orders list) re-fetches as a signed-out user,
  // instead of router.push leaving stale authenticated data on screen when already on "/"
  window.location.href = "/";
}
</script>

<style scoped>
.navbar {
  background: #1c3fb1;
  color: white;
  padding: 1rem 2rem;
}

.navbar-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.navbar h1 {
  font-size: 1.4rem;
  margin: 0;
  flex: 1 1 auto;
  min-width: 0;
}

.navbar ul {
  list-style: none;
  display: flex;
  gap: 1.5rem;
  margin: 0;
  padding: 0;
}

.navbar li {
  font-weight: 500;
}

.navbar a {
  color: white;
  text-decoration: underline;
}

.navbar a:hover {
  opacity: 0.85;
}

.hamburger {
  display: none;
  flex-direction: column;
  justify-content: center;
  flex-shrink: 0;
  margin-left: auto;
  gap: 5px;
  width: 32px;
  height: 32px;
  padding: 0;
  border: none;
  background: transparent;
  cursor: pointer;
}

.hamburger span {
  display: block;
  height: 3px;
  width: 100%;
  background: white;
  border-radius: 2px;
  transition: transform 0.2s ease, opacity 0.2s ease;
}

.hamburger.open span:nth-child(1) {
  transform: translateY(8px) rotate(45deg);
}

.hamburger.open span:nth-child(2) {
  opacity: 0;
}

.hamburger.open span:nth-child(3) {
  transform: translateY(-8px) rotate(-45deg);
}

@media (max-width: 600px) {
  .navbar {
    padding: 1rem;
  }

  .hamburger {
    display: flex;
  }

  .navbar ul {
    display: none;
    flex-direction: column;
    gap: 1rem;
    margin-top: 1rem;
  }

  .navbar ul.open {
    display: flex;
  }
}
</style>
