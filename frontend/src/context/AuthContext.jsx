import { createContext, useContext, useState, useEffect } from 'react'
import api from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user,    setUser]    = useState(null)
  const [token,   setToken]   = useState(() => localStorage.getItem('token'))
  const [loading, setLoading] = useState(true)

  // On mount, try to reload the current user from /api/users/me
  useEffect(() => {
    if (!token) { setLoading(false); return }
    api.get('/users/me')
      .then(r => setUser(r.data))
      .catch(() => { localStorage.removeItem('token'); setToken(null) })
      .finally(() => setLoading(false))
  }, [token])

  const login = async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password })
    localStorage.setItem('token', data.token)
    setToken(data.token)
    setUser(data.user)
    return data.user
  }

  const register = async (payload) => {
    const { data } = await api.post('/auth/register', payload)
    localStorage.setItem('token', data.token)
    setToken(data.token)
    setUser(data.user)
    return data.user
  }

  const logout = () => {
    localStorage.removeItem('token')
    setToken(null)
    setUser(null)
  }

  const isAdmin   = user?.role === 'ADMIN'
  const isAnalyst = user?.role === 'ANALYST' || isAdmin
  const isViewer  = !!user

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout, register, isAdmin, isAnalyst, isViewer }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
