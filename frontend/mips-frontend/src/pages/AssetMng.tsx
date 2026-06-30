import { useEffect, useState } from "react"
import type { HoldingStock, StockMarket } from "@/types/Asset"
import ChargePopup from "@/pages/ChargePopup"

export default function AssetMng() {
  const [showCharge, setShowCharge] = useState(false)
  const [market, setMarket] = useState<StockMarket>("KR")
  const [holdings, setHoldings] = useState<HoldingStock[]>([])
  const [totalBalance] = useState<number>(0)
  const [totalInvested] = useState<number>(0)
  const [totalKrw] = useState<number>(0)

  useEffect(() => {
    // fetch("/api/asset/holdings")
    //   .then((res) => {
    //     if (!res.ok) throw new Error("Failed to fetch holdings")
    //     return res.json()
    //   })
    //   .then((data: HoldingStock[]) => setHoldings(data))
    //   .catch((err) => {
    //     setHoldings([])
    //     console.error("Failed to fetch holdings:", err)
    //   })
  }, [])

  const filteredHoldings = holdings.filter((h) => h.market === market)

  return (
    <>
      <div className="grid grid-cols-[260px_1fr] gap-5 p-6 items-start">

        {/* ── 왼쪽: 내자산 카드 ── */}
        <div className="rounded-2xl border border-[var(--border)] bg-[var(--bg)] p-6 shadow-sm flex flex-col gap-6">
          <div className="flex flex-col gap-1">
            <span className="text-xs font-medium text-[var(--text)] tracking-wider uppercase">내자산</span>
            <span className="text-xs text-[var(--text)] mt-2">현재금액</span>
            <span className="text-3xl font-bold text-[var(--text-h)]">
              ₩{totalBalance.toLocaleString()}
            </span>
          </div>
          <button
            type="button"
            onClick={() => setShowCharge(true)}
            className="w-full py-2.5 rounded-xl bg-[var(--accent)] text-white text-sm font-medium hover:opacity-90 transition cursor-pointer"
          >
            충전하기
          </button>
        </div>

        {/* ── 오른쪽: 내 주식 잔고 ── */}
        <div className="flex flex-col gap-4">

          {/* 제목 + 국내/해외 토글 */}
          <div className="rounded-2xl border border-[var(--border)] bg-[var(--bg)] px-5 py-4 shadow-sm flex items-center justify-between">
            <h2 className="text-base font-semibold text-[var(--text-h)] m-0 pr-5">내 주식 잔고</h2>
            <div className="flex rounded-lg overflow-hidden border border-[var(--border)]">
              <button
                type="button"
                onClick={() => setMarket("KR")}
                className={`px-5 py-1.5 text-sm font-medium transition cursor-pointer ${
                  market === "KR"
                    ? "bg-[var(--accent)] text-white"
                    : "text-[var(--text)] hover:bg-[var(--accent-bg)]"
                }`}
              >
                국내
              </button>
              <button
                type="button"
                onClick={() => setMarket("US")}
                className={`px-5 py-1.5 text-sm font-medium transition cursor-pointer ${
                  market === "US"
                    ? "bg-[var(--accent)] text-white"
                    : "text-[var(--text)] hover:bg-[var(--accent-bg)]"
                }`}
              >
                해외
              </button>
            </div>
          </div>

          {/* 투자금액 / 원화금액 */}
          <div className="grid grid-cols-2 gap-4">
            <div className="rounded-2xl border border-[var(--border)] bg-[var(--bg)] p-5 shadow-sm">
              <span className="text-xs text-[var(--text)]">투자금액</span>
              <p className="text-2xl font-bold text-[var(--text-h)] mt-1">
                ₩{totalInvested.toLocaleString()}
              </p>
            </div>
            <div className="rounded-2xl border border-[var(--border)] bg-[var(--bg)] p-5 shadow-sm">
              <span className="text-xs text-[var(--text)]">투자 원화금액</span>
              <p className="text-2xl font-bold text-[var(--text-h)] mt-1">
                ₩{totalKrw.toLocaleString()}
              </p>
            </div>
          </div>

        </div>

        {/* ── 투자리스트: 전체 너비 ── */}
        <div className="col-span-2 rounded-2xl border border-[var(--border)] bg-[var(--bg)] shadow-sm overflow-hidden">
          <div className="px-5 py-4 border-b border-[var(--border)] flex items-center justify-between">
            <h3 className="text-sm font-semibold text-[var(--text-h)]">투자리스트</h3>
            <span className="text-xs text-[var(--text)]">
              {filteredHoldings.length}개 종목
            </span>
          </div>
          <div className="overflow-auto">
            <table className="w-full text-sm">
              <thead>
                <tr
                  className="text-xs text-[var(--text)] uppercase tracking-wide"
                  style={{ background: "var(--code-bg)" }}
                >
                  <th className="text-left px-5 py-3 font-medium">종목</th>
                  <th className="text-right px-5 py-3 font-medium">수량</th>
                  <th className="text-right px-5 py-3 font-medium">평균단가</th>
                  <th className="text-right px-5 py-3 font-medium">현재가</th>
                  <th className="text-right px-5 py-3 font-medium">평가손익</th>
                </tr>
              </thead>
              <tbody>
                {filteredHoldings.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="text-center py-14 text-[var(--text)] text-sm">
                      보유 종목이 없습니다.
                    </td>
                  </tr>
                ) : (
                  filteredHoldings.map((stock) => {
                    const pnl = (stock.currentPrice - stock.avgPrice) * stock.quantity
                    const pnlRate = ((stock.currentPrice - stock.avgPrice) / stock.avgPrice) * 100
                    const isProfit = pnl >= 0
                    return (
                      <tr
                        key={stock.ticker}
                        className="border-t border-[var(--border)] hover:bg-[var(--accent-bg)] transition"
                      >
                        <td className="px-5 py-3.5">
                          <div className="font-semibold text-[var(--text-h)]">{stock.ticker}</div>
                          <div className="text-xs text-[var(--text)] mt-0.5">{stock.name}</div>
                        </td>
                        <td className="px-5 py-3.5 text-right tabular-nums text-[var(--text-h)]">
                          {stock.quantity.toLocaleString()}
                        </td>
                        <td className="px-5 py-3.5 text-right tabular-nums text-[var(--text-h)]">
                          {stock.avgPrice.toLocaleString()}
                        </td>
                        <td className="px-5 py-3.5 text-right tabular-nums text-[var(--text-h)]">
                          {stock.currentPrice.toLocaleString()}
                        </td>
                        <td className="px-5 py-3.5 text-right">
                          <span className={`font-semibold tabular-nums ${isProfit ? "text-red-500" : "text-blue-500"}`}>
                            {isProfit ? "+" : ""}{pnl.toLocaleString()}
                          </span>
                          <div className={`text-xs tabular-nums mt-0.5 ${isProfit ? "text-red-400" : "text-blue-400"}`}>
                            {isProfit ? "+" : ""}{pnlRate.toFixed(2)}%
                          </div>
                        </td>
                      </tr>
                    )
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>

      </div>

      {showCharge && <ChargePopup onClose={() => setShowCharge(false)} />}
    </>
  )
}
