import { describe, it, expect } from 'vitest';
import { paymentStatusClass } from './paymentService.js';

describe('paymentStatusClass', () => {
  it('returns yellow for a pending payment', () => {
    expect(paymentStatusClass({ status: 'PENDING' })).toBe('btn-yellow');
  });

  it('returns green for a completed payment', () => {
    expect(paymentStatusClass({ status: 'COMPLETED' })).toBe('btn-green');
  });

  it('returns red for a failed payment', () => {
    expect(paymentStatusClass({ status: 'FAILED' })).toBe('btn-red');
  });

  it('returns grey for a refunded payment', () => {
    expect(paymentStatusClass({ status: 'REFUNDED' })).toBe('btn-grey');
  });

  it('returns empty string when there is no payment', () => {
    expect(paymentStatusClass(null)).toBe('');
  });
});
