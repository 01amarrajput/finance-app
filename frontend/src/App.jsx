import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Layout        from './components/layout/Layout'
import LoginPage     from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import RecordsPage   from './pages/RecordsPage'
import UsersPage     from './pages/UsersPage'
import Spinner       from './components/common/Spinner'

function ProtectedRoute({ children, adminOnly = false, analystOnly = false }) {
  const { user, loading, isAdmin, isAnalyst } = useAuth()
  if (loading) return <Spinner fullscreen />
  if (!user)   return <Navigate to="/login" replace />
  if (adminOnly   && !isAdmin)   return <Navigate to="/" replace />
  if (analystOnly && !isAnalyst) return <Navigate to="/" replace />
  return children
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={
            <ProtectedRoute><Layout /></ProtectedRoute>
          }>
            <Route index element={<DashboardPage />} />
            <Route path="records" element={
              <ProtectedRoute analystOnly><RecordsPage /></ProtectedRoute>
            } />
            <Route path="users" element={
              <ProtectedRoute adminOnly><UsersPage /></ProtectedRoute>
            } />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
