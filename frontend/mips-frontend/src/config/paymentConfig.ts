// src/config/paymentConfig.ts

export type PaymentType =
  | 'CARD'
  | 'KAKAOPAY'
  | 'NICEPAY'
  | 'NAVERPAY'
  | 'TOSSPAY';

interface PaymentConfig {
  storeId: string;
  channelKey: string;
}

export const PAYMENT_CONFIG = {
  CARD: {
    storeId: import.meta.env.VITE_CARD_STORE_ID,
    channelKey: import.meta.env.VITE_CARD_CHANNEL_KEY,
  },
  KAKAOPAY: {
    storeId: import.meta.env.VITE_KAKAOPAY_STORE_ID,
    channelKey: import.meta.env.VITE_KAKAOPAY_CHANNEL_KEY,
  },
  NICEPAY: {
    storeId: import.meta.env.VITE_NICEPAY_STORE_ID,
    channelKey: import.meta.env.VITE_NICEPAY_CHANNEL_KEY,
  },
  NAVERPAY: {
    storeId: import.meta.env.VITE_NAVERPAY_STORE_ID,
    channelKey: import.meta.env.VITE_NAVERPAY_CHANNEL_KEY,
  },
  TOSSPAY: {
    storeId: import.meta.env.VITE_TOSSPAY_STORE_ID,
    channelKey: import.meta.env.VITE_TOSSPAY_CHANNEL_KEY,
  },
} satisfies Record<PaymentType, PaymentConfig>;