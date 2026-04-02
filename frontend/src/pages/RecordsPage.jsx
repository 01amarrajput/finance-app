import { useState, useEffect, useCallback } from 'react'
import { recordsApi } from '../api/services'
import { useAuth } from '../context/AuthContext'
import Spinner from '../components/common/Spinner'
import styles from './RecordsPage.module.css'

const fmt = n => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(n)

const EMPTY_FORM = { amount: '', type: 'INCOME', category: '', date: new Date().toISOString().slice(0,10), notes: '' }

export default function RecordsPage() {
  const { isAdmin } = useAuth()
  const [records, setRecords] = useState([])
  const [meta,    setMeta]    = useState({ totalElements: 0, totalPages: 0 })
  const [loading, setLoading] = useState(true)
  const [error,   setError]   = useState('')

  // Filters
  const [filters, setFilters] = useState({ type: '', category: '', date_from: '', date_to: '', page: 0, size: 15 })

  // Modal state
  const [modal,     setModal]     = useState(null) // null | 'create' | 'edit'
  const [editTarget,setEditTarget]= useState(null)
  const [form,      setForm]      = useState(EMPTY_FORM)
  const [saving,    setSaving]    = useState(false)
  const [formError, setFormError] = useState('')

  const load = useCallback(() => {
    setLoading(true)
    const params = {}
    if (filters.type)      params.type      = filters.type
    if (filters.category)  params.category  = filters.category
    if (filters.date_from) params.date_from = filters.date_from
    if (filters.date_to)   params.date_to   = filters.date_to
    params.page = filters.page
    params.size = filters.size

    recordsApi.list(params)
      .then(r => {
        setRecords(r.data.content)
        setMeta({ totalElements: r.data.totalElements, totalPages: r.data.totalPages })
      })
      .catch(() => setError('Failed to load records'))
      .finally(() => setLoading(false))
  }, [filters])

  useEffect(() => { load() }, [load])

  const setF = k => e => setFilters(p => ({ ...p, [k]: e.target.value, page: 0 }))
  const setFormF = k => e => setForm(p => ({ ...p, [k]: e.target.value }))

  const openCreate = () => { setForm(EMPTY_FORM); setFormError(''); setModal('create') }
  const openEdit   = r  => { setEditTarget(r); setForm({ amount: r.amount, type: r.type, category: r.category, date: r.date, notes: r.notes || '' }); setFormError(''); setModal('edit') }
  const closeModal = () => { setModal(null); setEditTarget(null) }

  const save = async e => {
    e.preventDefault()
    setSaving(true); setFormError('')
    try {
      const payload = { ...form, amount: parseFloat(form.amount) }
      if (modal === 'create') await recordsApi.create(payload)
      else                    await recordsApi.update(editTarget.id, payload)
      closeModal(); load()
    } catch (err) {
      setFormError(err.response?.data?.error || JSON.stringify(err.response?.data?.details) || 'Save failed')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async id => {
    if (!confirm('Soft-delete this record?')) return
    await recordsApi.delete(id)
    load()
  }

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <div>
          <h1 className={styles.title}>Records</h1>
          <span className={styles.subtitle}>{meta.totalElements} total entries</span>
        </div>
        {isAdmin && <button className={styles.createBtn} onClick={openCreate}>+ New Record</button>}
      </header>

      {/* Filters */}
      <div className={styles.filters}>
        <select className={styles.input} value={filters.type} onChange={setF('type')}>
          <option value="">All Types</option>
          <option value="INCOME">Income</option>
          <option value="EXPENSE">Expense</option>
        </select>
        <input className={styles.input} placeholder="Category" value={filters.category} onChange={setF('category')} />
        <input className={styles.input} type="date" value={filters.date_from} onChange={setF('date_from')} />
        <input className={styles.input} type="date" value={filters.date_to}   onChange={setF('date_to')} />
        <button className={styles.resetBtn} onClick={() => setFilters({ type: '', category: '', date_from: '', date_to: '', page: 0, size: 15 })}>Reset</button>
      </div>

      {/* Table */}
      {loading ? <Spinner /> : error ? <div className={styles.error}>{error}</div> : (
        <div className={styles.tableWrap}>
          <div className={styles.thead}>
            <span>Date</span><span>Category</span><span>Type</span><span>Amount</span><span>Created by</span><span>Notes</span>
            {isAdmin && <span>Actions</span>}
          </div>
          {records.map(r => (
            <div key={r.id} className={styles.trow}>
              <span className={styles.mono}>{r.date}</span>
              <span>{r.category}</span>
              <span className={r.type === 'INCOME' ? styles.income : styles.expense}>{r.type}</span>
              <span className={`${styles.mono} ${r.type === 'INCOME' ? styles.income : styles.expense}`}>
                {r.type === 'INCOME' ? '+' : '-'}{fmt(r.amount)}
              </span>
              <span className={styles.muted}>{r.createdByName}</span>
              <span className={styles.muted}>{r.notes || '—'}</span>
              {isAdmin && (
                <span className={styles.actions}>
                  <button className={styles.editBtn}   onClick={() => openEdit(r)}>Edit</button>
                  <button className={styles.deleteBtn} onClick={() => handleDelete(r.id)}>Del</button>
                </span>
              )}
            </div>
          ))}
          {records.length === 0 && <div className={styles.empty}>No records found.</div>}
        </div>
      )}

      {/* Pagination */}
      <div className={styles.pagination}>
        <button disabled={filters.page === 0} onClick={() => setFilters(p => ({ ...p, page: p.page - 1 }))}>← Prev</button>
        <span>Page {filters.page + 1} of {meta.totalPages || 1}</span>
        <button disabled={filters.page >= meta.totalPages - 1} onClick={() => setFilters(p => ({ ...p, page: p.page + 1 }))}>Next →</button>
      </div>

      {/* Modal */}
      {modal && (
        <div className={styles.overlay} onClick={closeModal}>
          <div className={styles.modal} onClick={e => e.stopPropagation()}>
            <h2 className={styles.modalTitle}>{modal === 'create' ? 'New Record' : 'Edit Record'}</h2>
            {formError && <div className={styles.formError}>{formError}</div>}
            <form onSubmit={save} className={styles.form}>
              <label className={styles.label}>
                Amount
                <input required type="number" min="0.01" step="0.01" className={styles.input} value={form.amount} onChange={setFormF('amount')} />
              </label>
              <label className={styles.label}>
                Type
                <select required className={styles.input} value={form.type} onChange={setFormF('type')}>
                  <option value="INCOME">Income</option>
                  <option value="EXPENSE">Expense</option>
                </select>
              </label>
              <label className={styles.label}>
                Category
                <input required className={styles.input} value={form.category} onChange={setFormF('category')} />
              </label>
              <label className={styles.label}>
                Date
                <input required type="date" className={styles.input} value={form.date} onChange={setFormF('date')} />
              </label>
              <label className={styles.label}>
                Notes
                <textarea className={styles.textarea} value={form.notes} onChange={setFormF('notes')} rows={3} />
              </label>
              <div className={styles.modalActions}>
                <button type="button" className={styles.cancelBtn} onClick={closeModal}>Cancel</button>
                <button type="submit" className={styles.saveBtn} disabled={saving}>{saving ? 'Saving…' : 'Save'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
