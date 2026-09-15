import { useEffect, useMemo, useRef, useState, type ChangeEvent } from 'react'
import { AlertCircle, ArrowDown, ArrowUp, Clock, Radio, Search } from 'lucide-react'
import { fetchRealtimeUsStocks, getRealtimeStockStreamUrl } from '@/api/stockApi'
import type { RealtimeQuoteEvent, RealtimeStockQuote } from '@/types/Stock'

function formatPrice(value: number) {
  return `$${value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 4 })}`
}

function formatQuotedAt(value: string) {
  return new Date(value).toLocaleString('ko-KR', {
    month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit',
  })
}

export default function UsStock() {
  const [quotes, setQuotes] = useState<RealtimeStockQuote[]>([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [streamStatus, setStreamStatus] = useState<'connecting' | 'open' | 'error'>('connecting')
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let active = true
    let eventSource: EventSource | null = null

    const connect = () => {
      eventSource = new EventSource(getRealtimeStockStreamUrl())
      eventSource.onopen = () => active && setStreamStatus('open')
      eventSource.onerror = () => active && setStreamStatus('error')
      eventSource.addEventListener('quote', event => {
        if (!active) return

        let update: RealtimeQuoteEvent
        try {
          update = JSON.parse(event.data) as RealtimeQuoteEvent
        } catch {
          setError('실시간 시세 메시지 형식이 올바르지 않습니다.')
          return
        }
        setQuotes(current => {
          const index = current.findIndex(quote => quote.ticker === update.ticker)
          if (index < 0) {
            return [...current, {
              ...update,
              companyName: update.ticker,
              assetType: null,
            }].sort((a, b) => a.ticker.localeCompare(b.ticker))
          }

          const next = [...current]
          next[index] = { ...next[index], ...update }
          return next
        })
      })
    }

    const initialize = async () => {
      try {
        const data = await fetchRealtimeUsStocks()
        if (!active) return
        setQuotes(Array.isArray(data) ? data : [])
        setError(null)
      } catch {
        if (active) setError('실시간 시세를 불러오지 못했습니다.')
      } finally {
        if (active) {
          setLoading(false)
          connect()
        }
      }
    }

    void initialize()
    return () => {
      active = false
      eventSource?.close()
    }
  }, [])

  const filteredQuotes = useMemo(() => {
    const keyword = search.trim().toLowerCase()
    if (!keyword) return quotes
    return quotes.filter(quote =>
      quote.ticker.toLowerCase().includes(keyword)
      || quote.companyName.toLowerCase().includes(keyword),
    )
  }, [quotes, search])

  const latestQuotedAt = useMemo(() => {
    if (!Array.isArray(quotes) || quotes.length === 0) return null

    return quotes.reduce<string | null>((latest, quote) => {
      const quotedAtMillis = Date.parse(quote.quotedAt)
      if (Number.isNaN(quotedAtMillis)) return latest

      const latestMillis = latest ? Date.parse(latest) : Number.NEGATIVE_INFINITY
      return quotedAtMillis > latestMillis ? quote.quotedAt : latest
    }, null)
  }, [quotes])

  return (
    <div className="space-y-5 text-left">
      <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-bold text-slate-800">나스닥 실시간 시세</h1>
            <span className="rounded-full bg-emerald-100 px-2 py-0.5 text-xs font-semibold text-emerald-700">LIVE</span>
          </div>
          <p className="mt-1 text-sm text-slate-500">Finnhub 체결가 기준 · SSE 실시간 수신</p>
        </div>
        <div className="flex items-center gap-1.5 text-xs text-slate-400">
          <Radio size={13} className={streamStatus === 'open' ? 'text-emerald-500' : streamStatus === 'error' ? 'text-red-500' : 'animate-pulse'} />
          <span>{streamStatus === 'open' ? '실시간 연결됨' : streamStatus === 'error' ? '재연결 중' : '연결 중'}</span>
          <span>·</span>
          {latestQuotedAt ? `최근 체결 ${formatQuotedAt(latestQuotedAt)}` : '시세 대기 중'}
        </div>
      </div>

      <div className="rounded-xl border border-slate-200 bg-white p-4">
        <div className="relative">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="search"
            value={search}
            onChange={(event: ChangeEvent<HTMLInputElement>) => setSearch(event.target.value)}
            placeholder="종목명 또는 티커 검색 (예: AAPL, Apple)"
            className="w-full rounded-lg border border-slate-200 py-2 pl-9 pr-4 text-sm text-slate-700 focus:border-transparent focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>

      {error && (
        <div className="flex items-center gap-2 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
          <AlertCircle size={17} />
          {error} 기존 시세가 있으면 마지막 값은 유지됩니다.
        </div>
      )}

      <div className="overflow-hidden rounded-xl border border-slate-200 bg-white">
        <div className="border-b border-slate-100 px-4 py-2.5 text-xs text-slate-500">{filteredQuotes.length}개 종목</div>
        {loading ? (
          <div className="flex items-center justify-center py-24 text-sm text-slate-400">
            <div className="mr-3 h-6 w-6 animate-spin rounded-full border-2 border-blue-500 border-t-transparent" />
            실시간 시세를 불러오는 중...
          </div>
        ) : filteredQuotes.length === 0 ? (
          <div className="py-20 text-center text-sm text-slate-400">
            {search ? '검색 결과가 없습니다.' : 'Redis에 수신된 실시간 시세가 없습니다.'}
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[620px] text-sm">
              <thead className="border-b border-slate-200 bg-slate-50 text-xs uppercase tracking-wide text-slate-500">
                <tr>
                  <th className="px-4 py-3 text-left">티커</th>
                  <th className="px-4 py-3 text-left">종목명</th>
                  <th className="px-4 py-3 text-left">구분</th>
                  <th className="px-4 py-3 text-right">현재 체결가</th>
                  <th className="px-4 py-3 text-right">체결 시각</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredQuotes.map(quote => <RealtimeStockRow key={quote.ticker} quote={quote} />)}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

function RealtimeStockRow({ quote }: { quote: RealtimeStockQuote }) {
  const previousPrice = useRef(quote.currentPrice)
  const [direction, setDirection] = useState<'up' | 'down' | null>(null)

  useEffect(() => {
    const previous = previousPrice.current
    previousPrice.current = quote.currentPrice
    if (quote.currentPrice === previous) return

    setDirection(quote.currentPrice > previous ? 'up' : 'down')
    const timer = window.setTimeout(() => setDirection(null), 900)
    return () => window.clearTimeout(timer)
  }, [quote.currentPrice])

  const highlight = direction === 'up'
    ? 'bg-emerald-50'
    : direction === 'down'
      ? 'bg-red-50'
      : ''
  const priceColor = direction === 'up'
    ? 'text-emerald-600'
    : direction === 'down'
      ? 'text-red-500'
      : 'text-slate-900'

  return (
    <tr className={`${highlight} transition-colors duration-700 hover:bg-slate-50`}>
      <td className="px-4 py-3 font-mono font-bold text-slate-800">{quote.ticker}</td>
      <td className="px-4 py-3 text-slate-700">{quote.companyName}</td>
      <td className="px-4 py-3">
        <span className="rounded bg-blue-100 px-2 py-0.5 text-xs font-semibold text-blue-700">
          {quote.assetType ?? '미분류'}
        </span>
      </td>
      <td className={`px-4 py-3 text-right font-semibold tabular-nums transition-colors ${priceColor}`}>
        <span className="inline-flex items-center justify-end gap-1">
          {direction === 'up' && <ArrowUp size={14} />}
          {direction === 'down' && <ArrowDown size={14} />}
          {formatPrice(quote.currentPrice)}
        </span>
      </td>
      <td className="px-4 py-3 text-right text-xs text-slate-500">
        <span className="inline-flex items-center gap-1"><Clock size={12} />{formatQuotedAt(quote.quotedAt)}</span>
      </td>
    </tr>
  )
}
