import { useState, useMemo, useEffect } from 'react'
import { Search, ChevronUp, ChevronDown, ChevronsUpDown, Clock, AlertCircle } from 'lucide-react'
import { fetchAllUsStock } from '@/api/stockApi'
import type {
  UsTopStock,
  StockFilter,
  StockSort,
  SortField,
  AssetType,
} from '@/types/Stock'

// ---------------------------------------------------------------------------
// 유틸 함수
// ---------------------------------------------------------------------------
function formatPrice(v: number) {
  return `$${v.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

function formatUpdatedAt(iso: string) {
  return new Date(iso).toLocaleString('ko-KR', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit',
  })
}

// ---------------------------------------------------------------------------
// 서브 컴포넌트
// ---------------------------------------------------------------------------
function AssetTypeBadge({ type }: { type: AssetType }) {
  const styles: Record<AssetType, string> = {
    STOCK: 'bg-blue-100 text-blue-600',
    ETF:   'bg-green-100 text-green-700',
    INDEX: 'bg-slate-100 text-slate-600',
  }
  const labels: Record<AssetType, string> = { STOCK: '주식', ETF: 'ETF', INDEX: '지수' }
  return (
    <span className={`px-2 py-0.5 rounded text-xs font-semibold whitespace-nowrap ${styles[type]}`}>
      {labels[type]}
    </span>
  )
}

function SortIcon({ field, sort }: { field: SortField; sort: StockSort }) {
  if (sort.field !== field) return <ChevronsUpDown size={14} className="text-slate-400" />
  return sort.order === 'asc'
    ? <ChevronUp size={14} className="text-blue-600" />
    : <ChevronDown size={14} className="text-blue-600" />
}

function SortableHeader({
  label, field, sort, onSort, className = '',
}: {
  label: string
  field: SortField
  sort: StockSort
  onSort: (f: SortField) => void
  className?: string
}) {
  return (
    <th
      className={`px-4 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide cursor-pointer select-none hover:bg-slate-100 transition-colors ${className}`}
      onClick={() => onSort(field)}
    >
      <div className="flex items-center gap-1">
        {label}
        <SortIcon field={field} sort={sort} />
      </div>
    </th>
  )
}

// ---------------------------------------------------------------------------
// 메인 컴포넌트
// ---------------------------------------------------------------------------
export default function UsStock() {
  const [data, setData] = useState<UsTopStock[] | null>(null)
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)
  const [filter, setFilter] = useState<StockFilter>({ search: '', assetType: 'ALL' })
  const [sort, setSort] = useState<StockSort>({ field: 'price', order: 'desc' })

  useEffect(() => {
    fetchAllUsStock()
      .then(setData)
      .catch(() => setError('데이터를 불러오는 중 오류가 발생했습니다.'))
      .finally(() => setLoading(false))
  }, [])

  const filtered = useMemo(() => {
    if (!data) return []
    let list = [...data]

    if (filter.search) {
      const q = filter.search.toLowerCase()
      list = list.filter(
        s =>
          s.ticker.toLowerCase().includes(q) ||
          s.companyName.toLowerCase().includes(q),
      )
    }

    if (filter.assetType !== 'ALL') {
      list = list.filter(s => s.assetType === filter.assetType)
    }

    return list.sort((a, b) => {
      const av = a[sort.field]
      const bv = b[sort.field]
      const dir = sort.order === 'asc' ? 1 : -1
      return (typeof av === 'string' ? av.localeCompare(bv as string) : (av as number) - (bv as number)) * dir
    })
  }, [data, filter, sort])

  function handleSort(field: SortField) {
    setSort(prev => ({
      field,
      order: prev.field === field && prev.order === 'asc' ? 'desc' : 'asc',
    }))
  }

  const filterButtons: { label: string; value: StockFilter['assetType'] }[] = [
    { label: '전체',  value: 'ALL' },
    { label: '주식',  value: 'STOCK' },
    { label: 'ETF',   value: 'ETF' },
  ]

  const updatedAt = data?.[0]?.updatedAt

  return (
    <div className="space-y-5">
      {/* 페이지 헤더 */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">미국 주식</h1>
          <p className="text-sm text-slate-500 mt-0.5">시가총액 기준 상위 종목</p>
        </div>
        {updatedAt && (
          <div className="flex items-center gap-1.5 text-xs text-slate-400">
            <Clock size={13} />
            <span>최종 갱신: {formatUpdatedAt(updatedAt)}</span>
          </div>
        )}
      </div>

      {/* 필터 바 */}
      <div className="bg-white rounded-xl border border-slate-200 p-4 flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="종목명 또는 종목코드 검색 (예: AAPL, Apple)"
            value={filter.search}
            onChange={e => setFilter(prev => ({ ...prev, search: e.target.value }))}
            className="w-full pl-9 pr-4 py-2 rounded-lg border border-slate-200 text-sm text-slate-700 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div className="flex gap-1.5">
          {filterButtons.map(btn => (
            <button
              key={btn.value}
              onClick={() => setFilter(prev => ({ ...prev, assetType: btn.value }))}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                filter.assetType === btn.value
                  ? 'bg-blue-600 text-white shadow-sm'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {btn.label}
            </button>
          ))}
        </div>
      </div>

      {/* 결과 영역 */}
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        {!loading && !error && (
          <div className="px-4 py-2.5 border-b border-slate-100 text-xs text-slate-500">
            {filtered.length}개 종목
            {(filter.search || filter.assetType !== 'ALL') && (
              <button
                onClick={() => setFilter({ search: '', assetType: 'ALL' })}
                className="ml-2 text-blue-500 hover:underline"
              >
                필터 초기화
              </button>
            )}
          </div>
        )}

        {loading && (
          <div className="flex items-center justify-center py-24 text-slate-400 text-sm">
            <div className="animate-spin rounded-full h-6 w-6 border-2 border-blue-500 border-t-transparent mr-3" />
            데이터를 불러오는 중...
          </div>
        )}

        {error && (
          <div className="flex items-center justify-center gap-2 py-24 text-red-500 text-sm">
            <AlertCircle size={18} />
            {error}
          </div>
        )}

        {!loading && !error && (
          <div className="overflow-x-auto">
            <table className="min-w-[640px] w-full text-sm">
              <thead className="bg-slate-50 border-b border-slate-200">
                <tr>
                  <th className="px-3 py-2 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide w-10">#</th>
                  <SortableHeader label="종목코드" field="ticker"      sort={sort} onSort={handleSort} className="w-20" />
                  <SortableHeader label="종목명"   field="companyName" sort={sort} onSort={handleSort} className="min-w-[140px]" />
                  <th className="px-3 py-2 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide hidden sm:table-cell w-16">구분</th>
                  <th className="px-3 py-2 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide hidden lg:table-cell">섹터</th>
                  <SortableHeader label="현재가"   field="price"        sort={sort} onSort={handleSort} className="text-right" />
                  <SortableHeader label="전일대비"  field="changeRate"   sort={sort} onSort={handleSort} className="text-right" />
                  <th className="px-3 py-2 text-right text-xs font-semibold text-slate-500 uppercase tracking-wide hidden md:table-cell">시가</th>
                  <th className="px-3 py-2 text-right text-xs font-semibold text-slate-500 uppercase tracking-wide hidden xl:table-cell whitespace-nowrap">고가 / 저가</th>
                  <th className="px-3 py-2 text-right text-xs font-semibold text-slate-500 uppercase tracking-wide hidden md:table-cell whitespace-nowrap">전일종가</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filtered.length === 0 ? (
                  <tr>
                    <td colSpan={10} className="py-16 text-center text-slate-400 text-sm">
                      검색 결과가 없습니다.
                    </td>
                  </tr>
                ) : (
                  filtered.map((stock, idx) => (
                    <StockRow key={stock.ticker} stock={stock} index={idx + 1} />
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

function StockRow({ stock, index }: { stock: UsTopStock; index: number }) {
  const isPositive = stock.changeAmount >= 0
  const changeColor = isPositive ? 'text-green-600' : 'text-red-500'
  const changeSign  = isPositive ? '+' : ''

  return (
    <tr className="hover:bg-slate-50 transition-colors">
      <td className="px-3 py-2 text-slate-400 text-xs">{index}</td>
      <td className="px-3 py-2 font-bold text-slate-800 font-mono whitespace-nowrap">{stock.ticker}</td>
      <td className="px-3 py-2 text-slate-700">
        <span className="block max-w-[200px] truncate">{stock.companyName}</span>
      </td>
      <td className="px-3 py-2 hidden sm:table-cell">
        <AssetTypeBadge type={stock.assetType} />
      </td>
      <td className="px-3 py-2 text-slate-500 hidden lg:table-cell">{stock.sector}</td>
      <td className="px-3 py-2 text-right font-semibold text-slate-800 whitespace-nowrap">{formatPrice(stock.price)}</td>
      <td className={`px-3 py-2 text-right font-medium whitespace-nowrap ${changeColor}`}>
        <div>{changeSign}{formatPrice(stock.changeAmount)}</div>
        <div className="text-xs">{changeSign}{stock.changeRate.toFixed(2)}%</div>
      </td>
      <td className="px-3 py-2 text-right text-slate-600 whitespace-nowrap hidden md:table-cell">{formatPrice(stock.openPrice)}</td>
      <td className="px-3 py-2 text-right text-slate-600 whitespace-nowrap hidden xl:table-cell">
        <span className="text-green-600">{formatPrice(stock.highPrice)}</span>
        <span className="text-slate-300 mx-1">/</span>
        <span className="text-red-500">{formatPrice(stock.lowPrice)}</span>
      </td>
      <td className="px-3 py-2 text-right text-slate-600 whitespace-nowrap hidden md:table-cell">{formatPrice(stock.prevClose)}</td>
    </tr>
  )
}
