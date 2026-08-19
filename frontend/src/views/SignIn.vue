<template>
  <div class="app-container">
    <header class="main-header">Sign In</header>

    <div class="layout-body">
      <form class="order-form" @submit.prevent="submit">
        <h2>Sign In</h2>

        <fieldset>
          <legend>Account</legend>

          <label>
            Email
            <input type="email" v-model="email" required autocomplete="email" />
          </label>

          <label>
            Password
            <input type="password" v-model="password" required autocomplete="current-password" />
          </label>
        </fieldset>

        <p v-if="error" class="form-error">{{ error }}</p>

        <button type="submit" :disabled="submitting">
          {{ submitting ? "Signing in…" : "Sign In" }}
        </button>

        <p class="form-switch">
          Don't have an account?
          <router-link to="/sign-up">Sign up</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuth } from "@/js/auth.js";

const router = useRouter();
const { login } = useAuth();

const email = ref("");
const password = ref("");
const error = ref("");
const submitting = ref(false);

async function submit() {
  error.value = "";
  submitting.value = true;
  try {
    await login(email.value, password.value);
    router.push("/");
  } catch (e) {
    error.value = e.message;
  } finally {
    submitting.value = false;
  }
}
</script>

<!-- External CSS -->
<style scoped src="../css/OrderManagement.css"></style>

<style scoped>
.order-form {
  width: 100%;
  max-width: 420px;
  margin: 0 auto;
  box-sizing: border-box;
}

.order-form h2 {
  margin-bottom: 1rem;
}

.order-form fieldset {
  display: grid;
  gap: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 1rem;
  margin: 0 0 1.5rem;
}

.order-form legend {
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

.order-form button[type="submit"]:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.form-error {
  color: #c0392b;
  font-weight: 600;
  margin: -0.5rem 0 1rem;
}

.form-switch {
  margin-top: 1rem;
  text-align: center;
}
</style>
