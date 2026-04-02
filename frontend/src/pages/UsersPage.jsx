import { useState, useEffect } from 'react'
import { usersApi } from '../api/services'
import { useAuth } from '../context/AuthContext'
import Spinner from '../components/common/Spinner'
import styles from './UsersPage.module.css'

const ROLES = ['VIEWER', 'ANALYST', 'ADMIN']
const STATUSES = ['ACTIVE', 'INACTIVE']

export default function UsersPage() {
  const { user: me } = useAuth()
  const [users,   setUsers]   = useState([])
  const [loading, setLoading] = useState(true)
  const [error,   setError]   = useState('')
  const [modal,   setModal]   = useState(null)  // null | { user }
  const [form,    setForm]    = useState({ name: '', role: '', status: '' })
  const [saving,  setSaving]  = useState(false)
  const [formErr, setFormErr] = useState('')

  const load = () => {
    setLoading(true)
    usersApi.list()
      .then(r => setUsers(r.data))
      .catch(() => setError('Failed to load users'))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const openEdit = u => {
    setForm({ name: u.name, role: u.role, status: u.status })
    setFormErr('')
    setModal({ user: u })
  }

  const save = async e => {
    e.preventDefault()
    setSaving(true); setFormErr('')
    const patch = {}
    if (form.name   !== modal.user.name)   patch.name   = form.name
    if (form.role   !== modal.user.role)   patch.role   = form.role
    if (form.status !== modal.user.status) patch.status = form.status
    try {
      await usersApi.update(modal.user.id, patch)
      setModal(null); load()
    } catch (err) {
      setFormErr(err.response?.data?.error || 'Update failed')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async id => {
    if (id === me.id) return alert("You can't delete yourself.")
    if (!confirm('Delete this user permanently?')) return
    await usersApi.delete(id)
    load()
  }

  const roleBadge = r => ({ VIEWER: styles.viewer, ANALYST: styles.analyst, ADMIN: styles.admin }[r] || '')

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <div>
          <h1 className={styles.title}>Users</h1>
          <span className={styles.subtitle}>{users.length} accounts</span>
        </div>
      </header>

      {loading ? <Spinner /> : error ? <div className={styles.error}>{error}</div> : (
        <div className={styles.grid}>
          {users.map(u => (
            <div key={u.id} className={styles.card}>
              <div className={styles.avatar}>{u.name[0].toUpperCase()}</div>
              <div className={styles.info}>
                <div className={styles.name}>{u.name} {u.id === me.id && <span className={styles.you}>you</span>}</div>
                <div className={styles.email}>{u.email}</div>
                <div className={styles.meta}>
                  <span className={`${styles.badge} ${roleBadge(u.role)}`}>{u.role}</span>
                  <span className={`${styles.status} ${u.status === 'ACTIVE' ? styles.active : styles.inactive}`}>
                    {u.status}
                  </span>
                </div>
                <div className={styles.joined}>Joined {u.createdAt?.slice(0,10)}</div>
              </div>
              <div className={styles.cardActions}>
                <button className={styles.editBtn} onClick={() => openEdit(u)}>Edit</button>
                {u.id !== me.id && <button className={styles.deleteBtn} onClick={() => handleDelete(u.id)}>Delete</button>}
              </div>
            </div>
          ))}
        </div>
      )}

      {modal && (
        <div className={styles.overlay} onClick={() => setModal(null)}>
          <div className={styles.modal} onClick={e => e.stopPropagation()}>
            <h2 className={styles.modalTitle}>Edit User</h2>
            <p className={styles.modalSub}>{modal.user.email}</p>
            {formErr && <div className={styles.formError}>{formErr}</div>}
            <form onSubmit={save} className={styles.form}>
              <label className={styles.label}>
                Name
                <input className={styles.input} value={form.name} onChange={e => setForm(p => ({ ...p, name: e.target.value }))} />
              </label>
              <label className={styles.label}>
                Role
                <select className={styles.input} value={form.role} onChange={e => setForm(p => ({ ...p, role: e.target.value }))}>
                  {ROLES.map(r => <option key={r} value={r}>{r}</option>)}
                </select>
              </label>
              <label className={styles.label}>
                Status
                <select className={styles.input} value={form.status} onChange={e => setForm(p => ({ ...p, status: e.target.value }))}>
                  {STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
                </select>
              </label>
              <div className={styles.modalActions}>
                <button type="button" className={styles.cancelBtn} onClick={() => setModal(null)}>Cancel</button>
                <button type="submit" className={styles.saveBtn} disabled={saving}>{saving ? 'Saving…' : 'Save'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
