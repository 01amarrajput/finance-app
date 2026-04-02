import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import styles from './LoginPage.module.css'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate   = useNavigate()
  const [form, setForm]   = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [busy,  setBusy]  = useState(false)

  const set = k => e => setForm(p => ({ ...p, [k]: e.target.value }))

  const submit = async e => {
    e.preventDefault()
    setBusy(true); setError('')
    try {
      await login(form.email, form.password)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className={styles.page}>
      <div className={styles.card} style={{ animationDelay: '0ms' }}>
        <div className={styles.logo}>
          <span className={styles.mark}>FO</span>
          <span className={styles.name}>FinanceOS</span>
        </div>
        <h1 className={styles.heading}>Sign in</h1>
        <p className={styles.sub}>Finance Data Processing Platform</p>

        {error && <div className={styles.error}>{error}</div>}

        <form onSubmit={submit} className={styles.form}>
          <label className={styles.label}>
            Email
            <input
              type="email" required autoComplete="email"
              className={styles.input}
              value={form.email} onChange={set('email')}
              placeholder="admin@finance.com"
            />
          </label>
          <label className={styles.label}>
            Password
            <input
              type="password" required autoComplete="current-password"
              className={styles.input}
              value={form.password} onChange={set('password')}
              placeholder="••••••••"
            />
          </label>
          <button className={styles.btn} disabled={busy}>
            {busy ? 'Signing in…' : 'Sign in →'}
          </button>
        </form>

        <div className={styles.hint}>
          <strong>Demo credentials</strong><br />
          admin@finance.com / admin123<br />
          analyst@finance.com / analyst123<br />
          viewer@finance.com / viewer123
        </div>
      </div>
    </div>
  )
}
