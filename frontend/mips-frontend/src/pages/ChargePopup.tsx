import PortOne from "@portone/browser-sdk/v2"
import { useState } from "react"
import type { UserInfo } from "@/types/User"
import type { paymentStatus } from "@/types/Asset"
import type { Charge, ChargeRequest, ChargeResponse, PortoneResponse } from "@/types/Charge"
import type { Props } from "@/types/Comm"
import { DEFAULT_CHARGE } from "@/types/Charge"
import { handleAuthToken } from '@/api/userApi'
import { handleReqCharge, handleCompleteCharge } from '@/api/PaymentApi'
import { PAYMENT_CONFIG } from "@/config/paymentConfig"



export default function ChargePopup({ onClose }: Props) {
  const [charge, setCharge] = useState<Charge>(DEFAULT_CHARGE)
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null)
  const [paymentStatus, setPaymentStatus] = useState<paymentStatus>({
    status: "IDLE",
    message: undefined,
  })

  const isWaitingPayment = paymentStatus.status !== "IDLE" && paymentStatus.status !== "PAID" && paymentStatus.status !== "FAILED"
  
  const handlePaymentClose = () =>
    setPaymentStatus({ status: "IDLE", message: "" })

  const handleSubmit = async (e: React.SyntheticEvent) => {
    e.preventDefault()
    if (charge.amount <= 0) {
      alert("충전 금액을 입력해주세요.")
      return
    }
    console.log('ENV 전체:', import.meta.env)
    console.log('STORE:', import.meta.env.VITE_KAKAOPAY_STORE_ID, typeof PAYMENT_CONFIG.KAKAOPAY.storeId)

    // 리프레시 토큰으로 사용자 기본정보를 가져옴
    const res = await handleAuthToken()
    if (!res || !res.email)
    {
      alert("사용자정보 조회실패!")
      return
    }
    setUserInfo(res)
    console.log("handleAuthToken-res : ", res, userInfo, userInfo?.email)


    const chargeReqParam: ChargeRequest = {
      chargeId: randomId(),
      paymentId: randomId(),
      storeId: PAYMENT_CONFIG.KAKAOPAY.storeId,
      amount: charge.amount,
      currency: charge.currency,
      email: res.email
    }
    /* 결제 요청 전, 충전 내역 처리 API 호출 */
    const chargeResponse: ChargeResponse | null = await handleReqCharge(chargeReqParam)

    console.log("chargeResponse", chargeResponse)

    // 포트원 결제 요청
    
    if (!chargeResponse)
    {
      alert("충전 시도를 실패하였습니다!")
      return
    }

    setCharge((prev) => ({ chargeId: chargeResponse?.chargeId
                         , status: chargeResponse?.status
                         , amount: prev.amount
                         , currency: prev.currency }))
    setPaymentStatus({ status: "PENDING", message: "" })

    /* 결제 요청 포트원 API 호출 */
    const payment = await PortOne.requestPayment({
      storeId: PAYMENT_CONFIG.KAKAOPAY.storeId,
      channelKey: PAYMENT_CONFIG.KAKAOPAY.channelKey,
      paymentId: chargeResponse?.paymentId,
      orderName: `포인트 ${charge.amount.toLocaleString()}원 충전`,
      totalAmount: charge.amount,
      currency: charge.currency,
      payMethod: "EASY_PAY",
      customData: {
        email: userInfo?.email
      },
    })
    console.log("payment ", payment)

    if (payment === null || payment?.paymentId === undefined) {
      setPaymentStatus({ status: "FAILED", message: "결제 요청이 실패했습니다. 다시 시도해주세요." })
      return
    }
    if (payment?.code !== undefined) {
      setPaymentStatus({ status: "FAILED", message: payment?.message })
      return
    }
    /* 결제 완료 처리 API 호출 */
    const completeResponse: PortoneResponse | null = await handleCompleteCharge(chargeReqParam)
    
    console.log("completeResponse", completeResponse)
    if (completeResponse?.status === "PAID") {
      isWaitingPayment
      setPaymentStatus({ status: completeResponse?.status })
    } else {
      setPaymentStatus({ status: "FAILED", message: "결제 완료 처리에 실패했습니다." })
    }
  }
  // 랜덤 ID 생성 함수
  function randomId() {
    return [...crypto.getRandomValues(new Uint32Array(2))]
      .map((word) => word.toString(16).padStart(8, "0"))
      .join("")
  }

  return (
    <>
      {/* 배경 오버레이 */}
      <div
        className="fixed inset-0 z-40 bg-black/50"
        onClick={onClose}
      />

      {/* 모달 패널 */}
      <div className="fixed inset-0 z-50 flex items-center justify-center pointer-events-none">
        <div
          className="pointer-events-auto w-full max-w-sm rounded-2xl border border-[var(--border)] bg-[var(--bg)] shadow-[var(--shadow)] p-6 flex flex-col gap-4"
          onClick={(e) => e.stopPropagation()}
        >
          {/* 헤더 */}
          <div className="flex items-center justify-between">
            <h2 className="text-base font-semibold text-[var(--text-h)] m-0">충전하기</h2>
            <button
              type="button"
              onClick={onClose}
              className="text-[var(--text)] hover:text-[var(--text-h)] transition text-xl leading-none cursor-pointer"
            >
              ✕
            </button>
          </div>

          {/* 충전 금액 입력 */}
          <div className="flex flex-col gap-1">
            <span className="text-xs text-[var(--text)]">충전 금액</span>
            <div className="flex items-center gap-2 border border-[var(--border)] rounded-lg px-3 py-2 focus-within:border-[var(--accent)]">
              <span className="text-[var(--text)]">₩</span>
              <input
                type="text"
                inputMode="numeric"
                value={charge.amount ? charge.amount.toLocaleString() : ""}
                onChange={(e) => {
                  const raw = e.target.value.replace(/[^0-9]/g, "")
                  setCharge((prev) => ({ ...prev, amount: raw ? Number(raw) : 0 }))
                }}
                placeholder="금액 입력"
                className="flex-1 text-lg font-bold text-[var(--text-h)] bg-transparent border-none outline-none"
              />
            </div>
          </div>

          {/* 결제하기 버튼 */}
          <form onSubmit={handleSubmit}>
            <button
              type="submit"
              aria-busy={isWaitingPayment}
              disabled={isWaitingPayment}
              className="w-full py-3 rounded-lg bg-[var(--accent)] text-white text-sm font-medium hover:opacity-90 disabled:opacity-50 transition cursor-pointer"
            >
              {isWaitingPayment ? "결제 진행 중..." : "결제하기"}
            </button>
          </form>

          {/* 결제 실패 메시지 */}
          {paymentStatus.status === "FAILED" && (
            <div className="rounded-lg bg-red-50 border border-red-200 p-3 text-sm text-red-600 flex items-center justify-between gap-2">
              <span>{paymentStatus.message}</span>
              <button type="button" onClick={handlePaymentClose} className="text-red-400 hover:text-red-600 cursor-pointer">✕</button>
            </div>
          )}

          {/* 결제 성공 메시지 */}
          {paymentStatus.status === "PAID" && (
            <div className="rounded-lg bg-green-50 border border-green-200 p-3 text-sm text-green-600 flex items-center justify-between gap-2">
              <span>결제가 완료되었습니다.</span>
              <button type="button" onClick={onClose} className="text-green-400 hover:text-green-600 cursor-pointer">닫기</button>
            </div>
          )}
        </div>
      </div>
    </>
  )
}
