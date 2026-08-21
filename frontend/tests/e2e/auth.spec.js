import { test, expect } from '@playwright/test';

// These tests exercise the frontend's auth flow in isolation - the backend calls
// (/api/auth/*, /api/orders) are mocked via page.route() so nothing here depends on
// a live Spring Boot instance or the Northwind database being reachable.

const ACCOUNT = {
  accountID: 1,
  email: 'jane@example.com',
  role: 'Customer',
  customerID: 'JANEE',
  employeeID: null,
  verified: false,
  createdAt: '2026-01-01T00:00:00',
  lastLogin: '2026-01-01T00:00:00',
};

async function mockOrdersAsForbidden(page) {
  // Home.vue fetches orders on mount regardless of auth state; keep it from
  // hitting a real (or absent) backend and slowing the test down.
  await page.route('**/api/orders**', (route) => route.fulfill({ status: 403 }));
}

test.describe('Sign up', () => {
  test('registers, logs in, and lands on home as the new account', async ({ page }) => {
    await mockOrdersAsForbidden(page);

    await page.route('**/api/auth/register', (route) =>
      route.fulfill({ status: 201, contentType: 'application/json', body: JSON.stringify(ACCOUNT) })
    );
    await page.route('**/api/auth/login', (route) =>
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ token: 'fake.jwt.token', account: ACCOUNT }),
      })
    );

    await page.goto('/sign-up');

    await page.getByLabel('Company Name').fill('Acme Corp');
    await page.getByLabel('Email').fill(ACCOUNT.email);
    await page.getByLabel('Password', { exact: true }).fill('supersecret1');
    await page.getByLabel('Confirm Password').fill('supersecret1');

    await page.getByRole('button', { name: 'Sign Up' }).click();

    await expect(page).toHaveURL('/');
    await expect(page.getByText(ACCOUNT.email)).toBeVisible();
  });

  test('shows an error when passwords do not match', async ({ page }) => {
    await page.goto('/sign-up');

    await page.getByLabel('Company Name').fill('Acme Corp');
    await page.getByLabel('Email').fill(ACCOUNT.email);
    await page.getByLabel('Password', { exact: true }).fill('supersecret1');
    await page.getByLabel('Confirm Password').fill('different');

    await page.getByRole('button', { name: 'Sign Up' }).click();

    await expect(page.getByText('Passwords do not match.')).toBeVisible();
  });
});

test.describe('Sign in', () => {
  test('logs in with valid credentials and shows the account in the navbar', async ({ page }) => {
    await mockOrdersAsForbidden(page);

    await page.route('**/api/auth/login', (route) =>
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ token: 'fake.jwt.token', account: ACCOUNT }),
      })
    );

    await page.goto('/sign-in');
    await page.getByLabel('Email').fill(ACCOUNT.email);
    await page.getByLabel('Password').fill('supersecret1');
    await page.getByRole('button', { name: 'Sign In' }).click();

    await expect(page).toHaveURL('/');
    await expect(page.getByText(ACCOUNT.email)).toBeVisible();
  });

  test('shows the server error on invalid credentials', async ({ page }) => {
    await page.route('**/api/auth/login', (route) =>
      route.fulfill({ status: 401, contentType: 'text/plain', body: 'Invalid email or password.' })
    );

    await page.goto('/sign-in');
    await page.getByLabel('Email').fill(ACCOUNT.email);
    await page.getByLabel('Password').fill('wrongpassword');
    await page.getByRole('button', { name: 'Sign In' }).click();

    await expect(page.getByText('Invalid email or password.')).toBeVisible();
  });
});

test.describe('Session restore', () => {
  test('restores a logged-in session from a valid stored token', async ({ page }) => {
    await mockOrdersAsForbidden(page);
    await page.route('**/api/auth/me', (route) =>
      route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(ACCOUNT) })
    );

    await page.addInitScript(() => localStorage.setItem('auth_token', 'fake.jwt.token'));
    await page.goto('/');

    await expect(page.getByText(ACCOUNT.email)).toBeVisible();
  });

  test('drops back to signed-out when the stored token is expired/invalid', async ({ page }) => {
    await mockOrdersAsForbidden(page);
    await page.route('**/api/auth/me', (route) => route.fulfill({ status: 401 }));

    await page.addInitScript(() => localStorage.setItem('auth_token', 'expired.jwt.token'));
    await page.goto('/');

    await expect(page.getByRole('link', { name: 'Sign In' })).toBeVisible();
    await expect(page.getByRole('link', { name: 'Sign Up' })).toBeVisible();

    const token = await page.evaluate(() => localStorage.getItem('auth_token'));
    expect(token).toBeNull();
  });
});

test.describe('Sign out', () => {
  test('clears the session and returns to the signed-out home page', async ({ page }) => {
    await mockOrdersAsForbidden(page);
    await page.route('**/api/auth/me', (route) =>
      route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(ACCOUNT) })
    );

    // Seed the token once via evaluate() rather than addInitScript(), since
    // addInitScript re-runs on every navigation - including the hard reload
    // that Sign Out triggers - which would re-seed the token right after logout clears it.
    await page.goto('/');
    await page.evaluate(() => localStorage.setItem('auth_token', 'fake.jwt.token'));
    await page.reload();
    await expect(page.getByText(ACCOUNT.email)).toBeVisible();

    await page.getByRole('link', { name: 'Sign Out' }).click();

    await expect(page.getByRole('link', { name: 'Sign In' })).toBeVisible();
    const token = await page.evaluate(() => localStorage.getItem('auth_token'));
    expect(token).toBeNull();
  });
});
