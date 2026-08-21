import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import { createRouter, createWebHistory } from 'vue-router';
import SignUp from './SignUp.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: { template: '<div />' } },
    { path: '/sign-in', component: { template: '<div />' } },
  ],
});

describe('SignUp.vue', () => {
  it('shows an error and does not submit when passwords do not match', async () => {
    const wrapper = mount(SignUp, {
      global: { plugins: [router] },
    });

    await wrapper.find('input[type="email"]').setValue('jane@example.com');

    const passwordInputs = wrapper.findAll('input[type="password"]');
    await passwordInputs[0].setValue('supersecret1');
    await passwordInputs[1].setValue('a-different-password');

    await wrapper.find('form').trigger('submit.prevent');

    expect(wrapper.text()).toContain('Passwords do not match.');
  });
});
