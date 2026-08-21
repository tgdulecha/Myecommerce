import { test, expect } from '@playwright/test';

test.use({ viewport: { width: 375, height: 812 } });

test.describe('Mobile navbar', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('**/api/orders**', (route) => route.fulfill({ status: 403 }));
  });

  test('hides the link list behind a hamburger button', async ({ page }) => {
    await page.goto('/');

    const menu = page.locator('nav ul');
    const hamburger = page.getByRole('button', { name: 'Toggle menu' });

    await expect(hamburger).toBeVisible();
    await expect(menu).toBeHidden();

    await hamburger.click();
    await expect(menu).toBeVisible();
  });

  test('closes the menu after clicking a link inside it', async ({ page }) => {
    await page.goto('/');

    const hamburger = page.getByRole('button', { name: 'Toggle menu' });
    const menu = page.locator('nav ul');

    await hamburger.click();
    await expect(menu).toBeVisible();

    await page.getByRole('link', { name: 'Sign In' }).click();

    await expect(page).toHaveURL('/sign-in');
    await expect(menu).toBeHidden();
  });
});
