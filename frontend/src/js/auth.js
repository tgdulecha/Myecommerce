import { ref } from "vue";
import {
  fetchCurrentAccount,
  loginAccount,
  registerAccount,
} from "@/services/authService";

const TOKEN_KEY = "auth_token";

const token = ref(localStorage.getItem(TOKEN_KEY));
const currentAccount = ref(null);
const authChecked = ref(false);

function setToken(value) {
  token.value = value;
  if (value) localStorage.setItem(TOKEN_KEY, value);
  else localStorage.removeItem(TOKEN_KEY);
}

async function checkSession() {
  if (!token.value) {
    authChecked.value = true;
    return;
  }

  try {
    currentAccount.value = await fetchCurrentAccount(token.value);
  } catch {
    currentAccount.value = null;
    setToken(null);
  } finally {
    authChecked.value = true;
  }
}

async function login(email, password) {
  const result = await loginAccount(email, password);
  setToken(result.token);
  currentAccount.value = result.account;
}

async function register(payload) {
  await registerAccount(payload);
  await login(payload.email, payload.password);
}

function logout() {
  setToken(null);
  currentAccount.value = null;
}

export function useAuth() {
  return { currentAccount, authChecked, checkSession, login, register, logout };
}

// Used by other services to attach the bearer token to their requests
export function authHeader() {
  return token.value ? { Authorization: `Bearer ${token.value}` } : {};
}
