import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import styles from './Layout.module.css'

const NAV = [
  { to: '/',        label: 'Dashboard', icon: '▦' },
  { to: '/records', label: 'Records',   icon: '⊟', analystOnly: true },
  { to: '/users',   label: 'Users',     icon: '⊞', adminOnly:  true },
]

export default function Layout() {
  const { user, logout, isAdmin, isAnalyst } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => { logout(); navigate('/login') }

  const visibleNav = NAV.filter(n => {
    if (n.adminOnly)   return isAdmin
    if (n.analystOnly) return isAnalyst
    return true
  })

  return (
    <div className={styles.shell}>
      {/* Sidebar */}
      <aside className={styles.sidebar}>
        <div className={styles.brand}>
          <span className={styles.brandMark}>FO</span>
          <span className={styles.brandName}>FinanceOS</span>
        </div>

        <nav className={styles.nav}>
          {visibleNav.map(n => (
            <NavLink
              key={n.to}
              to={n.to}
              end={n.to === '/'}
              className={({ isActive }) => `${styles.navItem} ${isActive ? styles.active : ''}`}
            >
              <span className={styles.navIcon}>{n.icon}</span>
              {n.label}
            </NavLink>
          ))}
        </nav>

        <div className={styles.userBlock}>
          <div className={styles.userInfo}>
            <div className={styles.userName}>{user?.name}</div>
            <div className={styles.userRole}>{user?.role}</div>
          </div>
          <button className={styles.logoutBtn} onClick={handleLogout} title="Logout">
            ⏻
          </button>
        </div>
      </aside>

      {/* Main */}
      <main className={styles.main}>
        <Outlet />
      </main>
    </div>
  )
}
