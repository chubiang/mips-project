import type { ChargeRequest, ChargeResponse } from "@/types/Charge"
import { fetchViaWorker } from '@/api/authWorkerClient'

   
export const handleReqCharge = async (charge: ChargeRequest): Promise<ChargeResponse | null> => {
    const response = await fetchViaWorker("/api/charge/create", {
      method: "POST",
      credentials: 'include',
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        chargeId: charge.chargeId,
        paymentId: charge.paymentId,
        storeId: charge.storeId,
        amount: charge.amount,
        currency: charge.currency,
        email: charge.email
    }),
  })
  if (response?.data) {
    return {
      chargeId: response.data.chargeId,
      transactionId: response.data.transactionId,
      paymentId: response.data.paymentId,
      email: response.data.email,
      amount: response.data.amount,
      currency: response.data.currency,
      status: response.data.status,
    }
  }

  return null
}