import { useEffect, useState } from 'react'
import { dashboardApi } from '../api/services'
import Spinner from '../components/common/Spinner'
import {
  AreaChart, Area, BarChart, Bar,
  XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid, Legend
} from 'recharts'
import styles from './DashboardPage.module.css'

const fmt = n => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(n)

export default function DashboardPage() {
  const [data,    setData]    = useState(null)
  const [loading, setLoading] = useState(true)
  const [error,   setError]   = useState('')

  useEffect(() => {
    dashboardApi.summary()
      .then(r => setData(r.data))
      .catch(() => setError('Failed to load dashboard'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div style={{ padding: 40 }}><Spinner /></div>
  if (error)   return <div className={styles.error}>{error}</div>

  const trends = (data.monthlyTrends || []).map(t => ({
    month: t.month,
    Income:  Number(t.income),
    Expense: Number(t.expense),
  }))

  const incomeCategories  = Object.entries(data.incomeByCategory  || {}).map(([k,v]) => ({ name: k, value: Number(v) }))
  const expenseCategories = Object.entries(data.expenseByCategory || {}).map(([k,v]) => ({ name: k, value: Number(v) }))

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h1 className={styles.title}>Dashboard</h1>
        <span className={styles.subtitle}>Financial overview</span>
      </header>

      {/* KPI cards */}
      <div className={styles.kpis}>
        <KpiCard label="Total Income"   value={fmt(data.totalIncome)}   color="var(--income)"  icon="↑" delay={0} />
        <KpiCard label="Total Expenses" value={fmt(data.totalExpenses)} color="var(--expense)" icon="↓" delay={60} />
        <KpiCard label="Net Balance"    value={fmt(data.netBalance)}    color={data.netBalance >= 0 ? 'var(--accent2)' : 'var(--danger)'} icon="=" delay={120} />
      </div>

      {/* Charts row */}
      <div className={styles.charts}>
        <div className={styles.chartCard}>
          <h2 className={styles.chartTitle}>Monthly Trends</h2>
          <ResponsiveContainer width="100%" height={220}>
            <AreaChart data={trends}>
              <defs>
                <linearGradient id="gIncome"  x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%"  stopColor="#00e5a0" stopOpacity={.3}/>
                  <stop offset="95%" stopColor="#00e5a0" stopOpacity={0}/>
                </linearGradient>
                <linearGradient id="gExpense" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%"  stopColor="#ff4466" stopOpacity={.3}/>
                  <stop offset="95%" stopColor="#ff4466" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,.05)" />
              <XAxis dataKey="month" tick={{ fill: '#6b7280', fontSize: 11 }} />
              <YAxis tick={{ fill: '#6b7280', fontSize: 11 }} />
              <Tooltip contentStyle={{ background: '#111318', border: '1px solid #23283a', borderRadius: 8 }} />
              <Legend />
              <Area type="monotone" dataKey="Income"  stroke="#00e5a0" fill="url(#gIncome)"  strokeWidth={2} />
              <Area type="monotone" dataKey="Expense" stroke="#ff4466" fill="url(#gExpense)" strokeWidth={2} />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        <div className={styles.chartCard}>
          <h2 className={styles.chartTitle}>By Category</h2>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={[...incomeCategories.slice(0,6)]}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,.05)" />
              <XAxis dataKey="name" tick={{ fill: '#6b7280', fontSize: 11 }} />
              <YAxis tick={{ fill: '#6b7280', fontSize: 11 }} />
              <Tooltip contentStyle={{ background: '#111318', border: '1px solid #23283a', borderRadius: 8 }} />
              <Bar dataKey="value" fill="#00e5a0" radius={[4,4,0,0]} name="Income" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Recent activity */}
      <div className={styles.recent}>
        <h2 className={styles.sectionTitle}>Recent Activity</h2>
        <div className={styles.table}>
          <div className={styles.thead}>
            <span>Date</span><span>Category</span><span>Type</span><span>Amount</span><span>Notes</span>
          </div>
          {(data.recentActivity || []).map(r => (
            <div key={r.id} className={styles.trow}>
              <span className={styles.mono}>{r.date}</span>
              <span>{r.category}</span>
              <span className={r.type === 'INCOME' ? styles.income : styles.expense}>{r.type}</span>
              <span className={`${styles.mono} ${r.type === 'INCOME' ? styles.income : styles.expense}`}>
                {r.type === 'INCOME' ? '+' : '-'}{fmt(r.amount)}
              </span>
              <span className={styles.muted}>{r.notes || '—'}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

function KpiCard({ label, value, color, icon, delay }) {
  return (
    <div className={styles.kpi} style={{ animationDelay: `${delay}ms`, '--accent-color': color }}>
      <div className={styles.kpiIcon} style={{ color }}>{icon}</div>
      <div className={styles.kpiValue} style={{ color }}>{value}</div>
      <div className={styles.kpiLabel}>{label}</div>
    </div>
  )
}
