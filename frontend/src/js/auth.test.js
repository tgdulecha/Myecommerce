import { describe, it, expect, beforeEach, vi } from 'vitest';

vi.mock('@/services/authService', () => ({
  fetchCurrentAccount: vi.fn(),
  loginAccount: vi.fn(),
  registerAccount: vi.fn(),
}));

import { loginAccount, registerAccount } from '@/services/authService';
import { useAuth, authHeader } from './auth.js';

describe('auth.js', () => {
  beforeEach(() => {
    // auth.js holds module-level singleton state (token/currentAccount), so reset it
    // through the real logout() rather than touching localStorage directly - that
    // keeps the in-memory refs and localStorage in sync between tests.
    useAuth().logout();
    vi.clearAllMocks();
  });

  it('authHeader is empty when logged out', () => {
    expect(authHeader()).toEqual({});
  });

  it('login stores the token and exposes it via authHeader', async () => {
    loginAccount.mockResolvedValue({
      token: 'abc.def.ghi',
      account: { email: 'jane@example.com' },
    });

    const { login, currentAccount } = useAuth();
    await login('jane@example.com', 'password123');

    expect(currentAccount.value.email).toBe('jane@example.com');
    expect(localStorage.getItem('auth_token')).toBe('abc.def.ghi');
    expect(authHeader()).toEqual({ Authorization: 'Bearer abc.def.ghi' });
  });

  it('logout clears the token', async () => {
    loginAccount.mockResolvedValue({ token: 'abc', account: { email: 'jane@example.com' } });

    const { login, logout, currentAccount } = useAuth();
    await login('jane@example.com', 'password123');

    logout();

    expect(currentAccount.value).toBeNull();
    expect(localStorage.getItem('auth_token')).toBeNull();
    expect(authHeader()).toEqual({});
  });

  it('register calls registerAccount then logs in with the same credentials', async () => {
    registerAccount.mockResolvedValue({});
    loginAccount.mockResolvedValue({ token: 'xyz', account: { email: 'new@example.com' } });

    const { register, currentAccount } = useAuth();
    const payload = { email: 'new@example.com', password: 'password123', companyName: 'Acme' };
    await register(payload);

    expect(registerAccount).toHaveBeenCalledWith(payload);
    expect(loginAccount).toHaveBeenCalledWith('new@example.com', 'password123');
    expect(currentAccount.value.email).toBe('new@example.com');
  });
});
