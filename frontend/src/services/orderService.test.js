import { describe, it, expect } from 'vitest';
import { isShipped } from './orderService.js';

describe('isShipped', () => {
  it('returns false when there is no shippedDate', () => {
    expect(isShipped({ shippedDate: null })).toBe(false);
  });

  it('returns true when the shipped date is in the past', () => {
    expect(isShipped({ shippedDate: '2000-01-01' })).toBe(true);
  });

  it('returns false when the shipped date is in the future', () => {
    const future = new Date();
    future.setFullYear(future.getFullYear() + 1);
    expect(isShipped({ shippedDate: future.toISOString() })).toBe(false);
  });
});
