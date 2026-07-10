import type { ChargeRequest, ChargeResponse, PortoneResponse } from "@/types/Charge"
import type { AccountInfo } from "@/types/Asset"
import { fetchViaWorker } from '@/api/authWorkerClient'

// 충전요청 처리 API 호출   
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

// 충전완료 처리 API 호출
export const handleCompleteCharge = async (charge: ChargeRequest): Promise<PortoneResponse | null> => {
  const response = await fetchViaWorker("/api/pay/complete", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ 
                              paymentId: charge.paymentId,
                              chargeId: charge.chargeId,
                              storeId: charge.storeId,
                              email: charge.email,
                            }),
  })
  if (response?.data) {
    return {
      type: response.data.type,
      message: response.data.message, 
      id: response.data.id,
      status: response.data.status,
      storeId: response.data.storeId,
      billKey: response.data.billKey,
      paymentId: response.data.paymentId,
      transactionId: response.data.transactionId,
      orderName: response.data.orderName,
      totalAmount: response.data.totalAmount,
      channel: response.data.channel,
      payMethod: response.data.payMethod,
      currency: response.data.currency,
      version: response.data.version,
      pgTxId: response.data.pgTxId,
      requestedAt: response.data.requestedAt,
      createdAt: response.data.createdAt,
      updatedAt: response.data.updatedAt,
      statusChangedAt: response.data.statusChangedAt,
      paidAt: response.data.paidAt,
      cancelAt: response.data.cancelAt,
      failedAt: response.data.failedAt,
    }
  }
  return null;
}

// 사용자 계정 계좌 정보 조회
export const handleGetAccountInfo = async (): Promise<AccountInfo | null> => {
  const response = await fetchViaWorker(`/api/acc/get`, {
        method: "GET",  
      headers: { "Content-Type": "application/json" },
  })
  if (response?.data) {
    return {
      email: response.data.email,
      balance: response.data.balance,
      currency: response.data.currency,
      lockCashes: response.data.lockCashes,
    }
  }
  return null;
}
