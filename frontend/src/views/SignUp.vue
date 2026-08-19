<template>
  <div class="app-container">
    <header class="main-header">Sign Up</header>

    <div class="layout-body">
      <form class="order-form" @submit.prevent="submit">
        <h2>Create an Account</h2>

        <fieldset>
          <legend>Company Info</legend>

          <label>
            Company Name
            <input type="text" v-model="companyName" required autocomplete="organization" />
          </label>

          <label>
            Contact Name
            <input type="text" v-model="contactName" autocomplete="name" />
          </label>

          <label>
            Contact Title
            <input type="text" v-model="contactTitle" />
          </label>

          <label>
            Phone
            <input type="tel" v-model="phone" autocomplete="tel" />
          </label>

          <label>
            Address
            <input type="text" v-model="address" autocomplete="street-address" />
          </label>

          <label>
            City
            <input type="text" v-model="city" autocomplete="address-level2" />
          </label>

          <label>
            Region
            <input type="text" v-model="region" autocomplete="address-level1" />
          </label>

          <label>
            Postal Code
            <input type="text" v-model="postalCode" autocomplete="postal-code" />
          </label>

          <label>
            Country
            <input type="text" v-model="country" autocomplete="country-name" />
          </label>
        </fieldset>

        <fieldset>
          <legend>Account</legend>

          <label>
            Email
            <input type="email" v-model="email" required autocomplete="email" />
          </label>

          <label>
            Password
            <input type="password" v-model="password" required minlength="8" autocomplete="new-password" />
          </label>

          <label>
            Confirm Password
            <input type="password" v-model="confirmPassword" required autocomplete="new-password" />
          </label>
        </fieldset>

        <p v-if="error" class="form-error">{{ error }}</p>

        <button type="submit" :disabled="submitting">
          {{ submitting ? "Creating account…" : "Sign Up" }}
        </button>

        <p class="form-switch">
          Already have an account?
          <router-link to="/sign-in">Sign in</router-link>
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
const { register } = useAuth();

const companyName = ref("");
const contactName = ref("");
const contactTitle = ref("");
const phone = ref("");
const address = ref("");
const city = ref("");
const region = ref("");
const postalCode = ref("");
const country = ref("");

const email = ref("");
const password = ref("");
const confirmPassword = ref("");
const error = ref("");
const submitting = ref(false);

async function submit() {
  error.value = "";

  if (password.value !== confirmPassword.value) {
    error.value = "Passwords do not match.";
    return;
  }

  submitting.value = true;
  try {
    await register({
      companyName: companyName.value,
      contactName: contactName.value,
      contactTitle: contactTitle.value,
      phone: phone.value,
      address: address.value,
      city: city.value,
      region: region.value,
      postalCode: postalCode.value,
      country: country.value,
      email: email.value,
      password: password.value,
    });
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
  max-width: 640px;
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
